package formatters;

import org.joda.time.DateTime;

import java.math.BigDecimal;

import static org.apache.commons.lang3.StringUtils.leftPad;

public class ScoreFormatter {

    public static String format(BigDecimal value) {
        if(value != null) {
            String score = value.toString();
            return format(score);
        }
        return "Invalid";
    }

    private static String format(String score) {
        StringBuilder sb = new StringBuilder();
        int chara = 0;
        for (int i = score.length() - 1; i >= 0; i--) {
            if (chara % 3 == 0 && chara != 0) {
                sb.append(".");
            }
            sb.append(score.charAt(i));
            chara++;
        }
        score = sb.reverse().toString();
        return score;
    }

    public static String formatAsTime(BigDecimal score) {
        DateTime time = new DateTime(score.longValue());
        return time.getMinuteOfHour() + "'" + leftPad(Integer.toString(time.getSecondOfMinute()), 2, '0') + "\"" + leftPad(Integer.toString(time.getMillisOfSecond()), 2, '0');
    }
}
