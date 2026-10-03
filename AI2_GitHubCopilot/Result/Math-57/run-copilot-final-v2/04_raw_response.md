@Test
public void testEmptyClusterLargestNumber() {
    java.util.Random random = new java.util.Random(123456);
    KMeansPlusPlusClusterer<EuclideanIntegerPoint> clusterer =
        new KMeansPlusPlusClusterer<EuclideanIntegerPoint>(
            random, EmptyClusterStrategy.LARGEST_POINTS_NUMBER);
    java.util.ArrayList<EuclideanIntegerPoint> points = new
java.util.ArrayList<EuclideanIntegerPoint>();
    points.add(new EuclideanIntegerPoint(new int[] {1, 1}));
    points.add(new EuclideanIntegerPoint(new int[] {2, 2}));
    points.add(new EuclideanIntegerPoint(new int[] {3 3}));
    points.add(new EuclideanIntegerPoint(new int[] {100 100}));
    points.add(new EuclideanIntegerPoint(new int[] {101 101}));
    points.add(new EuclideanIntegerPoint(new int[] {102 102}));
    java.util.List<Cluster<EuclideanIntegerPoint>> result = clusterer.cluster(points, 3, 100);
    org.junit.Assert.assertNotNull(result);
    org.junit.Assert.assertFalse(result.isEmpty());
}

@Test
public void testEmptyClusterFarthestPoint() {
    java.util.Random random = new java.util.Random(654321);
    KMeansPlusPlusClusterer<EuclideanIntegerPoint> clusterer =
        new KMeansPlusPlusClusterer<EuclideanIntegerPoint>(
            random, EmptyClusterStrategy.FARTHEST_POINT);
    java.util.ArrayList<EuclideanIntegerPoint> points = new
java.util.ArrayList<EuclideanIntegerPoint>();
    points.add(new EuclideanIntegerPoint(new int[] {0, 0}));
    points.add(new EuclideanIntegerPoint(new int[] {1, 1}));
    points.add(new EuclideanIntegerPoint(new int[] {2, 2}));
    points.add(new EuclideanIntegerPoint(new int[] {200, 200}));
    points.add(new EuclideanIntegerPoint(new int[] {201, 201}));
    points.add(new EuclideanIntegerPoint(new int[] {202, 202}));
    java.util.List<Cluster<EuclideanIntegerPoint>> result = clusterer.cluster(points, 3, 100);
    org.junit.Assert.assertNotNull(result);
    org.junit.Assert.assertFalse(result.isEmpty());
}

@Test
public void testConvergenceNoChange() {
    java.util.Random random = new java.util.Random(999);
    KMeansPlusPlusClusterer<EuclideanIntegerPoint> clusterer =
        new KMeansPlusPlusClusterer<EuclideanIntegerPoint>(random);
    java.util.ArrayList<EuclideanIntegerPoint> points = new
java.util.ArrayList<EuclideanIntegerPoint>();
    // Two well-separated groups of identical points
    points.add(new EuclideanIntegerPoint(new int[] {0, 0}));
    points.add(new EuclideanIntegerPoint(new int[] {0, 0}));
    points.add(new EuclideanIntegerPoint(new int[] {0, 0}));
    points.add(new EuclideanIntegerPoint(new int[] {50, 50}));
    points.add(new EuclideanIntegerPoint(new int[] {50, 50}));
    points.add(new EuclideanIntegerPoint(new int[] {50, 50}));
    java.util.List<Cluster<EuclideanIntegerPoint>> result = clusterer.cluster(points, 2, 100);
    org.junit.Assert.assertEquals(2, result.size());
    // The algorithm should converge quickly, triggering the early return
}