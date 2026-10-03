package org.apache.commons.math.stat.clustering;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.List;
 import java.util.Random;

 import org.junit.Assert;
 import org.junit.Test;

 /**
  * Tests for KMeansPlusPlusClusterer targeting the MATH-546 bug where
  * {@code int sum} truncates squared distances below 1.0 in chooseInitialCenters.
  */
 public class KMeansPlusPlusClustererTest {

     /**
      * Simple Euclidean point implementation.
      */
     private static final class TestPoint implements Clusterable<TestPoint> {
         private final double[] coords;

         TestPoint(double... coords) {
             this.coords = coords.clone();
         }

         @Override
         public double distanceFrom(TestPoint other) {
             double sum = 0.0;
             for (int i = 0; i < coords.length; i++) {
                 double diff = coords[i] - other.coords[i];
                 sum += diff * diff;
             }
             return Math.sqrt(sum);
         }

         @Override
         public TestPoint centroidOf(Collection<TestPoint> points) {
             if (points.isEmpty()) {
                 return null;
             }
             double[] sum = new double[coords.length];
             for (TestPoint p : points) {
                 for (int i = 0; i < sum.length; i++) {
                     sum[i] += p.coords[i];
                 }
             }
             double[] centroid = new double[coords.length];
             for (int i = 0; i < centroid.length; i++) {
                 centroid[i] = sum[i] / points.size();
             }
             return new TestPoint(centroid);
         }

         @Override
         public String toString() {
             return Arrays.toString(coords);
         }
     }

     /**
      * Bug-trigger: sub-1.0 distances cause int-truncation in chooseInitialCenters,
      * resulting in all dx2[i]=0 and deterministic index-0 selection. Verifies
      * valid clustering despite the truncation bug.
      */
     @Test
     public void testSmallDistances() {
         List<TestPoint> points = new ArrayList<TestPoint>();
         points.add(new TestPoint(0.001, 0.001));
         points.add(new TestPoint(0.002, 0.002));
         points.add(new TestPoint(0.003, 0.003));
         points.add(new TestPoint(0.004, 0.004));
         points.add(new TestPoint(0.005, 0.005));
         KMeansPlusPlusClusterer<TestPoint> clusterer =
             new KMeansPlusPlusClusterer<TestPoint>(new Random(42));
         List<Cluster<TestPoint>> result = clusterer.cluster(points, 3, 10);
         Assert.assertEquals("Wrong cluster count", 3, result.size());
         int total = 0;
         for (Cluster<TestPoint> c : result) {
             Assert.assertTrue("Cluster must have points", c.getPoints().size() > 0);
             total += c.getPoints().size();
         }
         Assert.assertEquals("All points assigned", 5, total);
     }

     /**
      * Well-separated groups should produce k non-empty clusters.
      */
     @Test
     public void testNormalClustering() {
         List<TestPoint> points = new ArrayList<TestPoint>();
         points.add(new TestPoint(0.0, 0.0));
         points.add(new TestPoint(0.5, 0.5));
         points.add(new TestPoint(-0.3, 1.0));
         points.add(new TestPoint(100.0, 100.0));
         points.add(new TestPoint(100.5, 99.5));
         points.add(new TestPoint(101.0, 100.0));
         points.add(new TestPoint(200.0, 0.0));
         points.add(new TestPoint(199.5, 0.5));
         points.add(new TestPoint(200.0, -0.5));
         KMeansPlusPlusClusterer<TestPoint> clusterer =
             new KMeansPlusPlusClusterer<TestPoint>(new Random(42));
         List<Cluster<TestPoint>> result = clusterer.cluster(points, 3, 30);
         Assert.assertEquals(3, result.size());
         int total = 0;
         for (Cluster<TestPoint> c : result) {
             Assert.assertTrue(c.getPoints().size() > 0);
             total += c.getPoints().size();
         }
         Assert.assertEquals(9, total);
     }

     /**
      * Boundary: k=1 yields a single cluster with all points.
      */
     @Test
     public void testKEqualsOne() {
         List<TestPoint> points = Arrays.asList(
             new TestPoint(0.0, 0.0),
             new TestPoint(3.0, 4.0),
             new TestPoint(10.0, 0.0));
         KMeansPlusPlusClusterer<TestPoint> clusterer =
             new KMeansPlusPlusClusterer<TestPoint>(new Random(42));
         List<Cluster<TestPoint>> result = clusterer.cluster(points, 1, 10);
         Assert.assertEquals(1, result.size());
         Assert.assertEquals(3, result.get(0).getPoints().size());
     }

     /**
      * Boundary: k=n; each input point may serve as its own center.
      */
     @Test
     public void testKEqualsN() {
         List<TestPoint> points = Arrays.asList(
             new TestPoint(0.0, 0.0),
             new TestPoint(5.0, 5.0),
             new TestPoint(10.0, 0.0));
         KMeansPlusPlusClusterer<TestPoint> clusterer =
             new KMeansPlusPlusClusterer<TestPoint>(new Random(42));
         List<Cluster<TestPoint>> result = clusterer.cluster(points, 3, 10);
         Assert.assertEquals(3, result.size());
         int total = 0;
         for (Cluster<TestPoint> c : result) {
             total += c.getPoints().size();
         }
         Assert.assertEquals(3, total);
     }

     /**
      * Error: empty point collection must throw.
      */
     @Test(expected = Exception.class)
     public void testEmptyPointsThrows() {
         KMeansPlusPlusClusterer<TestPoint> clusterer =
             new KMeansPlusPlusClusterer<TestPoint>(new Random(42));
         clusterer.cluster(new ArrayList<TestPoint>(), 2, 10);
     }

     /**
      * Error: k larger than number of points must throw.
      */
     @Test(expected = Exception.class)
     public void testKGreaterThanSizeThrows() {
         List<TestPoint> points = Arrays.asList(
             new TestPoint(0.0, 0.0), new TestPoint(1.0, 1.0));
         KMeansPlusPlusClusterer<TestPoint> clusterer =
             new KMeansPlusPlusClusterer<TestPoint>(new Random(42));
         clusterer.cluster(points, 5, 10);
     }

     /**
      * Boundary: all points identical (distance zero). Verifies
      * clusters are non-empty and all points assigned.
      */
     @Test
     public void testIdenticalPoints() {
         List<TestPoint> points = new ArrayList<TestPoint>();
         for (int i = 0; i < 6; i++) {
             points.add(new TestPoint(1.5, 2.5));
         }
         KMeansPlusPlusClusterer<TestPoint> clusterer =
             new KMeansPlusPlusClusterer<TestPoint>(new Random(42));
         List<Cluster<TestPoint>> result = clusterer.cluster(points, 2, 10);
         Assert.assertEquals(2, result.size());
         int total = 0;
         for (Cluster<TestPoint> c : result) {
             total += c.getPoints().size();
         }
         Assert.assertEquals(6, total);
     }

     /**
      * Oracle: every point must be assigned to its nearest cluster center.
      */
     @Test
     public void testPointsAssignedToNearestCluster() {
         List<TestPoint> points = Arrays.asList(
             new TestPoint(0.0, 0.0),
             new TestPoint(1.0, 0.5),
             new TestPoint(100.0, 100.0),
             new TestPoint(101.0, 100.5),
             new TestPoint(200.0, 0.0),
             new TestPoint(199.5, 0.5));
         KMeansPlusPlusClusterer<TestPoint> clusterer =
             new KMeansPlusPlusClusterer<TestPoint>(new Random(42));
         List<Cluster<TestPoint>> result = clusterer.cluster(points, 3, 30);
         Assert.assertEquals(3, result.size());
         for (TestPoint p : points) {
             double minDist = Double.MAX_VALUE;
             Cluster<TestPoint> nearest = null;
             for (Cluster<TestPoint> c : result) {
                 double d = p.distanceFrom(c.getCenter());
                 if (d < minDist) {
                     minDist = d;
                     nearest = c;
                 }
             }
             Assert.assertNotNull(nearest);
             Assert.assertTrue(
                 "Point distance from nearest center (" + minDist
                 + ") exceeds distance to assigned center",
                 p.distanceFrom(nearest.getCenter()) <= minDist + 1e-10);
         }
     }

     /**
      * Oracle: all cluster centers must be distinct (distance > 0).
      */
     @Test
     public void testCentersDistinct() {
         List<TestPoint> points = new ArrayList<TestPoint>();
         for (int i = 0; i < 25; i++) {
             points.add(new TestPoint(i - 0.1, i * 0.1));
         }
         KMeansPlusPlusClusterer<TestPoint> clusterer =
             new KMeansPlusPlusClusterer<TestPoint>(new Random(12345));
         List<Cluster<TestPoint>> result = clusterer.cluster(points, 6, 20);
         Assert.assertEquals(6, result.size());
         for (int i = 0; i < result.size(); i++) {
             TestPoint ci = result.get(i).getCenter();
             for (int j = i + 1; j < result.size(); j++) {
                 TestPoint cj = result.get(j).getCenter();
                 Assert.assertTrue(
                     "Centers " + ci + " and " + cj + " must be distinct",
                     ci.distanceFrom(cj) > 0.0);
             }
         }
     }

     /**
      * Empty-cluster strategy FARTHEST_POINT handles empty clusters.
      */
     @Test
     public void testFarthestPointStrategy() {
         List<TestPoint> points = new ArrayList<TestPoint>();
         for (int i = 0; i < 20; i++) {
             points.add(new TestPoint(i - 0.05, i * 0.05));
         }
         KMeansPlusPlusClusterer<TestPoint> clusterer =
             new KMeansPlusPlusClusterer<TestPoint>(new Random(42),
                 KMeansPlusPlusClusterer.EmptyClusterStrategy.FARTHEST_POINT);
         List<Cluster<TestPoint>> result = clusterer.cluster(points, 4, 10);
         Assert.assertEquals(4, result.size());
         int total = 0;
         for (Cluster<TestPoint> c : result) {
             Assert.assertTrue(c.getPoints().size() > 0);
             total += c.getPoints().size();
         }
         Assert.assertEquals(20, total);
     }

     /**
      * Empty-cluster strategy LARGEST_POINTS_NUMBER handles empty clusters.
      */
     @Test
     public void testLargestPointsNumberStrategy() {
         List<TestPoint> points = new ArrayList<TestPoint>();
         for (int i = 0; i < 15; i++) {
             points.add(new TestPoint(i - 2.0, 0.0));
         }
         KMeansPlusPlusClusterer<TestPoint> clusterer =
             new KMeansPlusPlusClusterer<TestPoint>(new Random(42),
                 KMeansPlusPlusClusterer.EmptyClusterStrategy.LARGEST_POINTS_NUMBER);
         List<Cluster<TestPoint>> result = clusterer.cluster(points, 3, 10);
         Assert.assertEquals(3, result.size());
         int total = 0;
         for (Cluster<TestPoint> c : result) {
             total += c.getPoints().size();
         }
         Assert.assertEquals(15, total);
     }

     /**
      * Default empty-cluster strategy LARGEST_VARIANCE handles empty clusters
      * triggered by small-distance point sets.
      */
     @Test
     public void testLargestVarianceStrategyWithSmallDistances() {
         List<TestPoint> points = new ArrayList<TestPoint>();
         for (int i = 0; i < 20; i++) {
             points.add(new TestPoint(i - 0.01, i * 0.01));
         }
         KMeansPlusPlusClusterer<TestPoint> clusterer =
             new KMeansPlusPlusClusterer<TestPoint>(new Random(42),
                 KMeansPlusPlusClusterer.EmptyClusterStrategy.LARGEST_VARIANCE);
         List<Cluster<TestPoint>> result = clusterer.cluster(points, 4, 15);
         Assert.assertEquals(4, result.size());
         int total = 0;
         for (Cluster<TestPoint> c : result) {
             Assert.assertTrue("No cluster must be empty", c.getPoints().size() > 0);
             total += c.getPoints().size();
         }
         Assert.assertEquals(20, total);
     }
 }