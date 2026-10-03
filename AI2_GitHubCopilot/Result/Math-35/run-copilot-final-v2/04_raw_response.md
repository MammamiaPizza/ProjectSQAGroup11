@Test
 public void testNextGenerationPreservesElitismRate() {
     // Use a non-empty population so nextGeneration() has chromosomes to process
     ElitisticListPopulation pop = new ElitisticListPopulation(10, 0.3);
     pop.addChromosome(org.apache.commons.math3.genetics.Chromosome.newStub(0.9));
     pop.addChromosome(org.apache.commons.math3.genetics.Chromosome.newStub(0.5));
     pop.addChromosome(org.apache.commons.math3.genetics.Chromosome.newStub(0.1));
     Population next = pop.nextGeneration();
     org.junit.Assert.assertEquals(0.3, ((ElitisticListPopulation) next).getElitismRate(), 0.0);
 }