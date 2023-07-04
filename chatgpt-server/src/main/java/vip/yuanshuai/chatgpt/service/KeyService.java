package vip.yuanshuai.chatgpt.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vip.yuanshuai.chatgpt.beans.ChatUserKey;
import vip.yuanshuai.chatgpt.beans.ResponseResult;
import vip.yuanshuai.chatgpt.enums.KeyStatusEnum;
import vip.yuanshuai.chatgpt.enums.ResultCode;
import vip.yuanshuai.chatgpt.mapper.ChatUserKeyMapper;
import vip.yuanshuai.chatgpt.utils.DateUtils;
import vip.yuanshuai.chatgpt.utils.IPUtils;
import vip.yuanshuai.chatgpt.utils.ValueUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @program: chatgpt
 * @description: key服务
 * @author: yuanshuai
 * @create: 2023-06-26 12:27
 **/
@Service
@Slf4j
public class KeyService {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ChatUserKeyMapper chatUserKeyMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 创建key
     *
     * @return {@link ResponseResult}<{@link String}>
     */
    public ResponseResult<String> keyCreate() {
        String ip = IPUtils.getClientIP(request);
        log.info("开始申请key，ip:{}", ip);
        boolean flag = false;
        String key = "IP-LIMIT_".concat(ip);
        String value = redisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(value)) {
            redisTemplate.opsForValue().set(key, String.valueOf(0), 24, TimeUnit.HOURS);
        }else {
            Long expire = redisTemplate.getExpire(key, TimeUnit.HOURS);
            if (expire == null || expire <= 0) {
                expire = 24L;
            }
            int times = Integer.parseInt(value);
            if (times >= 3) {
                log.info("ip申请key次数超过限制,ip:{}", ip);
                return new ResponseResult<>(ResultCode.ACCESS_FREQUENT.getCode(), ResultCode.ACCESS_FREQUENT.getMsg());
            }
            redisTemplate.opsForValue().set(key, String.valueOf(++times), expire, TimeUnit.HOURS);
        }
        String userKey = ValueUtils.getUserKey();
        ChatUserKey chatUserKey = new ChatUserKey(null, userKey, ip, 10, 10, KeyStatusEnum.NORMAL.getStatus(), new Date(), DateUtils.addDaysToDate(new Date(), 3));
        chatUserKeyMapper.insert(chatUserKey);
        // 放入缓存 3天
        redisTemplate.opsForValue().set(userKey, new Gson().toJson(chatUserKey), 3, TimeUnit.DAYS);
        log.info("获取key成功，ip:{}, userKey:{}", ip, userKey);
        return new ResponseResult<>(ResultCode.SUCCESS.getCode(), "获取成功", userKey);
    }
}
