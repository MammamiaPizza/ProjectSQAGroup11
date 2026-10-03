import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateTimeSerializerBaseBug85Test {

    @Test
    public void timezoneOnlyFormatUsesConfiguredDefaultDateFormat() throws Exception {
        ObjectMapper mapper = mapperWithDefaultFormat();

        assertEquals("{\"date\":\"1970-01-01X01:00:00\"}",
                mapper.writeValueAsString(new TimezoneOnlyDateBean(new Date(0L))));
    }

    @Test
    public void unannotatedDateUsesConfiguredDefaultDateFormat() throws Exception {
        ObjectMapper mapper = mapperWithDefaultFormat();

        assertEquals("{\"date\":\"1970-01-01X00:00:00\"}",
                mapper.writeValueAsString(new PlainDateBean(new Date(0L))));
    }

    @Test
    public void explicitPatternStillOverridesConfiguredDefaultFormat() throws Exception {
        ObjectMapper mapper = mapperWithDefaultFormat();

        assertEquals("{\"date\":\"1970/01/01 01:00\"}",
                mapper.writeValueAsString(new PatternDateBean(new Date(0L))));
    }

    @Test
    public void numericShapeStillSerializesAsTimestamp() throws Exception {
        ObjectMapper mapper = mapperWithDefaultFormat();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        assertEquals("{\"date\":0}",
                mapper.writeValueAsString(new TimestampDateBean(new Date(0L))));
    }

    private ObjectMapper mapperWithDefaultFormat() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'X'HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return new ObjectMapper().setDateFormat(format);
    }

    public static class TimezoneOnlyDateBean {
        @JsonFormat(timezone = "GMT+01:00")
        public Date date;

        public TimezoneOnlyDateBean(Date date) {
            this.date = date;
        }
    }

    public static class PlainDateBean {
        public Date date;

        public PlainDateBean(Date date) {
            this.date = date;
        }
    }

    public static class PatternDateBean {
        @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+01:00")
        public Date date;

        public PatternDateBean(Date date) {
            this.date = date;
        }
    }

    public static class TimestampDateBean {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        public Date date;

        public TimestampDateBean(Date date) {
            this.date = date;
        }
    }
}