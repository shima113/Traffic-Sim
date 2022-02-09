package test;

import tools.math.Fraction;

public class MathTest {

	public static void main(String[] args) {
		Fraction f1 = new Fraction(2, 3);
		f1.division(new Fraction(2, 3));
		System.out.println(f1.getStringValue());
	}

}
