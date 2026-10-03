package org.jfree.chart.block.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Rectangle2D;

import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.Size2D;
import org.junit.Test;

public class BorderArrangementGeneratedTest {

    private RectangleConstraint fixedWidth(double width) {
        return new RectangleConstraint(width, null,
                LengthConstraintType.FIXED, 0.0, null,
                LengthConstraintType.NONE);
    }

    @Test
    public void normalFixedWidthLayoutUsesRequestedWidthAndPlacesBlocks() {
        BorderArrangement arrangement = new BorderArrangement();
        BlockContainer container = new BlockContainer(arrangement);
        EmptyBlock top = new EmptyBlock(2.0, 2.0);
        EmptyBlock bottom = new EmptyBlock(3.0, 3.0);
        EmptyBlock left = new EmptyBlock(4.0, 5.0);
        EmptyBlock right = new EmptyBlock(3.0, 6.0);
        EmptyBlock center = new EmptyBlock(8.0, 7.0);

        container.add(top, RectangleEdge.TOP);
        container.add(bottom, RectangleEdge.BOTTOM);
        container.add(left, RectangleEdge.LEFT);
        container.add(right, RectangleEdge.RIGHT);
        container.add(center, null);

        Size2D size = arrangement.arrange(container, null, fixedWidth(20.0));

        assertEquals(20.0, size.getWidth(), 0.0000001);
        assertEquals(12.0, size.getHeight(), 0.0000001);
        assertEquals(20.0, top.getBounds().getWidth(), 0.0000001);
        assertEquals(2.0, top.getBounds().getHeight(), 0.0000001);
        assertEquals(2.0, bottom.getBounds().getY(), 0.0000001);
        assertEquals(4.0, left.getBounds().getWidth(), 0.0000001);
        assertEquals(3.0, right.getBounds().getWidth(), 0.0000001);
        assertEquals(4.0, center.getBounds().getX(), 0.0000001);
        assertEquals(13.0, center.getBounds().getWidth(), 0.0000001);
    }

    @Test
    public void fixedWidthExactlyConsumedBySideBlocksLeavesZeroWidthForCenter() {
        BorderArrangement arrangement = new BorderArrangement();
        BlockContainer container = new BlockContainer(arrangement);
        EmptyBlock left = new EmptyBlock(6.0, 3.0);
        EmptyBlock right = new EmptyBlock(4.0, 2.0);
        EmptyBlock center = new EmptyBlock(5.0, 1.0);

        container.add(left, RectangleEdge.LEFT);
        container.add(right, RectangleEdge.RIGHT);
        container.add(center, null);

        Size2D size = arrangement.arrange(container, null, fixedWidth(10.0));

        assertEquals(10.0, size.getWidth(), 0.0000001);
        assertEquals(3.0, size.getHeight(), 0.0000001);
        assertEquals(6.0, left.getBounds().getWidth(), 0.0000001);
        assertEquals(4.0, right.getBounds().getWidth(), 0.0000001);
        assertEquals(0.0, center.getBounds().getWidth(), 0.0000001);
        assertEquals(6.0, center.getBounds().getX(), 0.0000001);
    }

    @Test
    public void fixedWidthSmallerThanPreferredSideWidthsDoesNotCreateNegativeBounds() {
        BorderArrangement arrangement = new BorderArrangement();
        BlockContainer container = new BlockContainer(arrangement);
        EmptyBlock left = new EmptyBlock(12.3, 4.0);
        EmptyBlock right = new EmptyBlock(5.6, 3.0);
        EmptyBlock center = new EmptyBlock(7.8, 2.0);

        container.add(left, RectangleEdge.LEFT);
        container.add(right, RectangleEdge.RIGHT);
        container.add(center, null);

        Size2D size = arrangement.arrange(container, null, fixedWidth(10.0));

        assertEquals(10.0, size.getWidth(), 0.0000001);
        assertEquals(4.0, size.getHeight(), 0.0000001);
        assertTrue(left.getBounds().getWidth() >= 0.0);
        assertTrue(right.getBounds().getWidth() >= 0.0);
        assertTrue(center.getBounds().getWidth() >= 0.0);
        assertEquals(10.0, left.getBounds().getWidth(), 0.0000001);
        assertEquals(0.0, right.getBounds().getWidth(), 0.0000001);
        assertEquals(0.0, center.getBounds().getWidth(), 0.0000001);
    }

    @Test
    public void decimalWidthConstraintWithOversizedBorderBlocksRemainsValid() {
        BorderArrangement arrangement = new BorderArrangement();
        BlockContainer container = new BlockContainer(arrangement);
        EmptyBlock left = new EmptyBlock(9.0, 4.0);
        EmptyBlock right = new EmptyBlock(5.6, 3.0);
        EmptyBlock center = new EmptyBlock(7.8, 2.0);

        container.add(left, RectangleEdge.LEFT);
        container.add(right, RectangleEdge.RIGHT);
        container.add(center, null);

        Size2D size = arrangement.arrange(container, null, fixedWidth(11.1));

        assertEquals(11.1, size.getWidth(), 0.0000001);
        assertTrue(size.getHeight() >= 4.0);
        assertTrue(left.getBounds().getWidth() >= 0.0);
        assertTrue(right.getBounds().getWidth() >= 0.0);
        assertTrue(center.getBounds().getWidth() >= 0.0);
    }

    @Test
    public void clearRemovesPreviouslyAssignedBlocksFromLayout() {
        BorderArrangement arrangement = new BorderArrangement();
        BlockContainer container = new BlockContainer(arrangement);
        container.add(new EmptyBlock(4.0, 5.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(6.0, 7.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(8.0, 9.0), null);

        arrangement.clear();
        Size2D size = arrangement.arrange(container, null, fixedWidth(20.0));

        assertEquals(0.0, size.getWidth(), 0.0000001);
        assertEquals(0.0, size.getHeight(), 0.0000001);
    }

    @Test
    public void equalsReflectsAssignedBlocksAndHandlesOtherObjects() {
        BorderArrangement first = new BorderArrangement();
        BorderArrangement second = new BorderArrangement();
        EmptyBlock block = new EmptyBlock(2.0, 3.0);

        first.add(block, RectangleEdge.LEFT);
        second.add(block, RectangleEdge.LEFT);

        assertTrue(first.equals(first));
        assertTrue(first.equals(second));
        assertTrue(second.equals(first));
        assertFalse(first.equals(null));
        assertFalse(first.equals("not an arrangement"));

        second.add(new EmptyBlock(2.0, 3.0), RectangleEdge.RIGHT);
        assertFalse(first.equals(second));
    }

    @Test
    public void unconstrainedLayoutProvidesBoundsForAllAssignedBlocks() {
        BorderArrangement arrangement = new BorderArrangement();
        BlockContainer container = new BlockContainer(arrangement);
        EmptyBlock top = new EmptyBlock(3.0, 2.0);
        EmptyBlock left = new EmptyBlock(4.0, 5.0);
        EmptyBlock center = new EmptyBlock(6.0, 7.0);

        container.add(top, RectangleEdge.TOP);
        container.add(left, RectangleEdge.LEFT);
        container.add(center, null);

        Size2D size = arrangement.arrange(container, null, RectangleConstraint.NONE);

        assertEquals(10.0, size.getWidth(), 0.0000001);
        assertEquals(9.0, size.getHeight(), 0.0000001);
        Rectangle2D centerBounds = center.getBounds();
        assertNotNull(centerBounds);
        assertEquals(4.0, centerBounds.getX(), 0.0000001);
        assertEquals(2.0, centerBounds.getY(), 0.0000001);
        assertEquals(6.0, centerBounds.getWidth(), 0.0000001);
        assertEquals(7.0, centerBounds.getHeight(), 0.0000001);
    }
}