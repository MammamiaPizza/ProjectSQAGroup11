import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BeanDeserializerFactoryIllegalTypeTest {

    @Test
    public void rejectsTemplatesImplWhenUsedAsDefaultTypedValue() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();

        boolean rejected = false;
        try {
            mapper.readValue(
                    "[\"com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl\",{}]",
                    Object.class);
        } catch (Exception e) {
            rejected = true;
        }

        assertTrue("Dangerous JDK TemplatesImpl type must not deserialize successfully", rejected);
    }

@org.junit.Test
public void rejectsJdbcRowSetImplWhenUsedAsDefaultTypedValue() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enableDefaultTyping();

    boolean rejected = false;
    try {
        mapper.readValue("{\"@class\":\"com.sun.rowset.JdbcRowSetImpl\"}", Object.class);
    } catch (Exception e) {
        rejected = true;
    }

    org.junit.Assert.assertTrue("Dangerous JDK JdbcRowSetImpl type must not deserialize successfully", rejected);
}
}
