package traffic;

import java.util.ArrayList;

import cars.Car;
import tools.math.EquationStraight;


/**
 * Nodeクラスは、StraightNodeクラスおよびCurveNodeクラスの親クラスです。<br>
 * 2つのクラスに共通する変数の定義とその変数のgetterとsetterを提供します。<br><br>
 * Nodeは、各Carごとに生成される道路のことです。移動した座標の計算などを行います。<br><br>
 * 角度は、↓が0度、←が90度、↑が180度というふうになります。
 */
public abstract class Node {

	float length;
	double nowDirection;
	double limitSpeed;

	CarList nowOnCars;

	EquationStraight equationStraight;

	/**
	 * 移動時の座標計算をするメソッド
	 * @param movedDistance Carから渡された移動量
	 * @param movedVector Carから渡された現在地
	 * @return
	 */
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

	public void setNowOnCars(CarList nowOnCars) {
		this.nowOnCars = nowOnCars;
	}

	public CarList getNowOnCars() {
		return nowOnCars;
	}

	public double getLimitSpeed() {
		return limitSpeed;
	}

	public void setLimitSpeed(double limitSpeed) {
		this.limitSpeed = limitSpeed;
	}
}
