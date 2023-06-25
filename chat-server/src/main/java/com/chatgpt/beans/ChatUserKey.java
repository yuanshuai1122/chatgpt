package com.chatgpt.beans;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * chat key
 *
 * @author: yuanshuai
 * @create: 2023-03-21 21:18
 */
@Getter
@Setter
@AllArgsConstructor
@NotBlank
@ToString
public class ChatUserKey {

  /**
   * 主键id
   */
  @TableId(type = IdType.AUTO)
  private Integer id;

  /**
   * 用户的key
   */
  private String userKey;

  /**
   * 剩余次数
   */
  private Integer remainTimes;

  /**
   * 总次数
   */
  private Integer totalTimes;

  /**
   * 激活状态
   */
  private String activeStatus;

  /**
   * 创建时间
   */
  private Date createTime;

  /**
   * 过期时间
   */
  private Date expireTime;

}
