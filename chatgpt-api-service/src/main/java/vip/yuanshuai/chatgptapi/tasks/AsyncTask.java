package vip.yuanshuai.chatgptapi.tasks;

import vip.yuanshuai.chatgptapi.beans.dto.entity.ChatFailureLog;
import vip.yuanshuai.chatgptapi.beans.dto.entity.ChatSensitiveLog;
import vip.yuanshuai.chatgptapi.beans.dto.entity.ChatSuccessLog;
import vip.yuanshuai.chatgptapi.mapper.ChatFailureLogMapper;
import vip.yuanshuai.chatgptapi.mapper.ChatSensitiveLogMapper;
import vip.yuanshuai.chatgptapi.mapper.ChatSuccessLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 异步任务
 *
 * @author: aabb
 * @create: 2023-03-15 18:25
 */
@Component
@Slf4j
public class AsyncTask {

  @Autowired
  private ChatSuccessLogMapper chatSuccessLogMapper;

  @Autowired
  private ChatSensitiveLogMapper chatSensitiveLogMapper;

  @Autowired
  private ChatFailureLogMapper chatFailureLogMapper;


  /**
   * 写入chatLog任务
   * @param chatSuccessLog chatLog实体
   */
  @Async("logAsyncTaskPool")
  public void setChatLog(ChatSuccessLog chatSuccessLog) {
    chatSuccessLogMapper.insert(chatSuccessLog);
  }

  /**
   * 写入敏感词日志任务
   * @param chatSensitiveLog chatLog实体
   */
  @Async("logAsyncTaskPool")
  public void setChatSensitiveLog(ChatSensitiveLog chatSensitiveLog) {
    chatSensitiveLogMapper.insert(chatSensitiveLog);
  }

  /**
   * 写入失败日志任务
   * @param failure 失败日志实体
   */
  @Async("logAsyncTaskPool")
  public void setChatLogFailure(ChatFailureLog failure) {
    chatFailureLogMapper.insert(failure);
  }


}
