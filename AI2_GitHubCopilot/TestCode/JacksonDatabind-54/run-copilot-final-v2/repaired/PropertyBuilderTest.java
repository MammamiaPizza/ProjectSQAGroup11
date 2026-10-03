import static org.junit.Assert.*;

 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.atomic.AtomicBoolean;
 import java.util.concurrent.atomic.AtomicInteger;
 import java.util.concurrent.atomic.AtomicLong;
 import java.util.concurrent.atomic.AtomicReference;

 import org.junit.Before;
 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonInclude;
 import com.fasterxml.jackson.databind.BeanDescription;
 import com.fasterxml.jackson.databind.JavaType;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.SerializationConfig;
 import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
 import com.fasterxml.jackson.databind.ser.PropertyBuilder;

 public class PropertyBuilderTest {

     private ObjectMapper mapper;
     private BeanDescription beanDesc;

     @Before
     public void setUp() {
         mapper = new ObjectMapper();
         beanDesc =
mapper.getSerializationConfig().introspect(mapper.constructType(PropertyBuilderTest.class));
     }

     @Test
     public void testGetDefaultValueAtomicReferenceShouldBeNull() {
         TestablePropertyBuilder tb = new TestablePropertyBuilder(mapper.getSerializationConfig(),
beanDesc);
         JavaType type = mapper.constructType(AtomicReference.class);
         assertNull("Default value for AtomicReference must be null",
tb.publicGetDefaultValue(type));
     }

     @Test
     public void testGetDefaultValueAtomicIntegerShouldBeNull() {
         TestablePropertyBuilder tb = new TestablePropertyBuilder(mapper.getSerializationConfig(),
beanDesc);
         JavaType type = mapper.constructType(AtomicInteger.class);
         assertNull("Default value for AtomicInteger must be null", tb.publicGetDefaultValue(type));
     }

     @Test
     public void testGetDefaultValueAtomicBooleanShouldBeNull() {
         TestablePropertyBuilder tb = new TestablePropertyBuilder(mapper.getSerializationConfig(),
beanDesc);
         JavaType type = mapper.constructType(AtomicBoolean.class);
         assertNull("Default value for AtomicBoolean must be null", tb.publicGetDefaultValue(type));
     }

     @Test
     public void testGetDefaultValueAtomicLongAndParameterizedShouldBeNull() {
         TestablePropertyBuilder tb = new TestablePropertyBuilder(mapper.getSerializationConfig(),
beanDesc);
         JavaType raw = mapper.constructType(AtomicLong.class);
         assertNull("Default value for AtomicLong must be null", tb.publicGetDefaultValue(raw));
         JavaType param = mapper.getTypeFactory().constructParametricType(AtomicReference.class,
String.class);
         assertNull("Default value for AtomicReference<String> must be null",
tb.publicGetDefaultValue(param));
     }

     @Test
     public void testGetDefaultValueContainerTypesReturnNonEmpty() {
         TestablePropertyBuilder tb = new TestablePropertyBuilder(mapper.getSerializationConfig(),
beanDesc);
         assertEquals("List should default to NON_EMPTY", JsonInclude.Include.NON_EMPTY,
tb.publicGetDefaultValue(mapper.constructType(List.class)));
         assertEquals("Map should default to NON_EMPTY", JsonInclude.Include.NON_EMPTY,
tb.publicGetDefaultValue(mapper.constructType(Map.class)));
     }

     @Test
     public void testGetDefaultValueStringReturnsEmpty() {
         TestablePropertyBuilder tb = new TestablePropertyBuilder(mapper.getSerializationConfig(),
beanDesc);
         assertEquals("Default value for String must be empty string", "",
tb.publicGetDefaultValue(mapper.constructType(String.class)));
     }

     @Test
     public void testGetDefaultValuePrimitivesReturnDefault() {
         TestablePropertyBuilder tb = new TestablePropertyBuilder(mapper.getSerializationConfig(),
beanDesc);
         assertEquals("Default value for int must be 0", 0,
tb.publicGetDefaultValue(mapper.constructType(int.class)));
         assertEquals("Default value for boolean must be false", false,
tb.publicGetDefaultValue(mapper.constructType(boolean.class)));
     }

     @Test
     public void testGetDefaultValueNormalObjectReturnsNull() {
         TestablePropertyBuilder tb = new TestablePropertyBuilder(mapper.getSerializationConfig(),
beanDesc);
         assertNull("Default value for ordinary object must be null",
tb.publicGetDefaultValue(mapper.constructType(Integer.class)));
     }

     @Test
     public void testGetDefaultBeanReturnsInstanceForConcreteClass() {
         TestablePropertyBuilder tb = new TestablePropertyBuilder(mapper.getSerializationConfig(),
beanDesc);
         Object bean = tb.publicGetDefaultBean();
         assertNotNull("Default bean must be instantiable for a concrete class", bean);
         assertTrue(bean instanceof PropertyBuilderTest);
     }

     @Test
     public void testGetPropertyDefaultValueForAtomicFieldReturnsNullFromDefaultBean() throws
Exception {
         BeanDescription atomicBeanDesc = mapper.getSerializationConfig()
                 .introspect(mapper.constructType(BeanWithAtomic.class));
         TestablePropertyBuilder tb = new TestablePropertyBuilder(mapper.getSerializationConfig(),
atomicBeanDesc);

         AnnotatedMember member = atomicBeanDesc.findProperties().stream()
                 .filter(p -> "atomicRef".equals(p.getName()) && p.getField() != null)
                 .findFirst()
                 .orElseThrow(() -> new AssertionError("field 'atomicRef' not found"))
                 .getField();

         JavaType type = mapper.constructType(AtomicReference.class);
         Object result = tb.publicGetPropertyDefaultValue("atomicRef", member, type);
         assertNull("Default property value for an unset AtomicReference field must be null",
result);
     }

     @Test
     public void testGetPropertyDefaultValueFallbackUsesGetDefaultValue() throws Exception {
         BeanDescription abstractBeanDesc = mapper.getSerializationConfig()
                 .introspect(mapper.constructType(AbstractBean.class));
         TestablePropertyBuilder tb = new TestablePropertyBuilder(mapper.getSerializationConfig(),
abstractBeanDesc) {
             @Override
             public Object publicGetDefaultBean() {
                 return null;
             }
         };

         JavaType atomicType = mapper.constructType(AtomicReference.class);
         AnnotatedMember dummyMember = beanDesc.findProperties().stream()
                 .filter(p -> p.getField() != null)
                 .findFirst()
                 .orElseThrow(() -> new AssertionError("no field found for dummy")).getField();

         Object result = tb.publicGetPropertyDefaultValue("dummy", dummyMember, atomicType);
         assertNull("Fallback default for AtomicReference must be null", result);
     }

     @SuppressWarnings("unused")
     public static class BeanWithAtomic {
         private AtomicReference<String> atomicRef;

         public AtomicReference<String> getAtomicRef() {
             return atomicRef;
         }

         public void setAtomicRef(AtomicReference<String> atomicRef) {
             this.atomicRef = atomicRef;
         }
     }

     public abstract static class AbstractBean {
         public abstract String getValue();
     }

     public static class TestablePropertyBuilder extends PropertyBuilder {
         public TestablePropertyBuilder(SerializationConfig config, BeanDescription beanDesc) {
             super(config, beanDesc);
         }

         public Object publicGetDefaultValue(JavaType type) {
             return getDefaultValue(type);
         }

         public Object publicGetDefaultBean() {
             return getDefaultBean();
         }

         public Object publicGetPropertyDefaultValue(String name, AnnotatedMember member, JavaType
type) {
             return getPropertyDefaultValue(name, member, type);
         }
     }
 }
