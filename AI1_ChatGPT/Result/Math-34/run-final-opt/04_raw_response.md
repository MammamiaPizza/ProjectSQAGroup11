@org.junit.Test
public void constructorRejectsNullInvalidLimitAndOversizedChromosomeList() {
    try {
        new TestPopulation((java.util.List<org.apache.commons.math3.genetics.Chromosome>) null, 1);
        org.junit.Assert.fail("A null chromosome list must be rejected");
    } catch (org.apache.commons.math3.exception.NullArgumentException expected) {
        // expected
    }

    try {
        new TestPopulation(0);
        org.junit.Assert.fail("A non-positive population limit must be rejected");
    } catch (org.apache.commons.math3.exception.NotPositiveException expected) {
        // expected
    }

    try {
        new TestPopulation(chromosomes(new TestChromosome(1), new TestChromosome(2)), 1);
        org.junit.Assert.fail("A chromosome list larger than the limit must be rejected");
    } catch (org.apache.commons.math3.exception.NumberIsTooLargeException expected) {
        // expected
    }
}

@org.junit.Test
public void setChromosomesAndAddChromosomesRespectPopulationLimit() {
    TestPopulation population = new TestPopulation(3);
    TestChromosome first = new TestChromosome(1);
    TestChromosome second = new TestChromosome(2);
    TestChromosome third = new TestChromosome(3);
    TestChromosome fourth = new TestChromosome(4);

    population.setChromosomes(chromosomes(first, second));
    org.junit.Assert.assertEquals(2, population.getPopulationSize());
    org.junit.Assert.assertSame(first, population.getChromosomes().get(0));
    org.junit.Assert.assertSame(second, population.getChromosomes().get(1));

    try {
        population.addChromosomes(chromosomes(third, fourth));
        org.junit.Assert.fail("Adding chromosomes beyond the limit must be rejected");
    } catch (org.apache.commons.math3.exception.NumberIsTooLargeException expected) {
        // expected
    }

    org.junit.Assert.assertEquals(2, population.getPopulationSize());
    population.addChromosomes(chromosomes(third));
    org.junit.Assert.assertEquals(3, population.getPopulationSize());

    try {
        population.setChromosomes(chromosomes(first, second, third, fourth));
        org.junit.Assert.fail("Replacing chromosomes with a list beyond the limit must be rejected");
    } catch (org.apache.commons.math3.exception.NumberIsTooLargeException expected) {
        // expected
    }

    org.junit.Assert.assertEquals(3, population.getPopulationSize());
}

@org.junit.Test
public void addChromosomeFindsFittestChromosomeAndRejectsOverflow() {
    TestChromosome lessFit = new TestChromosome(1);
    TestChromosome fittest = new TestChromosome(5);
    TestPopulation population = new TestPopulation(chromosomes(lessFit), 2);

    population.addChromosome(fittest);

    org.junit.Assert.assertEquals(2, population.getPopulationSize());
    org.junit.Assert.assertSame(fittest, population.getFittestChromosome());

    try {
        population.addChromosome(new TestChromosome(10));
        org.junit.Assert.fail("Adding a chromosome to a full population must be rejected");
    } catch (org.apache.commons.math3.exception.NumberIsTooLargeException expected) {
        // expected
    }

    org.junit.Assert.assertEquals(2, population.getPopulationSize());
    org.junit.Assert.assertSame(fittest, population.getFittestChromosome());
}

private static java.util.List<org.apache.commons.math3.genetics.Chromosome> chromosomes(
        TestChromosome... chromosomes) {
    java.util.List<org.apache.commons.math3.genetics.Chromosome> result =
            new java.util.ArrayList<org.apache.commons.math3.genetics.Chromosome>();
    for (TestChromosome chromosome : chromosomes) {
        result.add(chromosome);
    }
    return result;
}

private static final class TestPopulation extends org.apache.commons.math3.genetics.ListPopulation {
    TestPopulation(int populationLimit) {
        super(populationLimit);
    }

    TestPopulation(java.util.List<org.apache.commons.math3.genetics.Chromosome> chromosomes,
                   int populationLimit) {
        super(chromosomes, populationLimit);
    }

    @Override
    public org.apache.commons.math3.genetics.Population nextGeneration() {
        return this;
    }
}

private static final class TestChromosome extends org.apache.commons.math3.genetics.Chromosome {
    private final double value;

    TestChromosome(double value) {
        this.value = value;
    }

    @Override
    protected double fitness() {
        return value;
    }
}