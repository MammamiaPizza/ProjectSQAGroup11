import static org.junit.Assert.*;
import org.junit.Test;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;

public class ShapeUtilitiesTests {

 // Test identical paths (same winding rule, same segments, same coordinates)
 @Test
 public void testEqualGeneralPaths_Identical() {
     GeneralPath p1 = new GeneralPath();
     p1.moveTo(0.0f, 0.0f);
     p1.lineTo(1.0f, 1.0f);
     p1.lineTo(2.0f, 0.0f);
     p1.closePath();

     GeneralPath p2 = new GeneralPath();
     p2.moveTo(0.0f, 0.0f);
     p2.lineTo(1.0f, 1.0f);
     p2.lineTo(2.0f, 0.0f);
     p2.closePath();

     assertTrue(ShapeUtilities.equal(p1, p2));
     assertTrue(ShapeUtilities.equal(p2, p1));
 }

 // Test same coordinates but different winding rule (WIND_EVEN_ODD vs WIND_NON_ZERO)
 @Test
 public void testEqualGeneralPaths_DifferentWindingRule() {
     GeneralPath p1 = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
     p1.moveTo(0.0f, 0.0f);
     p1.lineTo(1.0f, 1.0f);
     p1.lineTo(2.0f, 0.0f);
     p1.closePath();

     GeneralPath p2 = new GeneralPath(GeneralPath.WIND_NON_ZERO);
     p2.moveTo(0.0f, 0.0f);
     p2.lineTo(1.0f, 1.0f);
     p2.lineTo(2.0f, 0.0f);
     p2.closePath();

     assertFalse(ShapeUtilities.equal(p1, p2));
 }

 // Test different subpath counts
 @Test
 public void testEqualGeneralPaths_DifferentSubpathCounts() {
     GeneralPath p1 = new GeneralPath();
     p1.moveTo(0.0f, 0.0f);
     p1.lineTo(1.0f, 1.0f);
     p1.closePath();

     GeneralPath p2 = new GeneralPath();
     p2.moveTo(0.0f, 0.0f);
     p2.lineTo(1.0f, 1.0f);
     p2.closePath();
     p2.moveTo(2.0f, 2.0f);
     p2.lineTo(3.0f, 3.0f);
     p2.closePath();

     assertFalse(ShapeUtilities.equal(p1, p2));
 }

 // Test empty vs non-empty
 @Test
 public void testEqualGeneralPaths_EmptyVsNonEmpty() {
     GeneralPath p1 = new GeneralPath();

     GeneralPath p2 = new GeneralPath();
     p2.moveTo(0.0f, 0.0f);
     p2.lineTo(1.0f, 1.0f);

     assertFalse(ShapeUtilities.equal(p1, p2));
 }

 // Both empty paths should be equal
 @Test
 public void testEqualGeneralPaths_BothEmpty() {
     GeneralPath p1 = new GeneralPath();
     GeneralPath p2 = new GeneralPath();
     assertTrue(ShapeUtilities.equal(p1, p2));
 }

 // Test same shape but created with a transform applied (resulting in different segment
coordinates)
 @Test
 public void testEqualGeneralPaths_DifferentTransformedCoordinates() {
     GeneralPath p1 = new GeneralPath();
     p1.moveTo(0.0f, 0.0f);
     p1.lineTo(1.0f, 0.0f);
     p1.closePath();

     GeneralPath p2 = new GeneralPath();
     p2.moveTo(0.0f, 0.0f);
     p2.lineTo(2.0f, 0.0f);
     p2.closePath();

     assertFalse(ShapeUtilities.equal(p1, p2));
 }

 // Test mixed curve and line segments
 @Test
 public void testEqualGeneralPaths_MixedCurveAndLine() {
     GeneralPath p1 = new GeneralPath();
     p1.moveTo(0.0f, 0.0f);
     p1.quadTo(0.5f, 0.5f, 1.0f, 0.0f);
     p1.lineTo(1.0f, 1.0f);

     GeneralPath p2 = new GeneralPath();
     p2.moveTo(0.0f, 0.0f);
     p2.quadTo(0.5f, 0.5f, 1.0f, 0.0f);
     p2.lineTo(1.0f, 1.0f);

     assertTrue(ShapeUtilities.equal(p1, p2));
 }

 // Verify iteration over p2's iterator (not p1 duplicated) - regression test for the bug where
iterator2 uses p1
 @Test
 public void testEqualGeneralPaths_UsesIterator2FromP2() {
     GeneralPath p1 = new GeneralPath();
     p1.moveTo(0.0f, 0.0f);
     p1.lineTo(1.0f, 0.0f);
     p1.closePath();

     GeneralPath p2 = new GeneralPath();
     p2.moveTo(0.0f, 0.0f);
     p2.lineTo(0.0f, 1.0f);
     p2.closePath();

     // The bug in the source: iterator2 = p1.getPathIterator(null) instead of
p2.getPathIterator(null)
     // This means this test would pass even though p1 != p2 because both iterators iterate p1
     // We assert the CORRECT behavior: they should NOT be equal
     assertFalse(ShapeUtilities.equal(p1, p2));
 }

 // Test null arguments: both null
 @Test
 public void testEqualGeneralPaths_BothNull() {
     assertTrue(ShapeUtilities.equal((GeneralPath) null, (GeneralPath) null));
 }

 // Test null arguments: first null, second non-null
 @Test
 public void testEqualGeneralPaths_FirstNull() {
     GeneralPath p2 = new GeneralPath();
     p2.moveTo(0.0f, 0.0f);
     assertFalse(ShapeUtilities.equal((GeneralPath) null, p2));
 }

 // Test null arguments: first non-null, second null
 @Test
 public void testEqualGeneralPaths_SecondNull() {
     GeneralPath p1 = new GeneralPath();
     p1.moveTo(0.0f, 0.0f);
     assertFalse(ShapeUtilities.equal(p1, (GeneralPath) null));
 }

 // Test paths with only MOVE_TO (no segments drawn)
 @Test
 public void testEqualGeneralPaths_OnlyMoveTo() {
     GeneralPath p1 = new GeneralPath();
     p1.moveTo(0.0f, 0.0f);
     p1.moveTo(1.0f, 1.0f);

     GeneralPath p2 = new GeneralPath();
     p2.moveTo(0.0f, 0.0f);
     p2.moveTo(1.0f, 1.0f);

     assertTrue(ShapeUtilities.equal(p1, p2));

     GeneralPath p3 = new GeneralPath();
     p3.moveTo(0.0f, 0.0f);
     p3.moveTo(2.0f, 2.0f);

     assertFalse(ShapeUtilities.equal(p1, p3));
 }

}
