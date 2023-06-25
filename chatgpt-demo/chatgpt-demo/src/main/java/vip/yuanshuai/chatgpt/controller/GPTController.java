package vip.yuanshuai.chatgpt.controller;

import org.mvnsearch.chatgpt.model.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.ChatCompletionResponse;
import org.mvnsearch.chatgpt.spring.service.ChatGPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @program: chatgpt-demo
 * @description: chatgpt控制器
 * @author: yuanshuai
 * @create: 2023-06-25 11:39
 **/
@RestController
public class GPTController {


    @Autowired
    private ChatGPTService chatGPTService;

    @PostMapping("/chat")
    public Mono<String> chat(@RequestBody String content) {
        return chatGPTService.chat(ChatCompletionRequest.of(content))
                .map(ChatCompletionResponse::getReplyText);
    }

    @GetMapping("/stream-chat")
    public Flux<String> streamChat(@RequestParam String content) {
        return chatGPTService.stream(ChatCompletionRequest.of(content))
                .map(ChatCompletionResponse::getReplyText);
    }

}
