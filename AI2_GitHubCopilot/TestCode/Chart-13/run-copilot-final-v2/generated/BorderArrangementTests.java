package org.jfree.chart.block.junit;

import static org.junit.Assert.*;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.jfree.chart.block.Block;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.block.Size2D;
import org.jfree.chart.util.Range;
import org.jfree.chart.util.RectangleEdge;
import org.junit.Before;
import org.junit.Test;

public class BorderArrangementTests {

 private Graphics2D g2;

 @Before
 public void setUp() {
     g2 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
 }

 // A minimal Block that returns a fixed size regardless of constraints
 private static class FixedSizeBlock implements Block {
     private final double width;
     private final double height;
     private Rectangle2D bounds;

     FixedSizeBlock(double width, double height) {
         this.width = width;
         this.height = height;
     }

     @Override
     public Rectangle2D getBounds() {
         return bounds;
     }

     @Override
     public void setBounds(Rectangle2D bounds) {
         this.bounds = (Rectangle2D) bounds.clone();
     }

     @Override
     public Size2D arrange(Graphics2D g2) {
         return new Size2D(width, height);
     }

     @Override
     public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
         return new Size2D(width, height);
     }
 }

 private BlockContainer createContainer(Block top, Block left, Block center, Block right, Block
bottom) {
     BlockContainer container = new BlockContainer();
     BorderArrangement arrangement = new BorderArrangement();
     container.setArrangement(arrangement);
     if (top != null)    container.add(top,    RectangleEdge.TOP);
     if (left != null)   container.add(left,   RectangleEdge.LEFT);
     if (center != null) container.add(center, null);
     if (right != null)  container.add(right,  RectangleEdge.RIGHT);
     if (bottom != null) container.add(bottom, RectangleEdge.BOTTOM);
     return container;
 }

 // -----------------------------------------------------------------------
 // width‑constraint border sizing – no exception, non‑negative results
 // -----------------------------------------------------------------------

 /**
  * Width constraint zero: left block wider than constraint forces
  * inter‑block calculations into tricky territory.  Must not throw.
  */
 @Test
 public void testSizingWithWidthConstraint_ZeroWidth() {
     Block left = new FixedSizeBlock(80, 20);
     BlockContainer container = createContainer(null, left, null, null, null);
     RectangleConstraint c = new RectangleConstraint(0.0, null,
             LengthConstraintType.FIXED, 0.0, null, LengthConstraintType.NONE);
     Size2D size = container.arrange(g2, c);
     assertTrue("Width must be non‑negative", size.width >= 0);
     assertTrue("Height must be non‑negative", size.height >= 0);
     // arrange must not have thrown
 }

 /**
  * Combined left+right blocks exceed the fixed width.
  * Should not produce a negative Range upper bound.
  */
 @Test
 public void testSizingWithWidthConstraint_CombinedExceeds() {
     Block left  = new FixedSizeBlock(100, 30);
     Block right = new FixedSizeBlock(150, 30);
     BlockContainer container = createContainer(null, left, null, right, null);
     double width = 200;
     RectangleConstraint c = new RectangleConstraint(width, null,
             LengthConstraintType.FIXED, 0.0, null, LengthConstraintType.NONE);
     Size2D size = container.arrange(g2, c);
     assertTrue("Width must be non‑negative", size.width >= 0);
     assertTrue("Height must be non‑negative", size.height >= 0);
 }

 /**
  * Width exactly equals combined left+right; boundary condition.
  */
 @Test
 public void testSizingWithWidthConstraint_ExactlyEqualsBorders() {
     Block left  = new FixedSizeBlock(100, 40);
     Block right = new FixedSizeBlock(100, 40);
     BlockContainer container = createContainer(null, left, null, right, null);
     double width = 200;
     RectangleConstraint c = new RectangleConstraint(width, null,
             LengthConstraintType.FIXED, 0.0, null, LengthConstraintType.NONE);
     Size2D size = container.arrange(g2, c);
     assertTrue("Width must be non‑negative", size.width >= 0);
     assertTrue("Height must be non‑negative", size.height >= 0);
 }

 /**
  * Width less than left border alone.
  */
 @Test
 public void testSizingWithWidthConstraint_LeftExceeds() {
     Block left  = new FixedSizeBlock(300, 50);
     BlockContainer container = createContainer(null, left, null, null, null);
     double width = 200;
     RectangleConstraint c = new RectangleConstraint(width, null,
             LengthConstraintType.FIXED, 0.0, null, LengthConstraintType.NONE);
     Size2D size = container.arrange(g2, c);
     assertTrue("Width must be non‑negative", size.width >= 0);
     assertTrue("Height must be non‑negative", size.height >= 0);
 }

 /**
  * Case from the bug report: width constraint triggers negative upper
  * bound when left block consumes most of the width and a right block exists.
  */
 @Test
 public void testSizingWithWidthConstraint_TriggerBug() {
     Block left  = new FixedSizeBlock(200, 20);
     Block right = new FixedSizeBlock(10, 20);
     BlockContainer container = createContainer(null, left, null, right, null);
     double width = 200;
     RectangleConstraint c = new RectangleConstraint(width, null,
             LengthConstraintType.FIXED, 0.0, null, LengthConstraintType.NONE);
     // This must not throw IllegalArgumentException.
     Size2D size = container.arrange(g2, c);
     assertTrue("Width must be non‑negative", size.width >= 0);
     assertTrue("Height must be non‑negative", size.height >= 0);
 }

 // -----------------------------------------------------------------------
 // normal / no‑border / mixed‑content paths
 // -----------------------------------------------------------------------

 /** arrangeNN path – both constraints NONE */
 @Test
 public void testArrangeNN_NoBorders() {
     BlockContainer container = createContainer(null, null, null, null, null);
     RectangleConstraint c = new RectangleConstraint(0.0, null,
             LengthConstraintType.NONE, 0.0, null, LengthConstraintType.NONE);
     Size2D size = container.arrange(g2, c);
     assertEquals(0.0, size.width, 1e-9);
     assertEquals(0.0, size.height, 1e-9);
 }

 /** arrangeNN path – all borders and center */
 @Test
 public void testArrangeNN_FullBorders() {
     Block top    = new FixedSizeBlock(300, 30);
     Block bottom = new FixedSizeBlock(300, 30);
     Block left   = new FixedSizeBlock(80, 50);
     Block right  = new FixedSizeBlock(80, 50);
     Block center = new FixedSizeBlock(140, 50);
     BlockContainer container = createContainer(top, left, center, right, bottom);
     RectangleConstraint c = RectangleConstraint.NONE;
     Size2D size = container.arrange(g2, c);
     assertTrue("Width must be >= max(total borders, center)", size.width >= 300);
     assertTrue("Height must be positive", size.height > 0);
 }

 /** arrangeFF path – both constraints FIXED */
 @Test
 public void testArrangeFF_FullBorders() {
     Block top    = new FixedSizeBlock(250, 20);
     Block bottom = new FixedSizeBlock(250, 20);
     Block left   = new FixedSizeBlock(60, 40);
     Block right  = new FixedSizeBlock(60, 40);
     Block center = new FixedSizeBlock(130, 40);
     BlockContainer container = createContainer(top, left, center, right, bottom);
     RectangleConstraint c = new RectangleConstraint(250, 150);
     Size2D size = container.arrange(g2, c);
     assertTrue("Width must be non‑negative", size.width >= 0);
     assertTrue("Height must be non‑negative", size.height >= 0);
 }

 /** arrangeFR path – fixed width, height range */
 @Test
 public void testArrangeFR_WidthFixed_HeightRange() {
     Block left   = new FixedSizeBlock(50, 30);
     Block right  = new FixedSizeBlock(50, 30);
     Block center = new FixedSizeBlock(100, 30);
     BlockContainer container = createContainer(null, left, center, right, null);
     RectangleConstraint c = new RectangleConstraint(200, new Range(10, 200),
             LengthConstraintType.FIXED, new Range(40, 100));
     Size2D size = container.arrange(g2, c);
     assertTrue("Width must be non‑negative", size.width >= 0);
     assertTrue("Height must be non‑negative", size.height >= 0);
 }

 /** arrangeRR path – both ranges */
 @Test
 public void testArrangeRR_BothRanges() {
     Block top    = new FixedSizeBlock(200, 20);
     Block bottom = new FixedSizeBlock(200, 20);
     BlockContainer container = createContainer(top, null, null, null, bottom);
     RectangleConstraint c = new RectangleConstraint(new Range(100, 400),
             new Range(50, 300));
     Size2D size = container.arrange(g2, c);
     assertTrue("Width must be non‑negative", size.width >= 0);
     assertTrue("Height must be non‑negative", size.height >= 0);
 }

 /** Null blocks: adding null should be ignored without exception */
 @Test
 public void testNullBlocksIgnored() {
     BlockContainer container = new BlockContainer();
     BorderArrangement arrangement = new BorderArrangement();
     container.setArrangement(arrangement);
     container.add(null, RectangleEdge.TOP);
     container.add(null, RectangleEdge.LEFT);
     container.add(new FixedSizeBlock(50, 20), null); // center
     RectangleConstraint c = RectangleConstraint.NONE;
     Size2D size = container.arrange(g2, c);
     assertTrue("Width must be non‑negative", size.width >= 0);
     assertTrue("Height must be non‑negative", size.height >= 0);
 }

}
