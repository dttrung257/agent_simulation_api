package com.uet.agent_simulation_api.utils;

import com.uet.agent_simulation_api.constant.Const;
import com.uet.agent_simulation_api.constant.TimeConst;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TimeUtil {
    /**
     * This method is used to get current time in milliseconds.
     *
     * @return current time in milliseconds
     */
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * This method is used to get current time string in default format and default time zone.
     *
     * @return String of current time in default format and default time zone.
     */
    public String getCurrentTimeString() {
        return getCurrentTimeString(TimeConst.DEFAULT_DATE_TIME_FORMAT, TimeConst.DEFAULT_TIME_ZONE);
    }

    /**
     * This method is used to get current time string in specific format.
     *
     * @param format - specific format
     * @return String of current time in specific format.
     */
    public String getCurrentTimeString(String format) {
        return getCurrentTimeString(format, TimeConst.DEFAULT_TIME_ZONE);
    }

    /**
     * This method is used to get current time string in specific format and specific time zone.
     *
     * @param format - specific format
     * @param zoneId - specific time zone
     * @return String of current time in specific format and specific time zone.
     */
    public String getCurrentTimeString(String format, String zoneId) {
        ZonedDateTime now = Instant.now().atZone(ZoneId.of(zoneId));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        return now.format(formatter);
    }
}
