package com.chatgpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chatgpt.beans.ChatOpLog;
import com.chatgpt.beans.ChatUserKey;
import com.chatgpt.beans.ResponseResult;
import com.chatgpt.beans.dto.ChatKeyUpdateDTO;
import com.chatgpt.constants.enums.ChatKeyStatusEnum;
import com.chatgpt.constants.enums.ResultCode;
import com.chatgpt.constants.enums.UserKeyTypeEnum;
import com.chatgpt.mapper.ChatOpLogMapper;
import com.chatgpt.mapper.ChatUserKeyMapper;
import com.chatgpt.utils.DateUtils;
import com.chatgpt.utils.IPUtils;
import com.chatgpt.utils.RedisKeyUtils;
import com.chatgpt.utils.ValueUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * chat key服务
 *
 * @author: yuanshuai
 * @create: 2023-03-21 21:15
 */
@Service
@Slf4j
public class ChatKeyService {

  @Autowired
  private ChatUserKeyMapper chatUserKeyMapper;

  @Autowired
  private HttpServletRequest request;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Autowired
  private ChatOpLogMapper chatOpLogMapper;


  public ResponseResult<ChatUserKey> create() {

    int times = 0;

    // 防刷 同一ip 24小时只能生成3条key
    String value = redisTemplate.opsForValue().get(RedisKeyUtils.getFlushKey(IPUtils.getIpAddr(request)));
    if (null == value) {
      redisTemplate.opsForValue().set(RedisKeyUtils.getFlushKey(IPUtils.getIpAddr(request)), "0", 24, TimeUnit.HOURS);
    }else {
      times = Integer.parseInt(value);
      if (times >= 1) {
        return new ResponseResult<>(ResultCode.VISIT_OUT_TIMES.getCode(), ResultCode.VISIT_OUT_TIMES.getMsg());
      }
    }


    String apiKey = ValueUtils.getApiKey();
    // 免费用户一共10次提问机会
    Date date = new Date();
    ChatUserKey chatUserKey = new ChatUserKey(null, apiKey, 10, 10, ChatKeyStatusEnum.ACTIVED.getStatus(), date, DateUtils.getAfterDate3(date, 1));
    int flag = chatUserKeyMapper.insert(chatUserKey);
    if (flag <= 0 ) {
      return new ResponseResult<>(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMsg());
    }

    // 获取缓存过期时间
    Long expire = redisTemplate.getExpire(RedisKeyUtils.getFlushKey(IPUtils.getIpAddr(request)), TimeUnit.HOURS);
    if (expire == null || expire <= 0) {
      redisTemplate.opsForValue().set(RedisKeyUtils.getFlushKey(IPUtils.getIpAddr(request)), "0", 24, TimeUnit.HOURS);
    }else {
      redisTemplate.opsForValue().set(RedisKeyUtils.getFlushKey(IPUtils.getIpAddr(request)), String.valueOf(times + 1), expire, TimeUnit.HOURS);
    }


    return new ResponseResult<>(ResultCode.SUCCESS.getCode(), "生成成功", chatUserKey);
  }


  public ResponseResult<List<ChatUserKey>> info(String chatKey) {
    QueryWrapper<ChatUserKey> wrapper = new QueryWrapper<>();
    if (StringUtils.isNotBlank(chatKey)) {
      wrapper.lambda().eq(ChatUserKey::getUserKey, chatKey);
    }

    List<ChatUserKey> chatUserKeys = chatUserKeyMapper.selectList(wrapper);

    return new ResponseResult<>(ResultCode.SUCCESS.getCode(), "获取成功", chatUserKeys);

  }


  public ResponseResult<ChatUserKey> updateKeyInfo(ChatKeyUpdateDTO dto) {

    String keyType = "";
    String activeStatus = "";

    QueryWrapper<ChatUserKey> wrapper = new QueryWrapper<>();
    wrapper.lambda().eq(ChatUserKey::getUserKey, dto.getUserKey());

    ChatUserKey chatUserKey = chatUserKeyMapper.selectOne(wrapper);
    if (null == chatUserKey) {
      return new ResponseResult<>(ResultCode.KEY_NOT_FOUND.getCode(), ResultCode.KEY_NOT_FOUND.getMsg(), null);
    }

    // 激活状态不为空
    if (StringUtils.isNotBlank(dto.getActiveStatus())) {
      activeStatus = dto.getActiveStatus();
      if (!ChatKeyStatusEnum.ACTIVED.getStatus().equals(dto.getActiveStatus()) && !ChatKeyStatusEnum.NOACTIVE.getStatus().equals(dto.getActiveStatus())) {
        return new ResponseResult<>(ResultCode.PARAM_IS_INVAlID.getCode(), ResultCode.PARAM_IS_INVAlID.getMsg(), null);
      }

      chatUserKey.setActiveStatus(dto.getActiveStatus());
      }

    // 付费类型不为空
    if (StringUtils.isNotBlank(dto.getKeyType())) {
      keyType = dto.getKeyType();
      if (dto.getKeyType().equals(UserKeyTypeEnum.Try.getType())) {
        chatUserKey.setRemainTimes(chatUserKey.getRemainTimes() + UserKeyTypeEnum.Try.getTimes());
        chatUserKey.setTotalTimes(chatUserKey.getTotalTimes() + UserKeyTypeEnum.Try.getTimes());
        chatUserKey.setExpireTime(DateUtils.getAfterDate15(chatUserKey.getExpireTime(), 1));
      }else if (dto.getKeyType().equals(UserKeyTypeEnum.Month.getType())) {
        chatUserKey.setRemainTimes(chatUserKey.getRemainTimes() + UserKeyTypeEnum.Month.getTimes());
        chatUserKey.setTotalTimes(chatUserKey.getTotalTimes() + UserKeyTypeEnum.Month.getTimes());
        chatUserKey.setExpireTime(DateUtils.getAfterDate15(chatUserKey.getExpireTime(), 2));
      }else {
        return new ResponseResult<>(ResultCode.PARAM_IS_INVAlID.getCode(), ResultCode.PARAM_IS_INVAlID.getMsg(), null);
      }
    }

    if (StringUtils.isBlank(dto.getKeyType()) && StringUtils.isBlank(dto.getActiveStatus())) {
      return new ResponseResult<>(ResultCode.SUCCESS.getCode(), "更新key成功", chatUserKey);
    }

    int flag = chatUserKeyMapper.updateById(chatUserKey);
    if (flag <= 0) {
      return new ResponseResult<>(ResultCode.KEY_NOT_UPDATE.getCode(), ResultCode.KEY_NOT_UPDATE.getMsg(), null);
    }

    // 插入操作日志
    ChatOpLog chatOpLog = new ChatOpLog(null, IPUtils.getIpAddr(request), dto.getUserKey(), keyType, activeStatus, new Date());
    chatOpLogMapper.insert(chatOpLog);

    return new ResponseResult<>(ResultCode.SUCCESS.getCode(), "更新key成功", chatUserKey);

  }
}
