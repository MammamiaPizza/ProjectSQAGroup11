import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

public class TypeDeserializerBaseBug51Test
{
    @Test
    public void customTypeIdResolverMustDeserializeObjectPropertyAsResolvedSubtype()
            throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        Container result = mapper.readValue(
                "{\"options\":{\"type\":\"poly1\",\"name\":\"custom value\",\"number\":13}}",
                Container.class);

        assertTrue(result.options instanceof Poly1Base);
        Poly1Base value = (Poly1Base) result.options;
        assertEquals("custom value", value.name);
        assertEquals(13, value.number);
    }

    public static class Container {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM,
                include = JsonTypeInfo.As.PROPERTY,
                property = "type")
        @JsonTypeIdResolver(PolyResolver.class)
        public Object options;
    }

    public static class Poly1Base {
        public String name;
        public int number;
    }

    public static class PolyResolver extends TypeIdResolverBase {
        @Override
        public String idFromValue(Object value) {
            return "poly1";
        }

        @Override
        public String idFromValueAndType(Object value, Class<?> suggestedType) {
            return "poly1";
        }

        @Override
        public JavaType typeFromId(DeserializationContext context, String id)
                throws IOException, JsonProcessingException {
            if ("poly1".equals(id)) {
                return context.getTypeFactory().constructType(Poly1Base.class);
            }
            return null;
        }

        @Override
        public JsonTypeInfo.Id getMechanism() {
            return JsonTypeInfo.Id.CUSTOM;
        }
    }
}