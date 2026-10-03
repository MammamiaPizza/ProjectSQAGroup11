import static org.junit.Assert.*;
 import org.junit.Test;

 import com.fasterxml.jackson.databind.ObjectMapper;

 import java.lang.reflect.Proxy;

 public class BeanDeserializerFactoryTest {

     private final ObjectMapper mapper = new ObjectMapper();

     public static class SimpleBean {
         public int value;
     }

     public interface ProxyInterface {
         void dummy();
     }

     public static abstract class AbstractBean {
         public String name;
     }

     public class NonStaticInner {
         public int x;
     }

     @Test
     public void testNormalBeanDeserialization() throws Exception {
         SimpleBean bean = mapper.readValue("{\"value\":42}", SimpleBean.class);
         assertNotNull(bean);
         assertEquals(42, bean.value);
     }

     @Test
     public void testEnumDeserialization() throws Exception {
         TestEnum value = mapper.readValue("\"A\"", TestEnum.class);
         assertEquals(TestEnum.A, value);
     }

     public enum TestEnum { A, B }

     @Test
     public void testClassTypeRejected() {
         try {
             mapper.readValue("{}", Class.class);
             fail("Expected IllegalArgumentException for java.lang.Class");
         } catch (IllegalArgumentException e) {
             assertTrue("Exception message must contain '[Illegal type]'",
                        e.getMessage().contains("[Illegal type]"));
         } catch (Exception e) {
             assertFalse("Exception message must not be 'N/A'",
                         "N/A".equals(e.getMessage()));
         }
     }

     @Test
     public void testProxyClassRejected() {
         Class<?> proxyClass = Proxy.getProxyClass(
                 getClass().getClassLoader(),
                 ProxyInterface.class);
         try {
             mapper.readValue("{}", proxyClass);
             fail("Expected exception for Proxy class");
         } catch (IllegalArgumentException e) {
             String msg = e.getMessage();
             assertTrue("Exception message must contain 'Illegal type' or '[Illegal type]'",
                        msg.contains("[Illegal type]") || msg.contains("Illegal type"));
             assertFalse("Exception message must not be 'N/A'", "N/A".equals(msg));
         } catch (Exception e) {
             assertFalse("Exception message must not be 'N/A'",
                         "N/A".equals(e.getMessage()));
         }
     }

     @Test
     public void testAbstractClassRejected() {
         try {
             mapper.readValue("{}", AbstractBean.class);
             fail("Expected exception for abstract class");
         } catch (IllegalArgumentException e) {
             String msg = e.getMessage();
             assertTrue("Exception message must contain '[Illegal type]' or indicate abstract",
                        msg.contains("[Illegal type]") || msg.contains("abstract"));
             assertFalse("Exception message must not be 'N/A'", "N/A".equals(msg));
         } catch (Exception e) {
             assertFalse("Exception message must not be 'N/A'",
                         "N/A".equals(e.getMessage()));
         }
     }

     @Test
     public void testNonStaticInnerClassRejected() {
         try {
             mapper.readValue("{}", NonStaticInner.class);
             fail("Expected exception for non-static inner class");
         } catch (IllegalArgumentException e) {
             String msg = e.getMessage();
             assertTrue("Exception message must contain '[Illegal type]' or indicate illegal",
                        msg.contains("[Illegal type]") || msg.contains("Illegal type") ||
msg.contains("inner"));
             assertFalse("Exception message must not be 'N/A'", "N/A".equals(msg));
         } catch (Exception e) {
             assertFalse("Exception message must not be 'N/A'",
                         "N/A".equals(e.getMessage()));
         }
     }

     @Test
     public void testInterfaceRejected() {
         try {
             mapper.readValue("{}", ProxyInterface.class);
             fail("Expected exception for interface");
         } catch (IllegalArgumentException e) {
             String msg = e.getMessage();
             assertTrue("Exception message must contain '[Illegal type]' or indicate
abstract/interface",
                        msg.contains("[Illegal type]") || msg.contains("abstract") ||
msg.contains("interface"));
             assertFalse("Exception message must not be 'N/A'", "N/A".equals(msg));
         } catch (Exception e) {
             assertFalse("Exception message must not be 'N/A'",
                         "N/A".equals(e.getMessage()));
         }
     }

     @Test
     public void testThrowableAccepted() throws Exception {
         Exception ex = mapper.readValue("{\"message\":\"test\"}", Exception.class);
         assertNotNull(ex);
         assertEquals("test", ex.getMessage());
     }

     @Test
     public void testMapTypeAccepted() throws Exception {
         java.util.Map<?,?> map = mapper.readValue("{\"a\":1}", java.util.HashMap.class);
         assertNotNull(map);
         assertEquals(1, map.get("a"));
     }

     @Test
     public void testNullTypeShouldThrow() {
         try {
             mapper.readValue("{}", (Class<?>) null);
             fail("Expected exception for null type");
         } catch (IllegalArgumentException e) {
             assertFalse("Exception message must not be 'N/A'",
                         "N/A".equals(e.getMessage()));
         } catch (NullPointerException e) {
         }
     }
 }
