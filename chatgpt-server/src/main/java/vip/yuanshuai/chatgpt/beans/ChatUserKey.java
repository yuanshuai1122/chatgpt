package vip.yuanshuai.chatgpt.beans;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.util.Date;

/**
 * @program: chatgpt
 * @description: 用户key实体
 * @author: yuanshuai
 * @create: 2023-07-03 12:30
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatUserKey {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 用户key
     */
    private String userKey;

    /**
     * IP
     */
    private String ip;

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
