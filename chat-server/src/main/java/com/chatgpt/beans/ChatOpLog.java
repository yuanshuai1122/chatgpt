package com.chatgpt.beans;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.util.Date;

/**
 * 操作日志
 *
 * @author: yuanshuai
 * @create: 2023-03-27 12:54
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatOpLog {

  /**
   * 主键id
   */
  @TableId(type = IdType.AUTO)
  private Integer id;

  /**
   * 操作人ip
   */
  private String opIp;

  /**
   * 被操作的key
   */
  private String userKey;

  /**
   * 操作类型
   */
  private String opType;

  /**
   * 激活操作
   */
  private String active;

  /**
   * 创建时间
   */
  private Date createTime;

}
