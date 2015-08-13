package formatters;

import org.joda.time.DateTime;
import org.junit.Test;

import java.math.BigDecimal;

import static org.fest.assertions.Assertions.assertThat;

public class ScoreFormatterTest {

    @Test
    public void should_format_as_timer_zero() {
        String format = ScoreFormatter.formatAsTime(BigDecimal.ZERO);
        assertThat(format).isEqualTo("0'00\"00");
    }

    @Test
    public void should_format_as_timer() {
        DateTime dateTime = new DateTime().withMinuteOfHour(15).withSecondOfMinute(56).withMillisOfSecond(12);
        BigDecimal score = BigDecimal.valueOf(dateTime.toDate().getTime());

        String format = ScoreFormatter.formatAsTime(score);
        assertThat(format).isEqualTo("15'56\"12");
    }

}