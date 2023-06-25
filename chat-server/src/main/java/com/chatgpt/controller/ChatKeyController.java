package com.chatgpt.controller;

import com.chatgpt.beans.ChatUserKey;
import com.chatgpt.beans.ResponseResult;
import com.chatgpt.beans.dto.ChatKeyUpdateDTO;
import com.chatgpt.constants.enums.ResultCode;
import com.chatgpt.service.ChatKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * chatKey控制器
 *
 * @author: yuanshuai
 * @create: 2023-03-21 21:13
 */
@RestController
@Slf4j
@RequestMapping("/key")
public class ChatKeyController {

  @Autowired
  private ChatKeyService chatKeyService;

  @GetMapping("/create")
  public ResponseResult<ChatUserKey> createKey() {

    return chatKeyService.create();
  }

  @GetMapping("/info")
  public ResponseResult<List<ChatUserKey>> createKeyInfo(@RequestParam(value = "chatKey", required = false) String chatKey) {

    return chatKeyService.info(chatKey);
  }

  @PostMapping ("/update")
  public ResponseResult<ChatUserKey> updateKeyInfo(@RequestBody @Valid ChatKeyUpdateDTO dto) {

    return chatKeyService.updateKeyInfo(dto);
  }

}
