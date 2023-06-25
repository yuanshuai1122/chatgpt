package vip.yuanshuai.chatgptapi.controller;

import vip.yuanshuai.chatgptapi.beans.dto.ImagesCreateDTO;
import vip.yuanshuai.chatgptapi.beans.dto.JourneyCreateDTO;
import vip.yuanshuai.chatgptapi.beans.dto.entity.ResponseResult;
import vip.yuanshuai.chatgptapi.beans.dto.vo.ImagesUrlCreateVO;
import vip.yuanshuai.chatgptapi.beans.dto.vo.journey.JourneyCreateVO;
import vip.yuanshuai.chatgptapi.beans.dto.vo.journey.JourneyResultVO;
import vip.yuanshuai.chatgptapi.config.sign.Signature;
import vip.yuanshuai.chatgptapi.service.ImagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 图片控制器
 *
 * @author: aabb
 * @create: 2023-04-04 20:45
 */
@RestController
@Slf4j
@RequestMapping("/images")
public class ImagesController {

  @Autowired
  private ImagesService imagesService;


  @PostMapping("/journey/create")
  @Signature
  public ResponseResult<JourneyCreateVO> journeyCreateDTO(@RequestBody @Valid JourneyCreateDTO dto) {
    log.info("开始请求图片生成， dto: {}", dto);
    return imagesService.journeyCreate(dto);
  }

  @GetMapping("/journey/create")
  @Signature
  public ResponseResult<JourneyResultVO> journeyCreateDTO(@RequestParam("id") String id) {

    return imagesService.getJourney(id);
  }


  @PostMapping("/gpt/create")
  @Signature
  public ResponseResult<ImagesUrlCreateVO> imagesCreate(@RequestBody @Valid ImagesCreateDTO dto) {
    log.info("开始请求图片生成， dto: {}", dto);
    return imagesService.imagesCreate(dto);
  }

}
