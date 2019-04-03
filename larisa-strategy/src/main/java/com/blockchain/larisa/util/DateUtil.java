package com.blockchain.larisa.util;


import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;

public class DateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

    public static String format(Date date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime res = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return formatter.format(res);
    }

    public static boolean skip() {
        String[] date = StringUtils.split(DateUtil.format(new Date()), " ");
        String hms = date[1];
        String ms = hms.substring(hms.indexOf(":") + 1);

        //在这些时间点拉k线程序会跑飞, 操蛋的火币
        Set<String> skipTime = Sets.newHashSet();
        skipTime.add("14:59");skipTime.add("15:00");skipTime.add("15:01");
        skipTime.add("29:59");skipTime.add("30:00");skipTime.add("30:01");
        skipTime.add("44:59");skipTime.add("45:00");skipTime.add("45:01");
        skipTime.add("59:59");skipTime.add("00:00");skipTime.add("00:01");

        return skipTime.contains(ms);
    }

    public static void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (Exception e) {
            LOGGER.error("sleep exception:{}", e);
        }
    }
}
