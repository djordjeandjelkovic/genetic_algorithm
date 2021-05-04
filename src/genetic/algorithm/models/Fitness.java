package genetic.algorithm.models;

public class Fitness {
	private double value;
	
	public Fitness(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
