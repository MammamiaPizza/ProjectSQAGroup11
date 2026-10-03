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
}