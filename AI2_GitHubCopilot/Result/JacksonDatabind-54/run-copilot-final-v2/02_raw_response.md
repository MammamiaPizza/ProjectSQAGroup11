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
     private BeanDescription beanDesc; // description of a simple bean used to create the
PropertyBuilder

     @Before
     public void setUp() {
         mapper = new ObjectMapper();
         // Use PropertyBuilderTest itself as a concrete class with default constructor
         beanDesc =
mapper.getSerializationConfig().introspect(mapper.constructType(PropertyBuilderTest.class));
     }

     // ----- getDefaultValue tests for JDK atomic types (core bug #1256) -----

     @Test
     public void testGetDefaultValueAtomicReferenceShouldBeNull() {
         PropertyBuilder builder = new PropertyBuilder(mapper.getSerializationConfig(), beanDesc);
         TestablePropertyBuilder tb = new TestablePropertyBuilder(builder);
         JavaType type = mapper.constructType(AtomicReference.class);
         // Expected: null so that AtomicReference(null) behaves like a missing value under NON_NULL
         assertNull("Default value for AtomicReference must be null",
tb.publicGetDefaultValue(type));
     }

     @Test
     public void testGetDefaultValueAtomicIntegerShouldBeNull() {
         PropertyBuilder builder = new PropertyBuilder(mapper.getSerializationConfig(), beanDesc);
         TestablePropertyBuilder tb = new TestablePropertyBuilder(builder);
         JavaType type = mapper.constructType(AtomicInteger.class);
         assertNull("Default value for AtomicInteger must be null", tb.publicGetDefaultValue(type));
     }

     @Test
     public void testGetDefaultValueAtomicBooleanShouldBeNull() {
         PropertyBuilder builder = new PropertyBuilder(mapper.getSerializationConfig(), beanDesc);
         TestablePropertyBuilder tb = new TestablePropertyBuilder(builder);
         JavaType type = mapper.constructType(AtomicBoolean.class);
         assertNull("Default value for AtomicBoolean must be null", tb.publicGetDefaultValue(type));
     }

     @Test
     public void testGetDefaultValueAtomicLongAndParameterizedShouldBeNull() {
         PropertyBuilder builder = new PropertyBuilder(mapper.getSerializationConfig(), beanDesc);
         TestablePropertyBuilder tb = new TestablePropertyBuilder(builder);
         // raw AtomicLong
         JavaType raw = mapper.constructType(AtomicLong.class);
         assertNull("Default value for AtomicLong must be null", tb.publicGetDefaultValue(raw));
         // parameterized AtomicReference<String>
         JavaType param = mapper.getTypeFactory().constructParametricType(AtomicReference.class,
String.class);
         assertNull("Default value for AtomicReference<String> must be null",
tb.publicGetDefaultValue(param));
     }

     // ----- getDefaultValue for ordinary types (expected behaviour, guard against regressions)
-----

     @Test
     public void testGetDefaultValueContainerTypesReturnNonEmpty() {
         PropertyBuilder builder = new PropertyBuilder(mapper.getSerializationConfig(), beanDesc);
         TestablePropertyBuilder tb = new TestablePropertyBuilder(builder);
         assertEquals("List should default to NON_EMPTY", JsonInclude.Include.NON_EMPTY,
                 tb.publicGetDefaultValue(mapper.constructType(List.class)));
         assertEquals("Map should default to NON_EMPTY", JsonInclude.Include.NON_EMPTY,
                 tb.publicGetDefaultValue(mapper.constructType(Map.class)));
     }

     @Test
     public void testGetDefaultValueStringReturnsEmpty() {
         PropertyBuilder builder = new PropertyBuilder(mapper.getSerializationConfig(), beanDesc);
         TestablePropertyBuilder tb = new TestablePropertyBuilder(builder);
         assertEquals("Default value for String must be empty string", "",
                 tb.publicGetDefaultValue(mapper.constructType(String.class)));
     }

     @Test
     public void testGetDefaultValuePrimitivesReturnDefault() {
         PropertyBuilder builder = new PropertyBuilder(mapper.getSerializationConfig(), beanDesc);
         TestablePropertyBuilder tb = new TestablePropertyBuilder(builder);
         assertEquals("Default value for int must be 0", 0,
                 tb.publicGetDefaultValue(mapper.constructType(int.class)));
         assertEquals("Default value for boolean must be false", false,
                 tb.publicGetDefaultValue(mapper.constructType(boolean.class)));
     }

     @Test
     public void testGetDefaultValueNormalObjectReturnsNull() {
         PropertyBuilder builder = new PropertyBuilder(mapper.getSerializationConfig(), beanDesc);
         TestablePropertyBuilder tb = new TestablePropertyBuilder(builder);
         // Integer as an example of a typical reference type that is not a container/reference
         assertNull("Default value for ordinary object must be null",
                 tb.publicGetDefaultValue(mapper.constructType(Integer.class)));
     }

     // ----- getDefaultBean & getPropertyDefaultValue (integration with atomic types) -----

     @Test
     public void testGetDefaultBeanReturnsInstanceForConcreteClass() {
         PropertyBuilder builder = new PropertyBuilder(mapper.getSerializationConfig(), beanDesc);
         TestablePropertyBuilder tb = new TestablePropertyBuilder(builder);
         Object bean = tb.publicGetDefaultBean();
         assertNotNull("Default bean must be instantiable for a concrete class", bean);
         assertTrue(bean instanceof PropertyBuilderTest);
     }

     /**
      * When a default bean can be instantiated, getPropertyDefaultValue returns the property's
      * value from that bean (null for an uninitialised AtomicReference field).
      */
     @Test
     public void testGetPropertyDefaultValueForAtomicFieldReturnsNullFromDefaultBean() throws
Exception {
         BeanDescription atomicBeanDesc = mapper.getSerializationConfig()
                 .introspect(mapper.constructType(BeanWithAtomic.class));
         PropertyBuilder builder = new PropertyBuilder(mapper.getSerializationConfig(),
atomicBeanDesc);
         TestablePropertyBuilder tb = new TestablePropertyBuilder(builder);

         // The bean has a default constructor and the field is null.
         // Find the property definition for 'atomicRef'
         AnnotatedMember member = atomicBeanDesc.findProperties().stream()
                 .filter(p -> "atomicRef".equals(p.getName()) && p.getField() != null)
                 .findFirst()
                 .orElseThrow(() -> new AssertionError("field 'atomicRef' not found"))
                 .getField();

         JavaType type = mapper.constructType(AtomicReference.class);
         Object result = tb.publicGetPropertyDefaultValue("atomicRef", member, type);
         // The default bean field is null -> should return null
         assertNull("Default property value for an unset AtomicReference field must be null",
result);
     }

     /**
      * When no default bean can be created, getPropertyDefaultValue must fall back to
getDefaultValue,
      * which (for atomic types) must be null in the fixed version – preserving the contract from
#1256.
      */
     @Test
     public void testGetPropertyDefaultValueFallbackUsesGetDefaultValue() throws Exception {
         // Use a class that cannot be instantiated (abstract) so getDefaultBean returns null.
         BeanDescription abstractBeanDesc = mapper.getSerializationConfig()
                 .introspect(mapper.constructType(AbstractBean.class));
         // Recreate the builder so it computes getDefaultBean() again (cached inside the builder).
         PropertyBuilder builder = new PropertyBuilder(mapper.getSerializationConfig(),
abstractBeanDesc);

         TestablePropertyBuilder tb = new TestablePropertyBuilder(builder) {
             @Override
             public Object publicGetDefaultBean() {
                 return null; // force fallback
             }
         };

         // getPropertyDefaultValue should now call getDefaultValue for the atomic type.
         JavaType atomicType = mapper.constructType(AtomicReference.class);
         // The method expects an AnnotatedMember; we provide a dummy field from PropertyBuilderTest
         AnnotatedMember dummyMember = beanDesc.findProperties().stream()
                 .filter(p -> p.getField() != null)
                 .findFirst()
                 .orElseThrow(() -> new AssertionError("no field found for dummy")).getField();

         Object result = tb.publicGetPropertyDefaultValue("dummy", dummyMember, atomicType);
         assertNull("Fallback default for AtomicReference must be null", result);
     }

     // ----- helper classes -----

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

     /** Abstract bean: cannot be instantiated by BeanDescription, so getDefaultBean returns null.
*/
     public abstract static class AbstractBean {
         public abstract String getValue();
     }

     /**
      * Thin wrapper around {@link PropertyBuilder} that exposes protected methods publicly
      * for testing without reflection.
      */
     public static class TestablePropertyBuilder extends PropertyBuilder {
         public TestablePropertyBuilder(PropertyBuilder source) {
             // copy state by invoking via the constructor we actually have,
             // but we cannot change production code. Use a fresh builder with same config.
             super(source._config, source._beanDesc);
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