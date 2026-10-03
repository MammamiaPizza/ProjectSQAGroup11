import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

public class Issue731RegressionTest
{
    @JsonSerialize(converter = InputToOutputConverter.class)
    public static class InputBean { }

    public static class PropertyContainer {
        @JsonSerialize(converter = InputToOutputConverter.class)
        public InputBean value = new InputBean();
    }

    @JsonSerialize(using = OutputSerializer.class)
    public interface ConvertedValue { }

    public static class DummyBean implements ConvertedValue { }

    public static class OutputSerializer extends JsonSerializer<ConvertedValue> {
        @Override
        public void serialize(ConvertedValue value, JsonGenerator gen,
                SerializerProvider provider) throws IOException {
            gen.writeString("converted");
        }
    }

    public static class InputToOutputConverter
        implements Converter<InputBean, ConvertedValue>
    {
        @Override
        public ConvertedValue convert(InputBean value) {
            return new DummyBean();
        }

        @Override
        public JavaType getInputType(TypeFactory typeFactory) {
            return typeFactory.constructType(InputBean.class);
        }

        @Override
        public JavaType getOutputType(TypeFactory typeFactory) {
            return typeFactory.constructType(ConvertedValue.class);
        }
    }

    @Test
    public void conversionUsesDeclaredOutputSerializerForRootValue() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals("\"converted\"", mapper.writeValueAsString(new InputBean()));
    }

    @Test
    public void conversionUsesDeclaredOutputSerializerForBeanProperty() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals("{\"value\":\"converted\"}",
                mapper.writeValueAsString(new PropertyContainer()));
    }
}
