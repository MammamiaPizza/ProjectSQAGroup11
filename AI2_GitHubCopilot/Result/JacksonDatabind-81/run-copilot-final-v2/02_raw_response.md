package com.fasterxml.jackson.databind.introspect;

 import com.fasterxml.jackson.annotation.JsonFormat;
 import com.fasterxml.jackson.annotation.JsonView;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.annotation.JsonSerialize;
 import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
 import org.junit.Rule;
 import org.junit.Test;
 import org.junit.rules.ExpectedException;

 import static org.junit.Assert.assertNotNull;

 public class JacksonAnnotationIntrospectorRefineTest {

     @Rule
     public ExpectedException thrown = ExpectedException.none();

     private final ObjectMapper mapper = new ObjectMapper();

     // --- Bean definitions ---

     /** Plain bean: primitive int, no annotation */
     public static class BeanNoAnnotation {
         public int i;
         public BeanNoAnnotation() {}
         public BeanNoAnnotation(int i) { this.i = i; }
     }

     /** Bean exercising all primitive-to-wrapper @JsonSerialize refinements */
     public static class BeanAllWrappers {
         @JsonSerialize(as = Integer.class)
         public int intField;
         @JsonSerialize(as = Long.class)
         public long longField;
         @JsonSerialize(as = Boolean.class)
         public boolean boolField;
         @JsonSerialize(as = Double.class)
         public double doubleField;
         @JsonSerialize(as = Float.class)
         public float floatField;
         @JsonSerialize(as = Short.class)
         public short shortField;
         @JsonSerialize(as = Byte.class)
         public byte byteField;
         @JsonSerialize(as = Character.class)
         public char charField;
     }

     /** Unrelated type refinement: primitive int -> String (should fail) */
     public static class BeanUnrelated {
         @JsonSerialize(as = String.class)
         public int i;
     }

     /** Primitive int field annotated with @JsonFormat */
     public static class BeanWithJsonFormat {
         @JsonFormat(shape = JsonFormat.Shape.NUMBER)
         public int i;
         public BeanWithJsonFormat() {}
         public BeanWithJsonFormat(int i) { this.i = i; }
     }

     /** Primitive int field annotated with @JsonView */
     public static class BeanWithJsonView {
         public interface View {}
         @JsonView(View.class)
         public int i;
         public BeanWithJsonView() {}
         public BeanWithJsonView(int i) { this.i = i; }
     }

     // --- Tests ---

     @Test
     public void noAnnotation_serializationUsesPrimitiveType() throws Exception {
         BeanNoAnnotation bean = new BeanNoAnnotation(42);
         String json = mapper.writeValueAsString(bean);
         assertNotNull(json);
         // verify primitive int serialized as number, not string
         org.junit.Assert.assertTrue(json.contains("42") && !json.contains("\"42\""));
     }

     @Test
     public void allPrimitiveToWrapperRefinements_shouldNotThrow() throws Exception {
         BeanAllWrappers bean = new BeanAllWrappers();
         bean.intField = 1;
         bean.longField = 2L;
         bean.boolField = true;
         bean.doubleField = 3.0;
         bean.floatField = 4.0f;
         bean.shortField = 5;
         bean.byteField = 6;
         bean.charField = 'A';
         String json = mapper.writeValueAsString(bean);
         assertNotNull(json);
     }

     @Test
     public void primitiveToUnrelatedType_shouldThrowInvalidDefinitionException() throws Exception {
         thrown.expect(InvalidDefinitionException.class);
         BeanUnrelated bean = new BeanUnrelated();
         bean.i = 1;
         mapper.writeValueAsString(bean);
     }

     @Test
     public void jsonFormatAnnotationOnPrimitive_shouldNotThrow() throws Exception {
         BeanWithJsonFormat bean = new BeanWithJsonFormat(42);
         String json = mapper.writeValueAsString(bean);
         assertNotNull(json);
     }

     @Test
     public void jsonViewAnnotationOnPrimitive_shouldNotThrow() throws Exception {
         BeanWithJsonView bean = new BeanWithJsonView(42);
         String json = mapper.writeValueAsString(bean);
         assertNotNull(json);
     }
 }