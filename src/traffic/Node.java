package traffic;

import tools.math.EquationStraight;

public abstract class Node {

	float length;
	double nowDirection;
	
	EquationStraight equationStraight;
	
	abstract public float[] move(float movedDistance);
	
	public float getLength() {
		return length;
	}
	
	public EquationStraight getEquationStraight() {
		return equationStraight;
	}

	public double getNowDirection() {
		return nowDirection;
	}
}
