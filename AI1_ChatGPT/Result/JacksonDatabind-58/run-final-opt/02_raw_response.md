import java.lang.reflect.ReflectPermission;
import java.security.Permission;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BeanDeserializerFactoryBug58Test {

    public static class PublicThrowable extends Throwable {
        private static final long serialVersionUID = 1L;

        public int code;

        public PublicThrowable() {
        }
    }

    @Test
    public void deserializesThrowableWhenSuppressAccessChecksIsDenied() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SecurityManager previous = System.getSecurityManager();
        SecurityManager denyReflectiveAccess = new SecurityManager() {
            @Override
            public void checkPermission(Permission permission) {
                if (permission instanceof ReflectPermission
                        && "suppressAccessChecks".equals(permission.getName())) {
                    throw new SecurityException("Reflection access is denied");
                }
            }
        };

        System.setSecurityManager(denyReflectiveAccess);
        try {
            Throwable value = mapper.readValue("{}", Throwable.class);

            assertNotNull(value);
            assertNull(value.getCause());
        } finally {
            System.setSecurityManager(previous);
        }
    }

    @Test
    public void deserializesAccessiblePropertiesOfThrowableSubclass() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        PublicThrowable value = mapper.readValue("{\"code\":37}", PublicThrowable.class);

        assertEquals(37, value.code);
    }
}