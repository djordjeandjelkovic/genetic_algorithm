package genetic.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import genetic.algorithm.models.Chromosome;
import genetic.algorithm.models.CumulativeProbability;
import genetic.algorithm.models.Fitness;
import genetic.algorithm.models.Probability;

public class GeneticAlgorithmMain {
	
	public static final int NUMBER_OF_POPULATION = 6;
	public static final int TOTAL_GEN_IN_CHROMOSOME = 4; // a, b, c, d
	public static final int TOTAL_GEN = TOTAL_GEN_IN_CHROMOSOME * NUMBER_OF_POPULATION;
	
	public static final int MAX_NUMBER_OF_CHROMOSOME = 30;
	public static final double CROSS_OVER_RATE = 0.25;
	public static final double MUTATION_RATE = 0.1;
	public static final long NUMBER_OF_MUTATIONS = Math.round(MUTATION_RATE * TOTAL_GEN);

	public static void main(String[] args) {
		
		// KORAK 1. INICIJALIZACIJA
		List<Chromosome> chromosomes = createInitialChromosomes();
		
		System.out.println("Inicijalni hromozomi");
		for(Chromosome c: chromosomes) {
			System.out.println(c.toString());
		}
		
		int i = 1;
		
		// Idemo do 1000, dokle bi trebalo da nadjemo optimalni hromozom
		while(i <= 1000) {
			chromosomes = getNextGeneration(chromosomes);
			
			boolean theBestWasFound = false;
			for(Chromosome c: chromosomes) {
				
				// Ako je zadovoljen uslov funkcije, onda smo nasli optimalan hromozom i mozemo prekinuti sa radom program
				if((c.getA() + 2 * c.getB() + 3 * c.getC() + 4 * c.getD()) == 30) {
					theBestWasFound = true;
					break;
				}
			}
			
			if(theBestWasFound) {
				System.out.println("The best chromosome was found after " + i + " iterations.");
				
				System.out.println("Chromosomes:");
				for(Chromosome c: chromosomes) {
					if((c.getA() + 2 * c.getB() + 3 * c.getC() + 4 * c.getD()) == 30) {
						System.out.println("The best --> " + c.toString());
					} else {
						System.out.println(c.toString());
					}					
				}
				
				break;
			}
			
			i++;
		}
	}
	
	public static List<Chromosome> getNextGeneration(List<Chromosome> chromosomes) {
		// KORAK 3. SELEKCIJA - FUNKCIJA PRILAGODJENOSTI
		List<Fitness> fitnesses = getFitnessListFromChromosomes(chromosomes);
		
		double totalFitnesses = 0;
		for(Fitness fitness: fitnesses) {
			totalFitnesses += fitness.getValue();
		}
				
		// KORAK 3. SELEKCIJA - VEROVATNOCA SELEKCIJE HROMOZOMA
		List<Probability> probabilities = getProbabilitiesFromFitnesses(fitnesses, totalFitnesses);
		
		// KORAK 3. SELEKCIJA - KUMULATIVNE VEROVATNOCE
		List<CumulativeProbability> cumulativeProbabilities = getCumulativeProbabilitiesFromProbabilities(probabilities);
		
		// KORAK 3. SELEKCIJA - RULET SELEKCIJA
		List<Double> randomList = generateRandomList();
		List<Chromosome> newChromosomes = getNewChromosomeList(chromosomes, cumulativeProbabilities, randomList);
		
		// KORAK 4. UKRSTANJE
		List<Double> newRandomList = generateRandomList();
		List<Integer> indexesUnderCrossOverRate = getChromosomeListUnderCrossOverRate(newChromosomes, newRandomList);
		
		// KORAK 5. MUTIRANJE
		List<Integer> cutRandomList = generateCutRandomList(indexesUnderCrossOverRate.size());
		updateChromosomeListWithCutGen(newChromosomes, indexesUnderCrossOverRate, cutRandomList);
		mutateChromosomes(newChromosomes);
		
		return newChromosomes;
	}
	
	public static List<Chromosome> createInitialChromosomes() {
		List<Chromosome> chromosomes = new ArrayList<>();
		Random r = new Random();
		for(int i = 0; i < NUMBER_OF_POPULATION; i++) {			
			int a = r.nextInt(MAX_NUMBER_OF_CHROMOSOME + 1);
			int b = r.nextInt(MAX_NUMBER_OF_CHROMOSOME + 1);
			int c = r.nextInt(MAX_NUMBER_OF_CHROMOSOME + 1);
			int d = r.nextInt(MAX_NUMBER_OF_CHROMOSOME + 1);
			
			chromosomes.add(new Chromosome(a, b, c, d));
		}
		
		return chromosomes;
	}

	public static List<Fitness> getFitnessListFromChromosomes(List<Chromosome> chromosomes) {
		List<Fitness> fitnesses = new ArrayList<>();	
		chromosomes.forEach((chromosome) -> fitnesses.add(new Fitness(1 / (1.0 + chromosome.getAbsolute())))); // KORAK 2. getAbsolute IZRACUNAVANJE CILJNE FUNKCIJE
		
		return fitnesses;
	}
	
	public static List<Probability> getProbabilitiesFromFitnesses(List<Fitness> fitnesses, double total) {
		List<Probability> probabilities = new ArrayList<>();
		fitnesses.forEach((fitness) -> probabilities.add(new Probability(fitness.getValue() / total)));
		
		return probabilities;
	}
	
	public static List<CumulativeProbability> getCumulativeProbabilitiesFromProbabilities(List<Probability> probabilities) {
		List<CumulativeProbability> cumulativeProbabilities = new ArrayList<>();
		
		double subtotal = 0.0;
		for(Probability p: probabilities) {
			subtotal += p.getValue();
			cumulativeProbabilities.add(new CumulativeProbability(subtotal));
		}
		
		return cumulativeProbabilities;
	}
	
	public static List<Double> generateRandomList() {
		Random r = new Random();
		
		List<Double> randomList = new ArrayList<>();
		for(int i = 0; i < NUMBER_OF_POPULATION; i++) {
			randomList.add(r.nextDouble());
		}
		
		return randomList;
	}
	
	public static List<Integer> generateCutRandomList(int selectedChromosomes) {
		Random r = new Random();
		List<Integer> randomList = new ArrayList<>();
		for(int i = 0; i < selectedChromosomes; i++) {
			randomList.add(r.nextInt(TOTAL_GEN_IN_CHROMOSOME - 1) + 1);
		}
		
		return randomList;
	}
	
	public static List<Chromosome> getNewChromosomeList(List<Chromosome> chromosomes, List<CumulativeProbability> cumulativeProbabilities, List<Double> randomList) {
		List<Chromosome> newChromosomes = new ArrayList<>();
		
		for(Double randomNumber: randomList) {
			for(int j = 0; j < cumulativeProbabilities.size() - 1; j++) {
				if(j == 0 && randomNumber < cumulativeProbabilities.get(j).getValue()) {
					newChromosomes.add(new Chromosome(chromosomes.get(j)));
					continue;
				}
				
				if(randomNumber > cumulativeProbabilities.get(j).getValue() && randomNumber < cumulativeProbabilities.get(j + 1).getValue()) {
					newChromosomes.add(new Chromosome(chromosomes.get(j + 1)));
				}
			}
		}
		
		return newChromosomes;
	}
	
	public static List<Integer> getChromosomeListUnderCrossOverRate(List<Chromosome> chromosomes, List<Double> randomList) {
		List<Integer> indexesList = new ArrayList<>();
		
		for(int i = 0; i < randomList.size(); i++) {			
			if(randomList.get(i) < CROSS_OVER_RATE) {
				indexesList.add(i);
			}
		}
		
		return indexesList;
	}
	
	public static void updateChromosomeListWithCutGen(List<Chromosome> chromosomes, List<Integer> indexesUnderCrossOverRate, List<Integer> randomList) {
		if(indexesUnderCrossOverRate.size() == 0) {
			//System.out.println("No chromosomes under cross over rate.");
			return;
		}
		
		Chromosome firstChromosomeCopy = new Chromosome(chromosomes.get(indexesUnderCrossOverRate.get(0)));
		for(int i = 0; i < indexesUnderCrossOverRate.size(); i++) {			
			if(i == indexesUnderCrossOverRate.size() - 1) {
				replaceChromosomeAtPosition(chromosomes.get(indexesUnderCrossOverRate.get(i)), firstChromosomeCopy, randomList.get(i));
				continue;
			}
			
			replaceChromosomeAtPosition(chromosomes.get(indexesUnderCrossOverRate.get(i)), chromosomes.get(indexesUnderCrossOverRate.get(i + 1)), randomList.get(i));
		}
	}
	
	public static void replaceChromosomeAtPosition(Chromosome chromosome1, Chromosome chromosome2, int position) {
		switch(position - 1) {
			case 0:
				chromosome1.setB(chromosome2.getB());
				chromosome1.setC(chromosome2.getC());
				chromosome1.setD(chromosome2.getD());
				break;
			case 1:
				chromosome1.setC(chromosome2.getC());
				chromosome1.setD(chromosome2.getD());
				break;
			case 2:
				chromosome1.setD(chromosome2.getD());
				break;
		}
	}
	
	public static void mutateChromosomes(List<Chromosome> chromosomes) {
		List<Integer> randomYieldsNumber = new ArrayList<Integer>();
		Random r = new Random();
		
		int numberOfMutations = (int) (MUTATION_RATE * TOTAL_GEN);
		for(int i = 0; i < numberOfMutations; i++) {
			randomYieldsNumber.add(r.nextInt(TOTAL_GEN - 1) + 1);
		}

		List<Integer> randomReplaceNumber = new ArrayList<Integer>();
		for(int i = 0; i < numberOfMutations; i++) {
			randomReplaceNumber.add(r.nextInt(MAX_NUMBER_OF_CHROMOSOME - 1) + 1);
		}
		
		for(int i = 0; i < randomYieldsNumber.size(); i++) {
			int chromosomeIndex = randomYieldsNumber.get(i) / TOTAL_GEN_IN_CHROMOSOME;
			int orderInChromosome = randomYieldsNumber.get(i) % TOTAL_GEN_IN_CHROMOSOME;
			
			if(orderInChromosome > 0) {
				chromosomeIndex++;
			}
			
			switch(orderInChromosome) {
				case 0:
					chromosomes.get(chromosomeIndex - 1).setD(randomReplaceNumber.get(i));
					break;
				case 1:
					chromosomes.get(chromosomeIndex - 1).setA(randomReplaceNumber.get(i));
					break;
				case 2:
					chromosomes.get(chromosomeIndex - 1).setB(randomReplaceNumber.get(i));
					break;
				case 3:
					chromosomes.get(chromosomeIndex - 1).setC(randomReplaceNumber.get(i));
					break;
			}
		}
	}
}














