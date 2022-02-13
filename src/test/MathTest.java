package test;

import tools.math.EquationStraight;

public class MathTest {

	public static void main(String[] args) {
		double[] temp = {5, 2};
		
		EquationStraight equationStraight = new EquationStraight(temp, Math.PI / 4);
		
		System.out.println(equationStraight.getStringValue());
	}

}
