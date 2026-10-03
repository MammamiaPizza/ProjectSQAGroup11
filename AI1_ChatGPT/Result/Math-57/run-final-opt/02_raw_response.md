package org.apache.commons.math.stat.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KMeansPlusPlusClustererSmallDistanceTest {

    @Test
    public void testTinySquaredDistancesStillSelectWeightedDistantCenter() {
        final EuclideanDoublePoint p0 = new EuclideanDoublePoint(new double[] { 0.000 });
        final EuclideanDoublePoint p1 = new EuclideanDoublePoint(new double[] { 0.001 });
        final EuclideanDoublePoint p2 = new EuclideanDoublePoint(new double[] { 0.002 });
        final EuclideanDoublePoint p3 = new EuclideanDoublePoint(new double[] { 0.003 });

        final List<EuclideanDoublePoint> points = new ArrayList<EuclideanDoublePoint>();
        points.add(p0);
        points.add(p1);
        points.add(p2);
        points.add(p3);

        final KMeansPlusPlusClusterer<EuclideanDoublePoint> clusterer =
                new KMeansPlusPlusClusterer<EuclideanDoublePoint>(new FixedRandom(0, 0.909));

        final List<Cluster<EuclideanDoublePoint>> clusters = clusterer.cluster(points, 2, 0);

        assertEquals(2, clusters.size());
        assertEquals(0.000, clusters.get(0).getCenter().getPoint()[0], 0.0);
        assertEquals(0.003, clusters.get(1).getCenter().getPoint()[0], 0.0);
        assertEquals(2, clusters.get(0).getPoints().size());
        assertEquals(2, clusters.get(1).getPoints().size());
        assertTrue(clusters.get(0).getPoints().contains(p0));
        assertTrue(clusters.get(0).getPoints().contains(p1));
        assertTrue(clusters.get(1).getPoints().contains(p2));
        assertTrue(clusters.get(1).getPoints().contains(p3));
    }

    @Test
    public void testSeparatedPointsAreAssignedToTheirNearestInitialCenters() {
        final EuclideanDoublePoint p0 = new EuclideanDoublePoint(new double[] { 0.0 });
        final EuclideanDoublePoint p1 = new EuclideanDoublePoint(new double[] { 1.0 });
        final EuclideanDoublePoint p2 = new EuclideanDoublePoint(new double[] { 10.0 });
        final EuclideanDoublePoint p3 = new EuclideanDoublePoint(new double[] { 11.0 });

        final List<EuclideanDoublePoint> points = new ArrayList<EuclideanDoublePoint>();
        points.add(p0);
        points.add(p1);
        points.add(p2);
        points.add(p3);

        final KMeansPlusPlusClusterer<EuclideanDoublePoint> clusterer =
                new KMeansPlusPlusClusterer<EuclideanDoublePoint>(new FixedRandom(0, 0.9));

        final List<Cluster<EuclideanDoublePoint>> clusters = clusterer.cluster(points, 2, 10);

        assertEquals(2, clusters.size());
        assertEquals(2, clusters.get(0).getPoints().size());
        assertEquals(2, clusters.get(1).getPoints().size());
        assertTrue(clusters.get(0).getPoints().contains(p0));
        assertTrue(clusters.get(0).getPoints().contains(p1));
        assertTrue(clusters.get(1).getPoints().contains(p2));
        assertTrue(clusters.get(1).getPoints().contains(p3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoreClustersThanPointsIsRejected() {
        final List<EuclideanDoublePoint> points = new ArrayList<EuclideanDoublePoint>();
        points.add(new EuclideanDoublePoint(new double[] { 0.0 }));
        points.add(new EuclideanDoublePoint(new double[] { 1.0 }));

        final KMeansPlusPlusClusterer<EuclideanDoublePoint> clusterer =
                new KMeansPlusPlusClusterer<EuclideanDoublePoint>(new FixedRandom(0, 0.5));

        clusterer.cluster(points, 3, 1);
    }

    private static final class FixedRandom extends Random {
        private static final long serialVersionUID = 1L;

        private final int firstIndex;
        private final double randomValue;

        FixedRandom(final int firstIndex, final double randomValue) {
            this.firstIndex = firstIndex;
            this.randomValue = randomValue;
        }

        @Override
        public int nextInt(final int bound) {
            return firstIndex;
        }

        @Override
        public double nextDouble() {
            return randomValue;
        }
    }
}