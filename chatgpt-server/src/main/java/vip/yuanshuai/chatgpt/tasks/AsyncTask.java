package vip.yuanshuai.chatgpt.tasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import vip.yuanshuai.chatgpt.beans.ChatSuccessLog;
import vip.yuanshuai.chatgpt.beans.ChatUserKey;
import vip.yuanshuai.chatgpt.mapper.ChatSuccessLogMapper;
import vip.yuanshuai.chatgpt.mapper.ChatUserKeyMapper;

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
  private ChatUserKeyMapper chatUserKeyMapper;


  /**
   * 写入key任务
   *
   * @param chatUserKey 聊天用户密钥
   */
  @Async("logAsyncTaskPool")
  public void updateChatUserKey(ChatUserKey chatUserKey) {
    chatUserKeyMapper.updateById(chatUserKey);
  }

  @Async("logAsyncTaskPool")
  public void setChatLog(ChatSuccessLog chatSuccessLog) {
    chatSuccessLogMapper.insert(chatSuccessLog);
  }


}
