package traffic;

import tools.math.EquationStraight;

import java.util.Timer;
import java.util.TimerTask;


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

	/**
	 * 偏角(ラジアン)
	 */
	double declination;

	CarList nowOnCars;

	/**
	 * （直線Nodeで）このNodeを含む直線の方程式
	 */
	EquationStraight equationStraight;

	/**
	 * このNodeの種類
	 * デフォルト：MAIN
	 */
	NodeType type = NodeType.MAIN;

	//setterで変える
	boolean nextNodeCarListChange = false;

	NodeChangeType changeType = NodeChangeType.NO;

	float nextListMarge = 0;

	/**
	 * 移動時の座標計算をするメソッド
	 * @param movedDistance Carから渡された移動量
	 * @param movedVector Carから渡された現在地（座標）
	 * @return 移動した後の座標
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

	public void setType(NodeType type) {
		this.type = type;
	}

	public NodeType getType() {
		return type;
	}

	public double getDeclination() {
		return declination;
	}

	public boolean isNextNodeCarListChange() {
		return nextNodeCarListChange;
	}

	public void setNextNodeCarListChange(boolean nextNodeCarListChange) {
		this.nextNodeCarListChange = nextNodeCarListChange;
	}

	public float getNextListMarge() {
		return nextListMarge;
	}

	public void setNextListMarge(float nextListMarge) {
		this.nextListMarge = nextListMarge;
	}

	public NodeChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(NodeChangeType changeType) {
		this.changeType = changeType;
	}
}
