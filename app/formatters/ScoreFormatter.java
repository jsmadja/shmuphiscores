package formatters;

import java.math.BigDecimal;

public class ScoreFormatter {

    public static String format(BigDecimal value) {
        String score = value.toString();
        return format(score);
    }

    public static String format(Long value) {
        String score = value.toString();
        return format(score);
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

}
