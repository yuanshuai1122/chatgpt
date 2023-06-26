package vip.yuanshuai.chatgpt.beans.ChatStream;

import lombok.Data;

import java.util.List;

/**
 * chat流式dto
 *
 * @author: aabb
 * @create: 2023-03-06 17:31
 */
@Data
public class ChatStream {

  /**
   * 聊天key
   */
  private String chatKey;

  /**
   * 提示词列表
   */
  private List<ChatPrompt> prompt;

}
