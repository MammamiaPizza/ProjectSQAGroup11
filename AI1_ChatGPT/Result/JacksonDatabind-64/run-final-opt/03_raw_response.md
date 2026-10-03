import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropertyBuilderBug64Test
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void nonDefaultStringNullWithoutDefaultConstructorIsSuppressed() throws Exception {
        assertEquals("{}", mapper.writeValueAsString(new NonDefaultStringBean(null)));
    }

    @Test
    public void nonDefaultStringValueWithoutDefaultConstructorIsSerialized() throws Exception {
        assertEquals("{\"str\":\"value\"}",
                mapper.writeValueAsString(new NonDefaultStringBean("value")));
    }

    @Test
    public void emptyStringIsTreatedAsDefaultWithoutDefaultConstructor() throws Exception {
        assertEquals("{}", mapper.writeValueAsString(new NonDefaultStringBean("")));
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static class NonDefaultStringBean {
        public String str;

        public NonDefaultStringBean(String str) {
            this.str = str;
        }
    }
}