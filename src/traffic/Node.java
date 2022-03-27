package traffic;

import java.util.ArrayList;

import cars.Car;
import tools.math.EquationStraight;

public abstract class Node {

	float length;
	double nowDirection;
	double limitSpeed;

	ArrayList<Car> nowOnCars;

	EquationStraight equationStraight;

	abstract public float[] move(float movedDistance, float[] movedVector);

	public float getLength() {
		return length;
	}

	public EquationStraight getEquationStraight() {
		return equationStraight;
	}

	public double getNowDirection() {
		return nowDirection;
	}

	public void setNowOnCars(ArrayList<Car> nowOnCars) {
		this.nowOnCars = nowOnCars;
	}

	public ArrayList<Car> getNowOnCars() {
		return nowOnCars;
	}

	public double getLimitSpeed() {
		return limitSpeed;
	}
}
