package org.apache.commons.jxpath.ri.axes;

 import junit.framework.TestCase;
 import org.apache.commons.jxpath.JXPathContext;
 import org.apache.commons.jxpath.Pointer;
 import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
 import org.apache.commons.jxpath.ri.QName;
 import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
 import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
 import org.apache.commons.jxpath.ri.model.NodePointer;
 import org.apache.commons.jxpath.ri.model.beans.NullPropertyPointer;

 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;

 public class AttributeContextTest extends TestCase {

     private AttributeContext createAttributeContext(Object bean, String xpath, String attrName)
             throws Exception {
         JXPathContextReferenceImpl ctx = (JXPathContextReferenceImpl)
JXPathContext.newContext(bean);
         Pointer ptr = ctx.getPointer(xpath);
         NodePointer nodePointer = (NodePointer) ptr;
         EvalContext parentContext = new SimpleEvalContext(nodePointer);
         NodeNameTest nodeTest = new NodeNameTest(new QName(null, attrName));
         return new AttributeContext(parentContext, nodeTest);
     }

     private static class SimpleEvalContext extends EvalContext {
         private NodePointer nodePointer;

         SimpleEvalContext(NodePointer nodePointer) {
             super(null);
             this.nodePointer = nodePointer;
         }

         public NodePointer getCurrentNodePointer() {
             return nodePointer;
         }

         public int getDocumentOrder() {
             return 0;
         }

         public boolean setPosition(int position) {
             return position == 1;
         }

         public boolean nextNode() {
             return false;
         }
     }

     private static class NullEvalContext extends EvalContext {
         NullEvalContext() {
             super(null);
         }

         public NodePointer getCurrentNodePointer() {
             return null;
         }

         public int getDocumentOrder() {
             return 0;
         }

         public boolean setPosition(int position) {
             return false;
         }

         public boolean nextNode() {
             return false;
         }
     }

     public static class SingleAttrBean {
         private String amount = "10%";

         public String getAmount() {
             return amount;
         }

         public void setAmount(String amount) {
             this.amount = amount;
         }
     }

     public static class Item {
         private String amount;
         private String currency;

         public Item(String amount, String currency) {
             this.amount = amount;
             this.currency = currency;
         }

         public String getAmount() {
             return amount;
         }

         public String getCurrency() {
             return currency;
         }
     }

     public static class Container {
         private List items = new ArrayList();

         public Container() {
             items.add(new Item("10%", "USD"));
             items.add(new Item("20%", "EUR"));
         }

         public List getItems() {
             return items;
         }
     }

     public static class NoAttrBean {
         private String name = "test";

         public String getName() {
             return name;
         }
     }

     public void testSingleAttribute() throws Exception {
         SingleAttrBean bean = new SingleAttrBean();
         AttributeContext ctx = createAttributeContext(bean, "/.", "amount");
         assertTrue("Should find at least one attribute", ctx.nextNode());
         NodePointer ptr = ctx.getCurrentNodePointer();
         assertNotNull("NodePointer should not be null", ptr);
         assertEquals("10%", ptr.getValue());
         assertFalse("Should not have a second attribute", ctx.nextNode());
     }

     public void testSetPositionSingleAttribute() throws Exception {
         SingleAttrBean bean = new SingleAttrBean();
         AttributeContext ctx = createAttributeContext(bean, "/.", "amount");
         assertTrue("setPosition(1) should succeed", ctx.setPosition(1));
         NodePointer ptr = ctx.getCurrentNodePointer();
         assertNotNull("NodePointer should not be null", ptr);
         assertEquals("10%", ptr.getValue());
     }

     public void testMultipleAttributesAcrossNodes() throws Exception {
         Container container = new Container();
         AttributeContext ctx = createAttributeContext(container, "/items", "amount");

         List values = new ArrayList();
         while (ctx.nextNode()) {
             values.add(ctx.getCurrentNodePointer().getValue());
         }

         assertEquals("Should find exactly 2 attributes", 2, values.size());
         assertEquals("First attribute value", "10%", values.get(0));
         assertEquals("Second attribute value", "20%", values.get(1));
     }

     public void testSetPositionMultipleAttributes() throws Exception {
         Container container = new Container();
         AttributeContext ctx = createAttributeContext(container, "/items", "amount");

         assertTrue("setPosition(1) should succeed", ctx.setPosition(1));
         assertEquals("First value", "10%", ctx.getCurrentNodePointer().getValue());

         assertTrue("setPosition(2) should succeed", ctx.setPosition(2));
         assertEquals("Second value", "20%", ctx.getCurrentNodePointer().getValue());

         assertFalse("setPosition(3) should fail", ctx.setPosition(3));
     }

     public void testNoMatchingAttributes() throws Exception {
         NoAttrBean bean = new NoAttrBean();
         AttributeContext ctx = createAttributeContext(bean, "/.", "amount");
         assertFalse("nextNode() should return false when no attribute matches", ctx.nextNode());
         assertNull("NodePointer should be null when no match", ctx.getCurrentNodePointer());
     }

     public void testSetPositionNoMatchingAttributes() throws Exception {
         NoAttrBean bean = new NoAttrBean();
         AttributeContext ctx = createAttributeContext(bean, "/.", "amount");
         assertFalse("setPosition(1) should fail when no attributes", ctx.setPosition(1));
     }

     public void testSetPositionBeyondLast() throws Exception {
         Container container = new Container();
         AttributeContext ctx = createAttributeContext(container, "/items", "amount");
         assertTrue(ctx.setPosition(2));
         assertEquals("20%", ctx.getCurrentNodePointer().getValue());
         assertFalse("setPosition(3) should fail", ctx.setPosition(3));
         assertEquals("Current pointer should remain at last valid position", "20%",
                 ctx.getCurrentNodePointer().getValue());
     }

     public void testResetAndIterateAgain() throws Exception {
         Container container = new Container();
         AttributeContext ctx = createAttributeContext(container, "/items", "amount");

         List firstPass = new ArrayList();
         while (ctx.nextNode()) {
             firstPass.add(ctx.getCurrentNodePointer().getValue());
         }
         assertEquals(2, firstPass.size());

         ctx.reset();
         List secondPass = new ArrayList();
         while (ctx.nextNode()) {
             secondPass.add(ctx.getCurrentNodePointer().getValue());
         }
         assertEquals("Second pass should collect same number of values", 2, secondPass.size());
         assertEquals("10%", secondPass.get(0));
         assertEquals("20%", secondPass.get(1));
     }

     public void testSetPositionBackwards() throws Exception {
         Container container = new Container();
         AttributeContext ctx = createAttributeContext(container, "/items", "amount");

         assertTrue(ctx.setPosition(2));
         assertEquals("20%", ctx.getCurrentNodePointer().getValue());

         assertTrue("setPosition(1) after being at 2 should succeed", ctx.setPosition(1));
         assertEquals("Should be back at first value", "10%",
                 ctx.getCurrentNodePointer().getValue());
     }

     public void testNonNodeNameTestReturnsFalse() throws Exception {
         SingleAttrBean bean = new SingleAttrBean();
         JXPathContextReferenceImpl ctx = (JXPathContextReferenceImpl)
JXPathContext.newContext(bean);
         Pointer ptr = ctx.getPointer("/.");
         NodePointer nodePointer = (NodePointer) ptr;
         EvalContext parentContext = new SimpleEvalContext(nodePointer);
         NodeTypeTest nodeTypeTest = new NodeTypeTest(2);
         AttributeContext attrCtx = new AttributeContext(parentContext, nodeTypeTest);
         assertFalse("nextNode() should return false for non-NodeNameTest", attrCtx.nextNode());
     }

     public void testCurrentNodePointerBeforeIteration() throws Exception {
         SingleAttrBean bean = new SingleAttrBean();
         AttributeContext ctx = createAttributeContext(bean, "/.", "amount");
         assertNull("getCurrentNodePointer() should be null before iteration",
                 ctx.getCurrentNodePointer());
     }
 }
