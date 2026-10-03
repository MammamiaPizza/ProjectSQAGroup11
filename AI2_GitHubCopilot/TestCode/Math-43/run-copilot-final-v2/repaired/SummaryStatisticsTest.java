package org.apache.commons.math.stat.descriptive;

 import org.apache.commons.math.stat.descriptive.moment.GeometricMean;
 import org.apache.commons.math.stat.descriptive.moment.Mean;
 import org.apache.commons.math.stat.descriptive.moment.Variance;

 import org.junit.Assert;
 import org.junit.Test;

 public class SummaryStatisticsTest {

     private static final double EPSILON = 1e-12;

     @Test
     public void testOverrideMean() {
         SummaryStatistics stats = new SummaryStatistics();
         stats.setMeanImpl(new Mean());
         stats.addValue(1);
         stats.addValue(4);
         Assert.assertEquals(2.5, stats.getMean(), EPSILON);
     }

     @Test
     public void testOverrideGeoMean() {
         SummaryStatistics stats = new SummaryStatistics();
         stats.setGeoMeanImpl(new GeometricMean());
         stats.addValue(2);
         stats.addValue(4);
         stats.addValue(8);
         // geometric mean of 2,4,8 is 64^(1/3)=4.0
         Assert.assertEquals(4.0, stats.getGeometricMean(), EPSILON);
     }

     @Test
     public void testOverrideVariance() {
         SummaryStatistics stats = new SummaryStatistics();
         stats.setVarianceImpl(new Variance());
         stats.addValue(1);
         stats.addValue(2);
         stats.addValue(3);
         stats.addValue(4);
         // sample variance of{1,2,3,4} = 5/3 ≈ 1.6666667
         Assert.assertEquals(5.0 / 3.0, stats.getVariance(), 1e-12);
     }

     @Test
     public void testOverrideMeanEmpty() {
         SummaryStatistics stats = new SummaryStatistics();
         stats.setMeanImpl(new Mean());
         Assert.assertTrue(Double.isNaN(stats.getMean()));
     }

     @Test
     public void testOverrideGeoMeanEmpty() {
         SummaryStatistics stats = new SummaryStatistics();
         stats.setGeoMeanImpl(new GeometricMean());
         Assert.assertTrue(Double.isNaN(stats.getGeometricMean()));
     }

     @Test
     public void testOverrideVarianceEmpty() {
         SummaryStatistics stats = new SummaryStatistics();
         stats.setVarianceImpl(new Variance());
         Assert.assertTrue(Double.isNaN(stats.getVariance()));
     }

     @Test
     public void testCopyAfterOverrideMean() {
         SummaryStatistics stats = new SummaryStatistics();
         stats.setMeanImpl(new Mean());
         stats.addValue(10);
         stats.addValue(20);
         SummaryStatistics copy = stats.copy();
         Assert.assertEquals(stats.getMean(), copy.getMean(), EPSILON);
         // ensure subsequent adds do not affect copy
         copy.addValue(30);
         Assert.assertEquals(15.0, stats.getMean(), EPSILON);
         Assert.assertEquals(20.0, copy.getMean(), EPSILON);
     }

     @Test
     public void testCopyAfterOverrideVariance() {
         SummaryStatistics stats = new SummaryStatistics();
         stats.setVarianceImpl(new Variance());
         stats.addValue(1);
         stats.addValue(2);
         stats.addValue(3);
         SummaryStatistics copy = stats.copy();
         Assert.assertEquals(stats.getVariance(), copy.getVariance(), EPSILON);
     }

     @Test
     public void testClearAfterOverride() {
         SummaryStatistics stats = new SummaryStatistics();
         stats.setMeanImpl(new Mean());
         stats.setVarianceImpl(new Variance());
         stats.addValue(5);
         stats.addValue(8);
         stats.clear();
         Assert.assertTrue(Double.isNaN(stats.getMean()));
         Assert.assertTrue(Double.isNaN(stats.getVariance()));
         Assert.assertEquals(0, stats.getN());
     }

     @Test
     public void testMultipleOverrideMean() {
         SummaryStatistics stats = new SummaryStatistics();
         stats.setMeanImpl(new Mean());
         stats.addValue(1);
         stats.addValue(2);
         // first impl: mean=1.5
         Assert.assertEquals(1.5, stats.getMean(), EPSILON);
         // replace and add more
         stats.setMeanImpl(new Mean());
         stats.addValue(100);
         stats.addValue(200);
         // new impl only sees later values
         Assert.assertEquals(150.0, stats.getMean(), EPSILON);
     }

     @Test
     public void testOverrideGeoMeanAfterOverride() {
         SummaryStatistics stats = new SummaryStatistics();
         GeometricMean gm = new GeometricMean();
         stats.setGeoMeanImpl(gm);
         stats.addValue(3);
         stats.addValue(12);
         // sqrt(36)=6
         Assert.assertEquals(6.0, stats.getGeometricMean(), EPSILON);
     }

     @Test
     public void testOverrideVariansWithPopulationVariance() {
         SummaryStatistics stats = new SummaryStatistics();
         stats.setVarianceImpl(new Variance());
         stats.addValue(2);
         stats.addValue(4);
         stats.addValue(6);
         // sample variance = (4+0+4)/2=4, population variance = (4+0+4)/3= 8/3 ≈2.6667
         // getVariance returns sample variance (default biasCorrected=true)
         Assert.assertEquals(4.0, stats.getVariance(), EPSILON);
         Assert.assertEquals(8.0/3.0, stats.getPopulationVariance(), 1e-12);
     }
 }
