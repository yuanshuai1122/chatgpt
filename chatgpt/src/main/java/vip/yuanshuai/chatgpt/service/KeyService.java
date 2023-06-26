package vip.yuanshuai.chatgpt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vip.yuanshuai.chatgpt.beans.ResponseResult;
import vip.yuanshuai.chatgpt.enums.ResultCode;

/**
 * @program: chatgpt
 * @description: key服务
 * @author: yuanshuai
 * @create: 2023-06-26 12:27
 **/
@Service
@Slf4j
public class KeyService {
    /**
     * 创建key
     *
     * @return {@link ResponseResult}<{@link String}>
     */
    public ResponseResult<String> keyCreate() {

        return new ResponseResult<>(ResultCode.SUCCESS.getCode(), "获取成功", "xxxxxxx");
    }
}
