package org.apache.commons.math3.genetics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.junit.Test;

/**

 - Tests for {@link ElitisticListPopulation} focusing on elitism rate validation
 - in constructors and {@code setElitismRate}.
  */
 public class ElitisticListPopulationTest {
  // dummy Chromosome implementation to create chromosome lists
  private static final class DummyChromosome extends Chromosome {
  DummyChromosome() {
      super();
  }
  @Override
  public double fitness() {
      return 0.0;
  }
  }
  /** Helper that creates a list with a single dummy chromosome.
  */
  private static List<Chromosome> chromosomeList() {
  List<Chromosome> list = new ArrayList<Chromosome>();
  list.add(new DummyChromosome());
  return list;
  }
  // --- constructors: populationLimit + elitismRate (no chromosomes) ---
  @Test(expected = OutOfRangeException.class)
  public void testConstructorTooLow() {
  new ElitisticListPopulation(10, -0.5);
  }
  @Test(expected = OutOfRangeException.class)
  public void testConstructorTooHigh() {
  new ElitisticListPopulation(10, 1.5);
  }
  @Test
  public void testConstructorValidLowerBoundary() {
  ElitisticListPopulation pop = new ElitisticListPopulation(10, 0.0);
  org.junit.Assert.assertEquals(0.0, pop.getElitismRate(), 0.0);
  }
  @Test
  public void testConstructorValidUpperBoundary() {
  ElitisticListPopulation pop = new ElitisticListPopulation(10, 1.0);
  org.junit.Assert.assertEquals(1.0, pop.getElitismRate(), 0.0);
  }
  @Test
  public void testConstructorValidMiddle() {
  ElitisticListPopulation pop = new ElitisticListPopulation(10, 0.5);
  org.junit.Assert.assertEquals(0.5, pop.getElitismRate(), 0.0);
  }
  // --- constructors: chromosome list + populationLimit + elitismRate ---
  @Test(expected = OutOfRangeException.class)
  public void testChromosomeListConstructorTooLow() {
  new ElitisticListPopulation(chromosomeList(), 10, -0.5);
  }
  @Test(expected = OutOfRangeException.class)
  public void testChromosomeListConstructorTooHigh() {
  new ElitisticListPopulation(chromosomeList(), 10, 1.5);
  }
  @Test
  public void testChromosomeListConstructorValid() {
  ElitisticListPopulation pop = new ElitisticListPopulation(chromosomeList(), 10, 0.75);
  org.junit.Assert.assertEquals(0.75, pop.getElitismRate(), 0.0);
  }
  // --- setElitismRate ---
  @Test(expected = OutOfRangeException.class)
  public void testSetElitismRateTooLow() {
  ElitisticListPopulation pop = new ElitisticListPopulation(10, 0.3);
  pop.setElitismRate(-0.1);
  }
  @Test(expected = OutOfRangeException.class)
  public void testSetElitismRateTooHigh() {
  ElitisticListPopulation pop = new ElitisticListPopulation(10, 0.3);
  pop.setElitismRate(1.2);
  }
  @Test
  public void testSetElitismRateValid() {
  ElitisticListPopulation pop = new ElitisticListPopulation(10, 0.0);
  pop.setElitismRate(0.65);
  org.junit.Assert.assertEquals(0.65, pop.getElitismRate(), 0.0);
  }

}