package org.apache.commons.math.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.stat.clustering.Cluster;
import org.apache.commons.math.stat.clustering.EuclideanIntegerPoint;
import org.apache.commons.math.stat.clustering.KMeansPlusPlusClusterer;
import org.junit.Test;

public class MathUtilsDistanceRegressionTest {

    @Test
    public void testAllDistanceMethodsReturnZeroForEqualDoublePoints() {
        final double[] point = { -2.5, 0.0, 7.25 };

        assertEquals(0.0, MathUtils.distance1(point, point), 0.0);
        assertEquals(0.0, MathUtils.distance(point, point), 0.0);
        assertEquals(0.0, MathUtils.distanceInf(point, point), 0.0);
    }

    @Test
    public void testAllDistanceMethodsReturnZeroForEqualIntegerPoints() {
        final int[] point = { -4, 0, 9 };

        assertEquals(0, MathUtils.distance1(point, point));
        assertEquals(0.0, MathUtils.distance(point, point), 0.0);
        assertEquals(0, MathUtils.distanceInf(point, point));
    }

    @Test
    public void testDoublePointDistancesUseTheirRespectiveNorms() {
        final double[] first = { -1.0, 2.0, 7.0 };
        final double[] second = { 2.0, -2.0, 4.0 };

        assertEquals(10.0, MathUtils.distance1(first, second), 0.0);
        assertEquals(Math.sqrt(34.0), MathUtils.distance(first, second), 0.0);
        assertEquals(4.0, MathUtils.distanceInf(first, second), 0.0);
    }

    @Test
    public void testIntegerPointDistancesUseTheirRespectiveNorms() {
        final int[] first = { -1, 2, 7 };
        final int[] second = { 2, -2, 4 };

        assertEquals(10, MathUtils.distance1(first, second));
        assertEquals(Math.sqrt(34.0), MathUtils.distance(first, second), 0.0);
        assertEquals(4, MathUtils.distanceInf(first, second));
    }

    @Test
    public void testEmptyPointsHaveZeroDistance() {
        final double[] doubles = new double[0];
        final int[] integers = new int[0];

        assertEquals(0.0, MathUtils.distance1(doubles, doubles), 0.0);
        assertEquals(0.0, MathUtils.distance(doubles, doubles), 0.0);
        assertEquals(0.0, MathUtils.distanceInf(doubles, doubles), 0.0);
        assertEquals(0, MathUtils.distance1(integers, integers));
        assertEquals(0.0, MathUtils.distance(integers, integers), 0.0);
        assertEquals(0, MathUtils.distanceInf(integers, integers));
    }

    @Test
    public void testDegenerateKMeansWithIdenticalPointsCompletes() {
        final List<EuclideanIntegerPoint> points = new ArrayList<EuclideanIntegerPoint>();
        points.add(new EuclideanIntegerPoint(new int[] { 1, 1 }));
        points.add(new EuclideanIntegerPoint(new int[] { 1, 1 }));
        points.add(new EuclideanIntegerPoint(new int[] { 1, 1 }));

        final JDKRandomGenerator random = new JDKRandomGenerator();
        random.setSeed(123456789L);
        final KMeansPlusPlusClusterer<EuclideanIntegerPoint> clusterer =
                new KMeansPlusPlusClusterer<EuclideanIntegerPoint>(random);

        final List<Cluster<EuclideanIntegerPoint>> clusters =
                clusterer.cluster(points, 2, 10);

        assertEquals(2, clusters.size());

        int assigned = 0;
        for (Cluster<EuclideanIntegerPoint> cluster : clusters) {
            assigned += cluster.getPoints().size();
        }
        assertEquals(points.size(), assigned);
    }
}
