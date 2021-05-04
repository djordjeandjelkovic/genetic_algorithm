package genetic.algorithm.models;

public class Chromosome {
	private int a;
	private int b;
	private int c;
	private int d;
	
	public Chromosome (Chromosome chromosome) {
		this.a = chromosome.getA();
		this.b = chromosome.getB();
		this.c = chromosome.getC();
		this.d = chromosome.getD();
	}
	
	public Chromosome (int a, int b, int c, int d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}

	public int getAbsolute() {
		return Math.abs((a + 2 * b + 3 * c + 4 * d) - 30);
	}
	
	@Override
	public String toString() {
		return "[" + a + "; " + b + "; " + c + "; " + d + "]";
	}
}
