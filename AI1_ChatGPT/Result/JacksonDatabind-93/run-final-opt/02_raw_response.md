package org.springframework;

import java.util.logging.FileHandler;

import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.SubTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SubTypeValidatorBug93Test
{
    private JavaType typeFor(Class<?> rawClass) {
        return TypeFactory.defaultInstance().constructType(rawClass);
    }

    private DeserializationContext context() {
        return new ObjectMapper().getDeserializationContext();
    }

    @Test
    public void instanceReturnsSingletonValidator() {
        assertSame(SubTypeValidator.instance(), SubTypeValidator.instance());
    }

    @Test
    public void acceptsOrdinaryJdkType() throws Exception {
        SubTypeValidator.instance().validateSubType(context(), typeFor(String.class));
    }

    @Test
    public void acceptsOrdinarySpringNamedClassWithObjectSuperclass() throws Exception {
        SubTypeValidator.instance().validateSubType(context(), typeFor(SpringOrdinaryBean.class));
    }

    @Test
    public void acceptsSpringNamedInterfaceWhoseSuperclassIsNull() throws Exception {
        SubTypeValidator.instance().validateSubType(context(), typeFor(SpringMarker.class));
    }

    @Test
    public void rejectsKnownDangerousJdkType() throws Exception {
        try {
            SubTypeValidator.instance().validateSubType(context(), typeFor(FileHandler.class));
            fail("Known dangerous JDK type must be rejected");
        } catch (JsonMappingException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains(FileHandler.class.getName()));
        }
    }

    @Test
    public void rejectsSpringAbstractPointcutAdvisorType() throws Exception {
        try {
            SubTypeValidator.instance().validateSubType(context(), typeFor(AbstractPointcutAdvisor.class));
            fail("Spring pointcut advisor types must be rejected");
        } catch (JsonMappingException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains(AbstractPointcutAdvisor.class.getName()));
        }
    }

    public interface SpringMarker {
    }

    public static class SpringOrdinaryBean {
    }

    public static class AbstractPointcutAdvisor {
    }
}