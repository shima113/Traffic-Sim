package test;

import tools.math.EquationStraight;

public class MathTest {

	public static void main(String[] args) {
		double[] temp = {5, 2};
		double angle = Math.PI / 6;
		
		EquationStraight equationStraight = new EquationStraight(temp, Math.PI);
		
		System.out.println(equationStraight.toString());
		System.out.println(angle / Math.PI * 180);
	}

}
