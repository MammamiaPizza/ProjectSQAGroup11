package org.apache.commons.jxpath.ri.model;

import java.util.Locale;

import junit.framework.TestCase;

/**

 - Tests for the {@link NodePointer} comparison behavior affected by JXPATH-89:
 - comparing pointers that belong to different trees must not throw.
  */
 public class NodePointerTest extends TestCase {
  /**
  - Minimal concrete NodePointer used to exercise comparison logic.
    */
   private static final class TestNodePointer extends NodePointer {
   private static final long serialVersionUID = 1L;
   private final QName name;
   TestNodePointer(NodePointer parent, String name) {
   super(parent);
   this.name = new QName(name);
   }
   TestNodePointer(NodePointer parent, String name, Locale locale) {
   super(parent, locale);
   this.name = new QName(name);
   }
   public boolean isLeaf() {
   return true;
   }
   public boolean isCollection() {
   return false;
   }
   public int getLength() {
   return 1;
   }
   public QName getName() {
   return name;
   }
   public Object getBaseValue() {
   return null;
   }
   public Object getImmediateNode() {
   return name.getName();
   }
   public void setValue(Object value) {
   // no-op
   }
  }
  private TestNodePointer rootA;
  private TestNodePointer childA1;
  private TestNodePointer childA2;
  private TestNodePointer grandChildA1;
  private TestNodePointer rootB;
  private TestNodePointer childB1;
  protected void setUp() {
      rootA = new TestNodePointer(null, "rootA");
      childA1 = new TestNodePointer(rootA, "a1");
      childA2 = new TestNodePointer(rootA, "a2");
      grandChildA1 = new TestNodePointer(childA1, "ga1");
      rootB = new TestNodePointer(null, "rootB");
      childB1 = new TestNodePointer(rootB, "b1");
  }
  public void testCompareToSelfReturnsZero() {
      assertEquals(0, childA1.compareTo(childA1));
      assertEquals(0, rootA.compareTo(rootA));
  }
  public void testCompareToSiblingsUsesParentOrdering() {
      // Same parent: parent.compareChildNodePointers is invoked; default
      // NodePointer does not implement it, so the result is deterministic 0.
      int result = childA1.compareTo(childA2);
      assertEquals(0, result);
  }
  public void testCompareToParentChildSameTreeDoesNotThrow() {
      // Different depth but same root: must not throw JXPathException.
      try {
          childA1.compareTo(grandChildA1);
      }
      catch (JXPathException e) {
          fail("Same-tree pointers should not throw: " + e.getMessage());
      }
  }
  public void testCompareToRootChildDoesNotThrow() {
      try {
          rootA.compareTo(childA1);
      }
      catch (JXPathException e) {
          fail("Same-tree root/child should not throw: " + e.getMessage());
      }
  }
  public void testCompareToDifferentTreesDoesNotThrow() {
      // JXPATH-89: union of variable and node pointers must not throw.
      try {
          childA1.compareTo(childB1);
      }
      catch (JXPathException e) {
          fail("Comparing pointers from different trees must not throw: "
                  + e.getMessage());
      }
  }
  public void testCompareToDifferentRootsDoesNotThrow() {
      try {
          rootA.compareTo(rootB);
      }
      catch (JXPathException e) {
          fail("Comparing different roots must not throw: " + e.getMessage());
      }
  }
  public void testCompareToEmptyPathRootVersusNonRootDoesNotThrow() {
      // Root has empty path (''); child has a non-empty path.
      assertEquals("", rootA.asPath());
      assertTrue(childA1.asPath().length() > 0);
      try {
          rootB.compareTo(childA1);
      }
      catch (JXPathException e) {
          fail("Comparing empty-path root to another tree must not throw: "
                  + e.getMessage());
      }
  }
  public void testCompareToNullThrowsClassCastException() {
      try {
          childA1.compareTo(null);
          fail("Expected NullPointerException");
      }
      catch (ClassCastException expected) {
          // The cast (NodePointer) object on null does not throw; the
          // subsequent access does. Accept NPE as well.
      }
      catch (NullPointerException expected) {
          // acceptable
      }
  }
  public void testAsPathForRootIsEmpty() {
      assertEquals("", rootA.asPath());
  }
  public void testAsPathForChildIsNonEmpty() {
      String path = childA1.asPath();
      assertNotNull(path);
      assertTrue(path.length() > 0);
  }
  public void testGetRootNodeResolvesToTreeRoot() {
      // grandChildA1's root node should be rootA's immediate node.
      assertEquals(rootA.getImmediateNode(), grandChildA1.getRootNode());
      assertEquals(rootA.getImmediateNode(), rootA.getRootNode());
  }
  public void testCompareToReversedOrderConsistent() {
      // Ordering across different trees should be consistent (antisymmetric).
      int ab = childA1.compareTo(childB1);
      int ba = childB1.compareTo(childA1);
      assertEquals(-ab, ba);
  }

}
