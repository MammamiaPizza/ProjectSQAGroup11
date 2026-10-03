package org.apache.commons.math3.genetics;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.List;
 import java.util.NoSuchElementException;

 import org.apache.commons.math3.exception.NotPositiveException;
 import org.apache.commons.math3.exception.NullArgumentException;
 import org.apache.commons.math3.exception.NumberIsTooLargeException;
 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link ListPopulation} with focus on the
  * {@code iterator()} contract (see MATH-779).
  */
 public class ListPopulationTest {

     /* -------- test doubles -------- */

     /** Concrete chromosome for unit testing. */
     private static final class SimpleChromosome extends Chromosome {
         private final double fitness;
         SimpleChromosome(double fitness) {
             super();
             this.fitness = fitness;
         }
         @Override public double fitness() { return fitness; }
     }

     /** Concrete population for unit testing. */
     private static final class SimpleListPopulation extends ListPopulation {
         SimpleListPopulation(List<Chromosome> chromosomes, int limit) {
             super(chromosomes, limit);
         }
         SimpleListPopulation(int limit) { super(limit); }
     }

     /* -------- iterator.remove() contract (MATH-779) -------- */

     @Test(expected = UnsupportedOperationException.class)
     public void testIteratorRemoveAfterNext() {
         SimpleListPopulation pop = new SimpleListPopulation(10);
         pop.addChromosome(new SimpleChromosome(1.0));
         pop.addChromosome(new SimpleChromosome(2.0));
         Iterator<Chromosome> it = pop.iterator();
         it.next();
         it.remove();   // must throw UnsupportedOperationException
     }

     @Test(expected = UnsupportedOperationException.class)
     public void testIteratorRemoveOnEmptyPopulation() {
         SimpleListPopulation pop = new SimpleListPopulation(5);
         Iterator<Chromosome> it = pop.iterator();
         it.remove();   // must throw even without calling next()
     }

     @Test
     public void testIteratorRemoveDoesNotModifyPopulation() {
         SimpleListPopulation pop = new SimpleListPopulation(10);
         pop.addChromosome(new SimpleChromosome(1.0));
         pop.addChromosome(new SimpleChromosome(2.0));
         int sizeBefore = pop.getPopulationSize();
         Iterator<Chromosome> it = pop.iterator();
         it.next();
         try {
             it.remove();
         } catch (UnsupportedOperationException ok) { /* expected */ }
         assertEquals("population size must not change after remove()",
                      sizeBefore, pop.getPopulationSize());
     }

     /* -------- normal iteration -------- */

     @Test
     public void testIteratorTraversalMatchesUnderlyingList() {
         SimpleListPopulation pop = new SimpleListPopulation(10);
         pop.addChromosome(new SimpleChromosome(1.0));
         pop.addChromosome(new SimpleChromosome(2.0));
         pop.addChromosome(new SimpleChromosome(3.0));

         List<Chromosome> iterated = new ArrayList<Chromosome>();
         Iterator<Chromosome> it = pop.iterator();
         while (it.hasNext()) {
             iterated.add(it.next());
         }
         assertEquals("iterator must return all chromosomes in order",
                      pop.getChromosomes(), iterated);
     }

     @Test
     public void testEmptyPopulationIteratorHasNext() {
         SimpleListPopulation pop = new SimpleListPopulation(5);
         assertFalse("hasNext() must be false for an empty population",
                     pop.iterator().hasNext());
     }

     @Test(expected = NoSuchElementException.class)
     public void testEmptyPopulationIteratorNextThrows() {
         SimpleListPopulation pop = new SimpleListPopulation(5);
         pop.iterator().next(); // must throw
     }

     /* -------- getChromosomes() returns unmodifiable list -------- */

     @Test(expected = UnsupportedOperationException.class)
     public void testGetChromosomesReturnsUnmodifiableList() {
         SimpleListPopulation pop = new SimpleListPopulation(10);
         pop.addChromosome(new SimpleChromosome(1.0));
         List<Chromosome> list = pop.getChromosomes();
         list.add(new SimpleChromosome(2.0));   // must throw
     }

     /* -------- constructor validation -------- */

     @Test(expected = NullArgumentException.class)
     public void testConstructorRejectsNullChromosomeList() {
         new SimpleListPopulation(null, 5);
     }

     @Test(expected = NotPositiveException.class)
     public void testConstructorRejectsZeroLimit() {
         new SimpleListPopulation(0);
     }

     @Test(expected = NotPositiveException.class)
     public void testConstructorRejectsNegativeLimit() {
         new SimpleListPopulation(-1);
     }

     @Test(expected = NumberIsTooLargeException.class)
     public void testConstructorRejectsChromosomesExceedingLimit() {
         List<Chromosome> chroms = new ArrayList<Chromosome>();
         for (int i = 0; i < 5; i++) {
             chroms.add(new SimpleChromosome(i));
         }
         new SimpleListPopulation(chroms, 3);   // size 5 > limit 3
     }
 }