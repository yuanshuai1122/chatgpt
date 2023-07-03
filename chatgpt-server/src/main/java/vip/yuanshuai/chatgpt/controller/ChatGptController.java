package vip.yuanshuai.chatgpt.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.yuanshuai.chatgpt.beans.ChatStream.ChatStream;
import vip.yuanshuai.chatgpt.beans.ResponseResult;
import vip.yuanshuai.chatgpt.enums.ResultCode;
import vip.yuanshuai.chatgpt.service.ChatGptService;

import javax.validation.Valid;

/**
 * @program: chatgpt
 * @description: chatgpt控制器
 * @author: yuanshuai
 * @create: 2023-06-25 12:23
 **/
@RestController
@Slf4j
@RequestMapping("/chat")
public class ChatGptController {

    private final ChatGptService chatService;

    public ChatGptController(ChatGptService chatService) {
        this.chatService = chatService;
    }


    /**
     * chat推流签名
     *
     * @param dto DTO
     * @return {@link ResponseResult}<{@link String}>
     */
    @PostMapping("/sign")
    public ResponseResult<String> chatStreamSign(@RequestBody ChatStream dto) {
        log.info("开始请求chatsign：{}", dto);
        if (StringUtils.isBlank(dto.getChatKey())) {
            return new ResponseResult<>(ResultCode.PARAM_IS_BLANK.getCode(), "用户key为空，请联系管理员申请");
        }
        return chatService.chatSign(dto);
    }

}
