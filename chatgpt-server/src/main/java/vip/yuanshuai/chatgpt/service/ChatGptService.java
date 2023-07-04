package vip.yuanshuai.chatgpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vip.yuanshuai.chatgpt.beans.ChatStream.ChatStream;
import vip.yuanshuai.chatgpt.beans.ChatSuccessLog;
import vip.yuanshuai.chatgpt.beans.ChatUserKey;
import vip.yuanshuai.chatgpt.beans.ResponseResult;
import vip.yuanshuai.chatgpt.enums.ChatRoleEnum;
import vip.yuanshuai.chatgpt.enums.KeyStatusEnum;
import vip.yuanshuai.chatgpt.enums.ResultCode;
import vip.yuanshuai.chatgpt.mapper.ChatUserKeyMapper;
import vip.yuanshuai.chatgpt.tasks.AsyncTask;
import vip.yuanshuai.chatgpt.utils.ValueUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * chatGPT api版本服务
 *
 * @author: aabb
 * @create: 2023-03-08 16:55
 */
@Service
@Slf4j
public class ChatGptService {
  
  @Autowired
  private AsyncTask asyncTask;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Autowired
  private HttpServletRequest request;

  @Autowired
  private ChatUserKeyMapper chatUserKeyMapper;


  /**
   * chat签名
   *
   * @param dto DTO
   * @return {@link ResponseResult}<{@link String}>
   */
  public ResponseResult<String> chatSign(ChatStream dto) {
    // 获取缓存中的key
    String value = redisTemplate.opsForValue().get(dto.getChatKey());
    ChatUserKey chatUserKey;
    if (StringUtils.isNotBlank(value)) {
      chatUserKey = new Gson().fromJson(value, ChatUserKey.class);
    }else {
      QueryWrapper<ChatUserKey> wrapper = new QueryWrapper<>();
      wrapper.eq("user_key", dto.getChatKey());
      chatUserKey = chatUserKeyMapper.selectOne(wrapper);
    }
    if (null == chatUserKey.getId()) {
      return new ResponseResult<>(ResultCode.PARAM_IS_BLANK.getCode(), "key不存在");
    }
    // 验证激活
    if (!chatUserKey.getActiveStatus().equals(KeyStatusEnum.NORMAL.getStatus())) {
      return new ResponseResult<>(ResultCode.PARAM_IS_BLANK.getCode(), "key未激活");
    }
    // 验证过期
    if (chatUserKey.getExpireTime().compareTo(new Date()) < 0) {
      return new ResponseResult<>(ResultCode.PARAM_IS_BLANK.getCode(), "key已过期");
    }
    // 验证次数
    if (chatUserKey.getTotalTimes() <= 0 || chatUserKey.getRemainTimes() <= 0) {
      return new ResponseResult<>(ResultCode.PARAM_IS_BLANK.getCode(), "key的提问次数没啦");
    }
    // 次数减1
    chatUserKey.setRemainTimes(chatUserKey.getRemainTimes() - 1);
    // 写入缓存
    redisTemplate.opsForValue().set(chatUserKey.getUserKey(), new Gson().toJson(chatUserKey));
    // 更新数据库
    asyncTask.updateChatUserKey(chatUserKey);
    // 加密key
    String signKey = ValueUtils.getUUID();
    // 写日志数据库
    ChatSuccessLog chatSuccessLog = new ChatSuccessLog(null, chatUserKey.getId(), ChatRoleEnum.USER.getRole(), ValueUtils.getMessageUUID(), signKey, new Gson().toJson(dto.getPrompt()), new Date());
    asyncTask.setChatLog(chatSuccessLog);
    // 放入缓存队列
    redisTemplate.opsForValue().set(signKey, new Gson().toJson(chatSuccessLog), 50, TimeUnit.MINUTES);
    // 返回key
    log.info("请求chatSign结束, 返回值key: {}", signKey);
    return new ResponseResult<>(ResultCode.SUCCESS.getCode(), "获取成功", signKey);
  }

}
