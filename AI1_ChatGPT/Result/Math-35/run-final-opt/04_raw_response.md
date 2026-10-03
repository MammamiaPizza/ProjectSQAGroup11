@org.junit.Test
public void testNextGenerationRetainsTheBestChromosomesUsingCeilingBound() {
    org.apache.commons.math3.genetics.Chromosome low = chromosomeWithFitness(1.0);
    org.apache.commons.math3.genetics.Chromosome high = chromosomeWithFitness(3.0);
    org.apache.commons.math3.genetics.Chromosome middle = chromosomeWithFitness(2.0);

    ElitisticListPopulation population = new ElitisticListPopulation(
            java.util.Arrays.<org.apache.commons.math3.genetics.Chromosome>asList(low, high, middle),
            3, 0.5);

    ElitisticListPopulation next =
            (ElitisticListPopulation) population.nextGeneration();

    org.junit.Assert.assertEquals(1, next.getPopulationSize());
    org.junit.Assert.assertSame(high, next.getChromosomes().get(0));
}

@org.junit.Test
public void testNextGenerationWithZeroElitismIsEmpty() {
    ElitisticListPopulation population = new ElitisticListPopulation(
            java.util.Arrays.<org.apache.commons.math3.genetics.Chromosome>asList(
                    chromosomeWithFitness(1.0), chromosomeWithFitness(2.0)),
            2, 0.0);

    ElitisticListPopulation next =
            (ElitisticListPopulation) population.nextGeneration();

    org.junit.Assert.assertEquals(0, next.getPopulationSize());
}

private org.apache.commons.math3.genetics.Chromosome chromosomeWithFitness(final double value) {
    return new org.apache.commons.math3.genetics.Chromosome() {
        @Override
        protected double fitness() {
            return value;
        }

        @Override
        protected boolean isSame(final org.apache.commons.math3.genetics.Chromosome another) {
            return this == another;
        }
    };
}