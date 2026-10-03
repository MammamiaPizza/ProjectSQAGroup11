package org.apache.commons.jxpath.ri.model.beans;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.jxpath.ri.model.NodePointer;

/**

 - Unit tests for PropertyPointer that target the JXPATH-151 bug and
 - null-value iteration bug (MixedModelTest::testNull).
  */
 public class PropertyPointerTest extends TestCase {
  // ---------- helper bean ----------
  public static final class TestBean {
  private String a;
  private String b;
  private List items;
  public String getA() { return a; }
  public void setA(String a) { this.a = a; }
  public String getB() { return b; }
  public void setB(String b) { this.b = b; }
  public List getItems() { return items; }
  public void setItems(List items) { this.items = items; }
  }
  private TestBean bean;
  private BeanPointer beanPointer;
  private BeanPointer nullBeanPointer;
  private BeanPropertyPointer propA;
  private BeanPropertyPointer propB;
  private BeanPropertyPointer propA2;
  private BeanPropertyPointer itemsProp;
  private BeanPropertyPointer nullPropA;
  private BeanPropertyPointer nullPropA2;
  protected void setUp() throws Exception {
  bean = new TestBean();
  bean.setA("aval");
  bean.setB("bval");
  bean.setItems(Arrays.asList("hello", null, "world")); // element[1] is null
  beanPointer = new BeanPointer(null, bean);
  nullBeanPointer = new BeanPointer(null, null);
  propA  = new BeanPropertyPointer(beanPointer,
                 new PropertyDescriptor("a", TestBean.class));
  propB  = new BeanPropertyPointer(beanPointer,
                 new PropertyDescriptor("b", TestBean.class));
  propA2 = new BeanPropertyPointer(beanPointer,
                 new PropertyDescriptor("a", TestBean.class));
  itemsProp = new BeanPropertyPointer(beanPointer,
                 new PropertyDescriptor("items", TestBean.class));
  nullPropA  = new BeanPropertyPointer(nullBeanPointer,
                 new PropertyDescriptor("a", TestBean.class));
  nullPropA2 = new BeanPropertyPointer(nullBeanPointer,
                 new PropertyDescriptor("a", TestBean.class));
  }
  // -----------------------------------------------
  // equals / hashCode
  // -----------------------------------------------
  public void testEqualsDifferentPropertyName() {
  assertFalse("pointers for different properties must not be equal",
              propA.equals(propB));
  }
  public void testEqualsSamePropertyName() {
  assertTrue("pointers for the same property must be equal",
             propA.equals(propA2));
  }
  public void testEqualsNullBeanSamePropertyName() {
  assertTrue("two property pointers with null beans but same property name must be equal",
             nullPropA.equals(nullPropA2));
  }
  public void testHashCodeConsistentWithEquals() {
  if (propA.equals(propA2)) {
      assertEquals("equal pointers must have equal hashcodes",
                   propA.hashCode(), propA2.hashCode());
  }
  // different property names may produce different hashcodes,
  // but at least they must not be forced equal.
  // Just verify the contract holds for the equal case; unequal
  // objects are allowed to have same hash.
  }
  public void testEqualsWithDifferentIndexWholeCollectionVsFirst() {
  // both indexes are WHOLE_COLLECTION atm; change one to 0
  BeanPropertyPointer p0 = (BeanPropertyPointer) propA.clone();
  p0.setIndex(0);
  // per current implementation WHOLE_COLLECTION is mapped to 0, so they are equal
  assertTrue("WHOLE_COLLECTION must equal index 0 (design decision)",
             propA.equals(p0));
  }
  // -----------------------------------------------
  // isActual / isActualProperty
  // -----------------------------------------------
  public void testIsActualValidProperty() {
  assertTrue("pointer to existing property must be actual", propA.isActual());
  }
  public void testIsActualNullBean() {
  // property "a" cannot be resolved on null bean -> isActualProperty is false
  assertFalse("pointer on null bean must not be actual", nullPropA.isActual());
  }
  // JXPATH-151 + testNull: null values must appear in iteration,
  // therefore isActual must return true for a pointer pointing to a null element.
  public void testIsActualNullElement() {
  BeanPropertyPointer item1 = (BeanPropertyPointer) itemsProp.clone();
  item1.setIndex(1); // points to the null element
  assertTrue("pointer to a null element of a collection must be actual",
             item1.isActual());
  }
  // -----------------------------------------------
  // getImmediateNode / getLength / getBaseValue
  // -----------------------------------------------
  public void testGetImmediateNodeNullBean() {
  assertNull("immediate node on null bean must be null",
             nullPropA.getImmediateNode());
  }
  public void testGetLengthNullBean() {
  assertEquals("length on null bean must be 0",
               0, nullPropA.getLength());
  }
  public void testGetImmediateNodeNullElement() {
  BeanPropertyPointer item1 = (BeanPropertyPointer) itemsProp.clone();
  item1.setIndex(1);
  assertNull("immediate node for a null element must be null",
             item1.getImmediateNode());
  }
  public void testGetLengthCollectionWithNull() {
  assertEquals("length must count null elements",
               3, itemsProp.getLength());
  }
  // -----------------------------------------------
  // getImmediateValuePointer
  // -----------------------------------------------
  public void testGetImmediateValuePointerNullElement() {
  BeanPropertyPointer item1 = (BeanPropertyPointer) itemsProp.clone();
  item1.setIndex(1);
  NodePointer vp = item1.getImmediateValuePointer();
  assertNotNull("value pointer for a null element must not be null", vp);
  // the value pointer must be actual as well (presence of element in collection)
  assertTrue("value pointer for null element must be actual", vp.isActual());
  }

}
