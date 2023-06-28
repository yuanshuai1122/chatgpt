package vip.yuanshuai.chatgpt.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vip.yuanshuai.chatgpt.beans.ChatStream.ChatStream;
import vip.yuanshuai.chatgpt.beans.ResponseResult;
import vip.yuanshuai.chatgpt.service.KeyService;

/**
 * @program: chatgpt
 * @description: key控制器
 * @author: yuanshuai
 * @create: 2023-06-26 12:26
 **/
@RestController
@RequestMapping("/key")
@Slf4j
public class KeyController {

    @Autowired
    private KeyService keyService;


    /**
     * 创建key
     *
     * @return {@link ResponseResult}<{@link String}>
     */
    @GetMapping("/create")
    public ResponseResult<String> keyCreate() {
        log.info("开始生成key");
        return keyService.keyCreate();
    }

}
