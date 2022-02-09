package tools.math;

public class Fraction {

	double numerator, denominator;

	public Fraction(double bunshi, double bunbo) {
		numerator = bunshi;
		denominator = bunbo;
	}

	public Fraction plus(Fraction plus2) {
		denominator *= plus2.denominator;
		numerator = numerator * plus2.denominator + plus2.numerator * denominator;

		return this;
	}

	public Fraction minus(Fraction minus2) {
		denominator *= minus2.denominator;
		numerator = numerator * minus2.denominator - minus2.numerator * denominator;

		return this;
	}

	public Fraction multiple(Fraction multi2) {
		denominator *= multi2.denominator;
		numerator *= multi2.numerator;

		return this;
	}

	public Fraction division(Fraction div2) {
		denominator *= div2.numerator;
		numerator *= div2.denominator;

		return this;
	}

	public double getDoubleValue() {
		return numerator / denominator;
	}

	public String getStringValue() {
		return numerator + "/" + denominator;
	}

	public Fraction abs() {
		if (getDoubleValue() < 0) {
			numerator *= -1;
		}
		return this;
	}
}
