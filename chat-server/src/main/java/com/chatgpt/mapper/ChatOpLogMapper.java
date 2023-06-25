package com.chatgpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chatgpt.beans.ChatOpLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志mapper
 *
 * @author: yuanshuai
 * @create: 2023-03-27 12:57
 */
@Mapper
public interface ChatOpLogMapper extends BaseMapper<ChatOpLog> {
}
