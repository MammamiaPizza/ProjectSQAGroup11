package org.jfree.chart.block.junit;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.Size2D;
import org.jfree.data.Range;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**

 - Focused regression tests for the width-constraint sizing behaviour of
 - {@link BorderArrangement}.
  */
 public class BorderArrangementSizingTest {
  private static final double EPS = 1e-9;
  private Graphics2D g2;
  @Before
  public void setUp() {
  g2 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
  }
  @After
  public void tearDown() {
  if (g2 != null) {
      g2.dispose();
  }
  }
  private static EmptyBlock block(double width, double height) {
  return new EmptyBlock(width, height);
  }
  private static RectangleConstraint fixedWidth(double width) {
  return new RectangleConstraint(width, LengthConstraintType.FIXED,
          0.0, LengthConstraintType.NONE);
  }
  private static RectangleConstraint fixedSize(double width, double height) {
  return new RectangleConstraint(width, LengthConstraintType.FIXED,
          height, LengthConstraintType.FIXED);
  }
  @Test
  public void testEmptyContainerArrangesToZeroSize() {
  BlockContainer container = new BlockContainer(new BorderArrangement());
  Size2D size = container.arrange(g2, RectangleConstraint.NONE);
  assertEquals(0.0, size.getWidth(), EPS);
  assertEquals(0.0, size.getHeight(), EPS);
  }
  @Test
  public void testSingleCenterBlockWithoutConstraint() {
  BlockContainer container = new BlockContainer(new BorderArrangement());
  container.add(block(100.0, 50.0), null);
  Size2D size = container.arrange(g2, RectangleConstraint.NONE);
  assertEquals(100.0, size.getWidth(), EPS);
  assertEquals(50.0, size.getHeight(), EPS);
  }
  @Test
  public void testFixedWidthWithSingleCenterBlock() {
  BlockContainer container = new BlockContainer(new BorderArrangement());
  container.add(block(100.0, 50.0), null);
  Size2D size = container.arrange(g2, fixedWidth(120.0));
  assertEquals(120.0, size.getWidth(), EPS);
  assertEquals(50.0, size.getHeight(), EPS);
  }
  @Test
  public void testSmallWidthReflowsBorderBlocks() {
  BlockContainer container = new BlockContainer(new BorderArrangement());
  EmptyBlock left = block(25.0, 10.0);
  EmptyBlock center = block(100.0, 50.0);
  EmptyBlock right = block(25.0, 10.0);
  container.add(left, RectangleEdge.LEFT);
  container.add(center, null);
  container.add(right, RectangleEdge.RIGHT);
  Size2D size = container.arrange(g2, fixedWidth(30.0));
  assertEquals(30.0, size.getWidth(), EPS);
  assertTrue(size.getHeight() >= 10.0);
  assertNotNull(left.getBounds());
  assertNotNull(right.getBounds());
  assertEquals(25.0, left.getBounds().getWidth(), EPS);
  assertEquals(30.0, right.getBounds().getMaxX(), EPS);
  }
  @Test
  public void testWidthConstraintEqualToTotalPreferredSize() {
  BlockContainer container = new BlockContainer(new BorderArrangement());
  container.add(block(20.0, 15.0), RectangleEdge.LEFT);
  container.add(block(60.0, 30.0), null);
  container.add(block(20.0, 15.0), RectangleEdge.RIGHT);
  Size2D size = container.arrange(g2, fixedWidth(100.0));
  assertEquals(100.0, size.getWidth(), EPS);
  assertEquals(30.0, size.getHeight(), EPS);
  }
  @Test
  public void testFixedWidthAndHeightArrangeAllEdges() {
  BlockContainer container = new BlockContainer(new BorderArrangement());
  container.add(block(80.0, 5.0), RectangleEdge.TOP);
  container.add(block(80.0, 6.0), RectangleEdge.BOTTOM);
  container.add(block(14.0, 7.0), RectangleEdge.LEFT);
  container.add(block(16.0, 8.0), RectangleEdge.RIGHT);
  container.add(block(40.0, 9.0), null);
  Size2D size = container.arrange(g2, fixedSize(100.0, 50.0));
  assertEquals(100.0, size.getWidth(), EPS);
  assertEquals(50.0, size.getHeight(), EPS);
  }
  @Test
  public void testSmallHeightConstraintArrangeAllEdges() {
  BlockContainer container = new BlockContainer(new BorderArrangement());
  container.add(block(10.0, 10.0), RectangleEdge.TOP);
  container.add(block(10.0, 10.0), RectangleEdge.BOTTOM);
  container.add(block(5.0, 5.0), RectangleEdge.LEFT);
  container.add(block(5.0, 5.0), RectangleEdge.RIGHT);
  container.add(block(5.0, 5.0), null);
  Size2D size = container.arrange(g2, fixedSize(20.0, 15.0));
  assertEquals(20.0, size.getWidth(), EPS);
  assertEquals(15.0, size.getHeight(), EPS);
  }
  @Test
  public void testRangeConstraintsArrangeCenterBlock() {
  BlockContainer container = new BlockContainer(new BorderArrangement());
  container.add(block(40.0, 30.0), null);
  RectangleConstraint constraint = new RectangleConstraint(
          new Range(20.0, 200.0), new Range(20.0, 200.0));
  Size2D size = container.arrange(g2, constraint);
  assertEquals(40.0, size.getWidth(), EPS);
  assertEquals(30.0, size.getHeight(), EPS);
  }
  @Test
  public void testBorderArrangementWithoutConstraintComputesSize() {
  BlockContainer container = new BlockContainer(new BorderArrangement());
  container.add(block(80.0, 10.0), RectangleEdge.TOP);
  container.add(block(90.0, 15.0), RectangleEdge.BOTTOM);
  container.add(block(20.0, 40.0), RectangleEdge.LEFT);
  container.add(block(30.0, 35.0), RectangleEdge.RIGHT);
  container.add(block(40.0, 45.0), null);
  Size2D size = container.arrange(g2, RectangleConstraint.NONE);
  // width = max(top, bottom, left + center + right)
  assertEquals(90.0, size.getWidth(), EPS);
  // height = top + max(left/right, center) + bottom
  assertEquals(70.0, size.getHeight(), EPS);
  }
  @Test
  public void testWidthConstraintSmallerThanLeftBlock() {
  BlockContainer container = new BlockContainer(new BorderArrangement());
  container.add(block(200.0, 100.0), RectangleEdge.LEFT);
  container.add(block(100.0, 50.0), RectangleEdge.RIGHT);
  container.add(block(100.0, 50.0), null);
  Size2D size = container.arrange(g2, fixedWidth(100.0));
  assertEquals(100.0, size.getWidth(), EPS);
  assertTrue(size.getHeight() >= 0.0);
  }
  @Test
  public void testClearRemovesAllBlocks() {
  BorderArrangement arrangement = new BorderArrangement();
  BlockContainer container = new BlockContainer(arrangement);
  container.add(block(100.0, 50.0), RectangleEdge.TOP);
  assertEquals(100.0,
          container.arrange(g2, RectangleConstraint.NONE).getWidth(), EPS);
  arrangement.clear();
  Size2D size = container.arrange(g2, RectangleConstraint.NONE);
  assertEquals(0.0, size.getWidth(), EPS);
  assertEquals(0.0, size.getHeight(), EPS);
  }
  @Test
  public void testEquals() {
  EmptyBlock top = block(10.0, 10.0);
  EmptyBlock left = block(20.0, 20.0);
  BorderArrangement a1 = new BorderArrangement();
  a1.add(top, RectangleEdge.TOP);
  a1.add(left, RectangleEdge.LEFT);
  BorderArrangement a2 = new BorderArrangement();
  a2.add(top, RectangleEdge.TOP);
  a2.add(left, RectangleEdge.LEFT);
  assertTrue(a1.equals(a1));
  assertTrue(a1.equals(a2));
  a2.add(block(5.0, 5.0), RectangleEdge.BOTTOM);
  assertFalse(a1.equals(a2));
  assertFalse(a1.equals(new Object()));
  assertFalse(a1.equals(null));
  }

}
