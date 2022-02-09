package tools.math;

/**
 * 1次方程式クラス<br>
 * EquationStraightクラスは、座標平面上の直線の方程式を表します。
 * <br>方程式ax+by=cにおいて、フィールドxValueはa、フィールドyValueはb、フィールドconstantValueはcを表します。
 *
 */
public class EquationStraight {

	Fraction xValue, yValue, constantValue;

	/**
	 * y=0のときの直線状の点を求めます。
	 * <br>(解説)
	 * <br>ax+by=c
	 * <br>ax=c (yに0を代入)
	 * <br>x=c/a
	 * @return 求めた点のx座標
	 */
	public Fraction getPoint() {
		Fraction xReturnFraction = constantValue.division(xValue);

		return xReturnFraction;
	}

	/**
	 * 点と子の直線の距離を求めます。
	 * @return 点と直線の距離
	 * @param 点（Fraction型の長さ2の配列、要素の1つ目がx座標、2つ目がy座標）
	 */
	public double getDistancePoint(Fraction[] point) {
		double distance = 0;

		Fraction tempNume = xValue.multiple(point[0]).plus(yValue.multiple(point[1]).plus(constantValue));
		tempNume = tempNume.abs();

		Fraction tempDeno =
		return distance;
	}

	/**
	 * 2つの直線の距離を求めます。
	 * @param もう1つの直線
	 * @return 2直線の距離
	 */
	public double getDistanceLine(EquationStraight otherStraight) {
		double distance = 0;

		return distance;
	}
}
