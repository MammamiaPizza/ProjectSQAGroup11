package org.apache.commons.math3.genetics;

import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.junit.Assert;
import org.junit.Test;

public class ElitisticListPopulationValidationTest {

    @Test(expected = OutOfRangeException.class)
    public void testPopulationLimitConstructorRejectsNegativeElitismRate() {
        new ElitisticListPopulation(10, -0.01);
    }

    @Test(expected = OutOfRangeException.class)
    public void testPopulationLimitConstructorRejectsElitismRateAboveOne() {
        new ElitisticListPopulation(10, 1.01);
    }

    @Test(expected = OutOfRangeException.class)
    public void testChromosomeListConstructorRejectsNegativeElitismRate() {
        List<Chromosome> chromosomes = Collections.emptyList();
        new ElitisticListPopulation(chromosomes, 10, -0.01);
    }

    @Test(expected = OutOfRangeException.class)
    public void testChromosomeListConstructorRejectsElitismRateAboveOne() {
        List<Chromosome> chromosomes = Collections.emptyList();
        new ElitisticListPopulation(chromosomes, 10, 1.01);
    }

    @Test
    public void testBoundaryElitismRatesAreAcceptedByPopulationLimitConstructor() {
        ElitisticListPopulation noElitism = new ElitisticListPopulation(10, 0.0);
        ElitisticListPopulation fullElitism = new ElitisticListPopulation(10, 1.0);

        Assert.assertEquals(0.0, noElitism.getElitismRate(), 0.0);
        Assert.assertEquals(1.0, fullElitism.getElitismRate(), 0.0);
    }

    @Test
    public void testBoundaryElitismRatesAreAcceptedByChromosomeListConstructor() {
        List<Chromosome> chromosomes = Collections.emptyList();

        ElitisticListPopulation noElitism =
                new ElitisticListPopulation(chromosomes, 10, 0.0);
        ElitisticListPopulation fullElitism =
                new ElitisticListPopulation(chromosomes, 10, 1.0);

        Assert.assertEquals(0.0, noElitism.getElitismRate(), 0.0);
        Assert.assertEquals(1.0, fullElitism.getElitismRate(), 0.0);
    }

    @Test
    public void testSetterRejectsOutOfRangeRatesAndKeepsCurrentRate() {
        ElitisticListPopulation population = new ElitisticListPopulation(10, 0.5);

        try {
            population.setElitismRate(-0.01);
            Assert.fail("Expected OutOfRangeException for negative elitism rate");
        } catch (OutOfRangeException expected) {
            Assert.assertEquals(0.5, population.getElitismRate(), 0.0);
        }

        try {
            population.setElitismRate(1.01);
            Assert.fail("Expected OutOfRangeException for elitism rate above one");
        } catch (OutOfRangeException expected) {
            Assert.assertEquals(0.5, population.getElitismRate(), 0.0);
        }
    }
}
