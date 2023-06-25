package com.chatgpt.controller;

import com.chatgpt.beans.ResponseResult;
import com.chatgpt.beans.dto.ImagesCreateDTO;
import com.chatgpt.beans.dto.chatProcess.ChatProcess;
import com.chatgpt.beans.vo.ImagesUrlCreateVO;
import com.chatgpt.service.ChatApiService;
import com.chatgpt.service.ImagesApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;

/**
 * chatgpt的api版控制器
 *
 * @author: yuanshuai
 * @create: 2023-03-06 11:58
 */
@RestController
@Slf4j
@RequestMapping("/chat")
public class ChatApiController {

  private final ChatApiService chatApiService;

  private final ImagesApiService imagesApiService;

  public ChatApiController(ChatApiService chatApiService, ImagesApiService imagesApiService) {
    this.chatApiService = chatApiService;
    this.imagesApiService = imagesApiService;
  }

  //@PostMapping("/chat")
  //public ResponseResult<ChatResult> chatCommon(@RequestBody ChatCommonDTO dto) {
  //  log.info("开始请求chat：{}", dto);
  //  return chatApiService.chatCommon(dto);
  //}

  @PostMapping("/sign")
  public ResponseResult<String> chatSign(@RequestBody @Valid ChatProcess process) {
    log.info("开始请求chatsign：{}", process);
    return chatApiService.chatSign(process);
  }

  @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter chatStream(@RequestParam("signKey") String signKey) {
    log.info("开始请求chat(Stream版), key: {}", signKey);
    return chatApiService.chatStream(signKey);
  }

  @PostMapping("/images/create")
  public ResponseResult<ImagesUrlCreateVO> imagesCreate(@RequestBody @Valid ImagesCreateDTO dto) {
      log.info("开始请求图片生成， dto: {}", dto);
      return imagesApiService.imagesCreate(dto);
  }


}
