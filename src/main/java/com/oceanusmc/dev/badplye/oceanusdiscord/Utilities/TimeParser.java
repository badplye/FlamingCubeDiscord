package com.oceanusmc.dev.badplye.oceanusdiscord.Utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {
    public static long parse(String input) {
        long duration = 0;
        String rawDuration = input;
        if (rawDuration.contains("d") || rawDuration.contains("h") || rawDuration.contains("m") || rawDuration.contains("s")) {
            Pattern pattern = Pattern.compile("(\\d+)([dhms])");
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                long value = Long.parseLong(matcher.group(1));
                char unit = matcher.group(2).charAt(0);
                switch (unit) {
                    case 'd':
                        duration += value * 86400;
                        break;
                    case 'h':
                        duration += value * 3600;
                        break;
                    case 'm':
                        duration += value * 60;
                        break;
                    case 's':
                        duration += value;
                        break;
                }
            }
        }
        return duration;
    }
}
