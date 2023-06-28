package vip.yuanshuai.chatgpt.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vip.yuanshuai.chatgpt.beans.ChatStream.ChatStream;
import vip.yuanshuai.chatgpt.beans.ChatSuccessLog;
import vip.yuanshuai.chatgpt.beans.ResponseResult;
import vip.yuanshuai.chatgpt.enums.ChatRoleEnum;
import vip.yuanshuai.chatgpt.enums.ResultCode;
import vip.yuanshuai.chatgpt.tasks.AsyncTask;
import vip.yuanshuai.chatgpt.utils.ValueUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
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


  /**
   * chat签名
   *
   * @param dto DTO
   * @return {@link ResponseResult}<{@link String}>
   */
  public ResponseResult<String> chatSign(ChatStream dto) {
    // 加密key
    String signKey = ValueUtils.getUUID();
    // 写日志数据库
    ChatSuccessLog chatSuccessLog = new ChatSuccessLog(null, 999, ChatRoleEnum.USER.getRole(), ValueUtils.getMessageUUID(), signKey, new Gson().toJson(dto.getPrompt()), new Date());
    asyncTask.setChatLog(chatSuccessLog);
    // 放入缓存队列
    redisTemplate.opsForValue().set(signKey, new Gson().toJson(chatSuccessLog), 50, TimeUnit.MINUTES);
    // 返回key
    log.info("请求chatSign结束, 返回值key: {}", signKey);
    return new ResponseResult<>(ResultCode.SUCCESS.getCode(), "获取成功", signKey);
  }

}
