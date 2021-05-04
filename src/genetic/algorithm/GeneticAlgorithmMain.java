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
		List<Chromosome> chromosomes = new ArrayList<>();
		chromosomes.add(new Chromosome(12, 5, 23, 8));
		chromosomes.add(new Chromosome(2, 21, 18, 3));
		chromosomes.add(new Chromosome(10, 4, 13, 14));
		chromosomes.add(new Chromosome(20, 1, 10, 6));
		chromosomes.add(new Chromosome(1, 4, 13, 19));
		chromosomes.add(new Chromosome(20, 5, 17, 1));
		
		int i = 0;
		while(i < 50) {
			chromosomes = getNextGeneration(chromosomes);
			
			System.out.println("NEW");
			for(Chromosome c: chromosomes) {
				System.out.println(c.toString());
			}
			i++;
		}
	}
	
	public static List<Chromosome> getNextGeneration(List<Chromosome> chromosomes) {
		List<Fitness> fitnesses = getFitnessListFromChromosomes(chromosomes);
		
		double totalFitnesses = 0;
		for(Fitness fitness: fitnesses) {
			totalFitnesses += fitness.getValue();
		}
		
		List<Probability> probabilities = getProbabilitiesFromFitnesses(fitnesses, totalFitnesses);		
		List<CumulativeProbability> cumulativeProbabilities = getCumulativeProbabilitiesFromProbabilities(probabilities);
		
		List<Double> randomList = generateStaticRandomList();
		List<Chromosome> newChromosomes = getNewChromosomeList(chromosomes, cumulativeProbabilities, randomList);
		
		List<Double> newRandomList = generateNewStaticRandomList();
		List<Integer> indexesUnderCrossOverRate = getChromosomeListUnderCrossOverRate(newChromosomes, newRandomList);
		
		List<Integer> cutRandomList = generateCutRandomList();
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
		chromosomes.forEach((chromosome) -> fitnesses.add(new Fitness(1 / (1.0 + chromosome.getAbsolute()))));
		
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
	
	public static List<Double> generateStaticRandomList() {
		List<Double> randomList = new ArrayList<>();
		randomList.add(0.201);
		randomList.add(0.284);
		randomList.add(0.099);
		randomList.add(0.822);
		randomList.add(0.398);
		randomList.add(0.501);
		
		return randomList;
	}
	
	public static List<Double> generateNewStaticRandomList() {
		List<Double> randomList = new ArrayList<>();
		randomList.add(0.191);
		randomList.add(0.259);
		randomList.add(0.760);
		randomList.add(0.006);
		randomList.add(0.159);
		randomList.add(0.340);
		
		return randomList;
	}
	
	public static List<Integer> generateCutRandomList() {
		List<Integer> randomList = new ArrayList<>();
		randomList.add(1);
		randomList.add(1);
		randomList.add(2);
		
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
		randomYieldsNumber.add(12);
		randomYieldsNumber.add(18);
		
		List<Integer> randomReplaceNumber = new ArrayList<Integer>();
		randomReplaceNumber.add(2);
		randomReplaceNumber.add(5);
		
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














