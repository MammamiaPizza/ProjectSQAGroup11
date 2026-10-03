package org.apache.commons.math3.genetics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ListPopulationIteratorContractTest {

    @Test
    public void iteratorRemoveAfterNextIsUnsupportedAndDoesNotAlterPopulation() {
        final Chromosome first = new TestChromosome(1.0);
        final Chromosome second = new TestChromosome(2.0);
        final List<Chromosome> chromosomes = new ArrayList<Chromosome>();
        chromosomes.add(first);
        chromosomes.add(second);

        final ListPopulation population = new TestPopulation(chromosomes, 3);
        final Iterator<Chromosome> iterator = population.iterator();

        final Chromosome visited = iterator.next();
        try {
            iterator.remove();
            fail("Iterator removal must be unsupported");
        } catch (UnsupportedOperationException expected) {
            // expected
        }

        assertEquals(2, population.getPopulationSize());
        assertTrue(population.getChromosomes().contains(first));
        assertTrue(population.getChromosomes().contains(second));

        int remaining = 0;
        while (iterator.hasNext()) {
            final Chromosome chromosome = iterator.next();
            assertTrue(chromosome == first || chromosome == second);
            remaining++;
        }
        assertEquals(1, remaining);
        assertTrue(population.getChromosomes().contains(visited));
    }

    @Test
    public void iteratorRemoveOnEmptyPopulationIsUnsupported() {
        final ListPopulation population = new TestPopulation(1);
        final Iterator<Chromosome> iterator = population.iterator();

        assertFalse(iterator.hasNext());
        try {
            iterator.remove();
            fail("Iterator removal must be unsupported even without a preceding next call");
        } catch (UnsupportedOperationException expected) {
            // expected
        }

        assertEquals(0, population.getPopulationSize());
    }

    private static final class TestPopulation extends ListPopulation {
        TestPopulation(final int populationLimit) {
            super(populationLimit);
        }

        TestPopulation(final List<Chromosome> chromosomes, final int populationLimit) {
            super(chromosomes, populationLimit);
        }

        public Population nextGeneration() {
            return new TestPopulation(getPopulationLimit());
        }
    }

    private static final class TestChromosome extends Chromosome {
        private final double value;

        TestChromosome(final double value) {
            this.value = value;
        }

        @Override
        public double fitness() {
            return value;
        }
    }
}