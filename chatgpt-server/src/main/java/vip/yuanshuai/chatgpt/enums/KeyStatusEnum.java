package vip.yuanshuai.chatgpt.enums;

import lombok.Getter;

/**
 * chat角色枚举
 *
 * @author: aabb
 * @create: 2023-03-14 12:09
 */
@Getter
public enum KeyStatusEnum {

  /**
   * 用户
   */
  NORMAL("normal", "正常"),

  /**
   * chatgpt
   */
  DELETED("deleted", "已删除")

          ;



  private String status;

  private String des;

  private KeyStatusEnum() {

  }

  KeyStatusEnum(String status, String des) {
    this.status = status;
    this.des = des;
  }

}
