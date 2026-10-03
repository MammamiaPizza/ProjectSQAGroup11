import static org.junit.Assert.*;
 import org.junit.Test;
 import com.fasterxml.jackson.annotation.JsonSerialize;
 import com.fasterxml.jackson.databind.JsonMappingException;
 import com.fasterxml.jackson.databind.ObjectMapper;

 import java.util.*;

 /**

 - Tests for AnnotationIntrospector.refineSerializationType() error reporting.
 - <p>
 - The fix for bug #1231 requires that when an {@code @JsonSerialize} annotation
 - specifies a type that is unrelated (neither a super- nor a sub-type) to the
 - actual property type, the resulting {@link JsonMappingException} must contain
 - the phrase {@code "types not related"}. This test class verifies that behaviour
 - for the main type, key type and content type cases, and also includes positive
 - cases (compatible or equal types) that must succeed.
   */
  public class RefineSerializationTypeUnrelatedTest {
  // ---------------------------------------------------------------
  // Helper POJOs for the test cases
  // ---------------------------------------------------------------
  @SuppressWarnings("unused")
  // ----------
  6. Unrelated main type: Long property, annotation asks for String ----------
  public static class BeanUnrelatedMain {
  private Long value;
  @JsonSerialize(value = String.class) public Long getValue() { return value; }
  public void setValue(Long v) { value = v; }
  }
  // ----------
  7. Unrelated key type: Map<Long,…> with key annotation String ----------
  public static class BeanUnrelatedKey {
  private Map<Long, String> map;
  @JsonSerialize(key = String.class)
  public Map<Long, String> getMap() { return map; }
  public void setMap(Map<Long, String> m) { map = m; } }
  // related classes for content type test
  public static class BaseType { }
  public static class AbstractType { }  // not related to BaseType
  // ----------
  8. Unrelated content type: List<BaseType> with content annotation AbstractType
  // ----------
  public static class BeanUnrelatedContent { private List<BaseType> list;
  @JsonSerialize(content = AbstractType.class) public List<BaseType> getList() { return list; }
  public void setList(List<BaseType> l) { list = l; } }
  // ----------
  9. Compatible subtype: Number property with annotation Integer ----------
  public static class BeanCompatibleSubtype { private Number num;
  @JsonSerialize(value = Integer.class) public Number getNum() { return num; }
  public void setNum(Number n) { num = n; } }
  // ----------
  10. Same type: String property with annotation String ----------
  public static class BeanSameType { private String str;
  @JsonSerialize(value = String.class)
  public String getStr() { return str; }
  public void setStr(String s) { str = s; }
  }
  // ---------------------------------------------------------------
  // Tests
  // ---------------------------------------------------------------
  @Test
  public void unrelatedMainType_shouldFailWithTypesNotRelated() throws Exception {
  ObjectMapper mapper = new ObjectMapper();
  BeanUnrelatedMain bean = new BeanUnrelatedMain();
  bean.setValue(42L);
  try {
      mapper.writeValueAsString(bean);
      fail("Expected JsonMappingException for unrelated main type");
  } catch (JsonMappingException e) {
      assertTrue("Exception message must contain 'types not related', but was: "
                 + e.getMessage(),
                 e.getMessage().contains("types not related"));
  }
  }
  @Test
  public void unrelatedKeyType_shouldFailWithTypesNotRelated() throws Exception {
  ObjectMapper mapper = new ObjectMapper();
  BeanUnrelatedKey bean = new BeanUnrelatedKey();
  Map<Long, String> m = new HashMap<Long, String>();
  m.put(1L, "a");
  bean.setMap(m);
  try {
      mapper.writeValueAsString(bean);
      fail("Expected JsonMappingException for unrelated key type");
  } catch (JsonMappingException e) {
      assertTrue("Exception message must contain 'types not related', but was: "
                 + e.getMessage(),
                 e.getMessage().contains("types not related"));
  }
  }
  @Test
  public void unrelatedContentType_shouldFailWithTypesNotRelated() throws Exception {
  ObjectMapper mapper = new ObjectMapper();
  BeanUnrelatedContent bean = new BeanUnrelatedContent();
  bean.setList(Collections.<BaseType>emptyList());
  try {
      mapper.writeValueAsString(bean);
      fail("Expected JsonMappingException for unrelated content type");
  } catch (JsonMappingException e) {
      assertTrue("Exception message must contain 'types not related', but was: "
                 + e.getMessage(),
                 e.getMessage().contains("types not related"));
  }
  }
  @Test
  public void compatibleSubtype_shouldNotThrow() throws Exception {
  ObjectMapper mapper = new ObjectMapper();
  BeanCompatibleSubtype bean = new BeanCompatibleSubtype();
  bean.setNum(123);
  // should succeed (no exception)
  String json = mapper.writeValueAsString(bean);
  assertNotNull(json);
  }
  @Test
  public void sameType_shouldNotThrow() throws Exception {
  ObjectMapper mapper = new ObjectMapper();
  BeanSameType bean = new BeanSameType();
  bean.setStr("test");
  String json = mapper.writeValueAsString(bean);
  assertNotNull(json);
  }
  @Test
  public void unrelatedMainType_exceptionMustBeJsonMappingException() {
  ObjectMapper mapper = new ObjectMapper();
  BeanUnrelatedMain bean = new BeanUnrelatedMain();
  try {
      mapper.writeValueAsString(bean);
      fail("Expected exception");
  } catch (JsonMappingException e) {
      // expected
  } catch (Exception e) {
      fail("Expected JsonMappingException, got " + e.getClass());
  }
  }

 }