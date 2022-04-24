package tools.math;

/**
 * 1次方程式クラス<br>
 * EquationStraightクラスは、座標平面上の直線の方程式を表します。
 * <br>方程式ax+by=cにおいて、フィールドxValueはa、フィールドyValueはb、フィールドconstantValueはcを表します。
 *
 */
public class EquationStraight {

	double xValue, yValue, constantValue;

	public EquationStraight(double x, double y, double constant) {
		xValue = x;
		yValue = y;
		constantValue = constant;
	}

	public EquationStraight(double[] startPoint, double angle) {
		if(angle % Math.PI == Math.PI / 2) {
			yValue = 0;
			xValue = 1;
			constantValue = startPoint[0];
		}else if (angle % Math.PI == 0) {
			xValue = 0;
			yValue = 1;
			constantValue = startPoint[1];
		}else {
			double slope = Math.tan(angle);

			constantValue = startPoint[1] - slope * startPoint[0];
			xValue = -slope;
			yValue = 1;
		}
	}

	/**
	 * y=0のときの直線状の点を求めます。
	 * <br>(解説)
	 * <br>ax+by=c
	 * <br>ax=c (yに0を代入)
	 * <br>x=c/a
	 * @return 求めた点の座標
	 */
	public double[] getPoint() {
		double[] xReturn = new double[2];
		if (xValue != 0 ) {
			xReturn[0] = constantValue / xValue;
			xReturn[1] = 0;
		}else {
			xReturn[1] = constantValue / yValue;
			xReturn[0] = 0;
		}

		return xReturn;
	}

	/**
	 * 点とこの直線の距離を求めます。
	 * @return 点と直線の距離
	 * @param point 点の座標（Fraction型の長さ2の配列、要素の1つ目がx座標、2つ目がy座標）
	 */
	public double getDistancePoint(double[] point) {
		double distance = 0;

		if(xValue == 0) {
			distance = Math.abs(constantValue / yValue - point[1]);
		}else if(yValue == 0) {
			distance = Math.abs(constantValue / xValue - point[0]);
		}else {
			double tempNume = xValue * point[0] + yValue * point[1] + constantValue;
			tempNume = Math.abs(tempNume);

			double tempDeno = Math.sqrt(xValue * xValue + yValue * yValue);

			distance = tempNume / tempDeno;
		}

		return distance;
	}

	/**
	 * 2つの直線の距離を求めます。
	 * @param otherStraight もう1つの直線
	 * @return 2直線の距離
	 */
	public double getDistanceLine(EquationStraight otherStraight) {
		double distance = 0;

		double[] y0point = otherStraight.getPoint();
		distance = getDistancePoint(y0point);

		return distance;
	}

	@Override
	public String toString() {
		return xValue + "x+" + yValue + "y=" + constantValue;
	}
}
