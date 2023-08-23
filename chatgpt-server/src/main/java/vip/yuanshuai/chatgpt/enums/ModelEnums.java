package vip.yuanshuai.chatgpt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: chatgpt
 * @description: 模型枚举
 * @author: yuanshuai
 * @create: 2023-08-23 18:11
 **/
@Getter
@AllArgsConstructor
public enum ModelEnums {


    /**
     * 其他
     */
    GPT_VERSION_3_5("gpt-3.5", "gpt-3.5-turbo-0613"),

    /**
     * 天润融通
     */
    GPT_VERSION_4("gpt-4", "gpt-4-0613"),


    ;


    /**
     * 名称
     */
    private String name;

    /**
     * 模型
     */
    private String model;


    /**
     * 全量数据枚举map
     */

    private static final Map<String, ModelEnums> DATA_MAP = Arrays.stream
            (ModelEnums.values()).collect(Collectors.toMap(ModelEnums::getName, e -> e));


    /**
     * 搜索
     *
     * @param name 名字
     * @return {@link ModelEnums}
     */
    public static ModelEnums search(String name) {
        // 默认返回3.5
        if (StringUtils.isBlank(name)) {
            return GPT_VERSION_3_5;
        }
        return DATA_MAP.get(name);
    }

}
