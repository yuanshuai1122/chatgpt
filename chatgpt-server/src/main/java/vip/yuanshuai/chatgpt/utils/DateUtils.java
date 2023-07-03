package vip.yuanshuai.chatgpt.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @program: chatgpt
 * @description: DateUtils
 * @author: yuanshuai
 * @create: 2023-07-03 12:46
 **/
public class DateUtils {

    /**
     * 生成几天后时间
     *
     * @param date      日期
     * @param numOfDays 天数
     * @return {@link Date}
     */
    public static Date addDaysToDate(Date date, int numOfDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, numOfDays);
        return calendar.getTime();
    }

}
