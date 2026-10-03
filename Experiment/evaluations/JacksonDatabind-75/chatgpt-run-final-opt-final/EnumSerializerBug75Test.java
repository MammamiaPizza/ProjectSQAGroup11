import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

public class EnumSerializerBug75Test {

    enum Color {
        RED,
        BLUE,
        GREEN
    }

    static class NumericColorBean {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        public Color color;

        NumericColorBean(Color color) {
            this.color = color;
        }
    }

    static class DefaultColorBean {
        public Color color;

        DefaultColorBean(Color color) {
            this.color = color;
        }
    }

    static class NumericBoundaryBean {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        public Color first;

        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        public Color last;

        NumericBoundaryBean(Color first, Color last) {
            this.first = first;
            this.last = last;
        }
    }

    static class StringColorBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public Color color;

        StringColorBean(Color color) {
            this.color = color;
        }
    }

    @Test
    public void propertyAnnotatedAsNumberUsesEnumOrdinal() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals("{\"color\":2}", mapper.writeValueAsString(new NumericColorBean(Color.GREEN)));
    }

    @Test
    public void numericPropertyShapeUsesOrdinalAtBothEnumBoundaries() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals("{\"first\":0,\"last\":2}",
                mapper.writeValueAsString(new NumericBoundaryBean(Color.RED, Color.GREEN)));
    }

    @Test
    public void defaultEnumPropertySerializationUsesName() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals("{\"color\":\"GREEN\"}",
                mapper.writeValueAsString(new DefaultColorBean(Color.GREEN)));
    }

    @Test
    public void stringPropertyShapeOverridesGlobalEnumIndexFeature() throws Exception {
        ObjectMapper mapper = new ObjectMapper()
                .enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);

        assertEquals("{\"color\":\"GREEN\"}",
                mapper.writeValueAsString(new StringColorBean(Color.GREEN)));
    }
}
