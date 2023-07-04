package vip.yuanshuai.chatgptapi.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求工具类
 *
 * @author: aabb
 * @create: 2023-03-23 22:59
 */
@Slf4j
public class RequestUtils {

  /**
   * 生成请求头
   * @param key chat秘钥
   * @return 请求头
   */
  public static Map<String, String> buildRequestHeaders(String key) {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("Authorization", "Bearer " + key);
    headers.put("Accept", "text/event-stream");
    return headers;
  }


  /**
   * 构建请求参数
   *
   * @param value 价值
   * @return {@link Map}<{@link String}, {@link Object}>
   */
  public static Map<String, Object> buildRequestParams(String value) {
    Map<String, Object> data = new HashMap<>();
    data.put("model", "gpt-4-0613");
    data.put("stream", true);
    data.put("top_p", 1);
    data.put("temperature", 1);
    data.put("n", 1);
    data.put("presence_penalty", 0);
    data.put("frequency_penalty", 0);
    //data.put("max_tokens", 1);
    return data;
  }

}
