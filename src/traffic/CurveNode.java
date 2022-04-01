package traffic;

import javax.vecmath.Point3f;

/**
 *ここでの曲線はすべて円弧です。
 */
public class CurveNode extends Node {

	/**
	 * 曲線半径
	 */
	float radius;
	/**
	 * 曲線の角度
	 */
	double wholeAngle;
	/**
	 * 偏角
	 */
	double declination;
	/**
	 * 勾配
	 */
	double slope;

	/**
	 * 曲線の中心
	 */
	Point3f centrePoint3f;

	/**
	 * @param radius 曲線半径
	 * @param declination 偏角
	 * @param angle 曲線の円弧の角度
	 * @param centre 曲線の中心
	 * @param upHeight このNodeで上昇する高さ
	 * @param nowHeight 現在(Nodeの一番最初)での高さ
	 */
	public CurveNode(float radius, double declination, double angle, Point3f centre, float upHeight, float nowHeight) {
		this.radius = radius * 100;
		this.declination = declination;

		centrePoint3f = centre;
		wholeAngle = angle;

		nowDirection += declination;

		float lengthTemp = (float) (radius * 2 * Math.PI * (wholeAngle / (Math.PI * 2)));
		slope = upHeight / lengthTemp;
		length = (float) Math.sqrt(upHeight * upHeight + lengthTemp * lengthTemp);

		if (this.radius < 75) {
			limitSpeed = 11.11111;
		}else if (this.radius < 200) {
			limitSpeed = 13.88889;
		}else if (this.radius < 300) {
			limitSpeed = 16.66667;
		}else if (this.radius < 400) {
			limitSpeed = 19.44444;
		}else if (this.radius < 600) {
			limitSpeed = 22.22222;
		}else {
			limitSpeed = 27.77778;
		}
	}

	@Override
	public float[] move(float movedDistance, float[] movedVector) {
		double movedAngle;
		double angle;

		float movedDistanceXZ = (float) (movedDistance / Math.sqrt(slope * slope / 10000 + 1));
		float movedDistanceY = (float) (movedDistanceXZ * slope / 100);

		movedAngle = movedDistance / radius;
		angle = declination + movedAngle;

		movedVector[0] = (float) (Math.cos(angle) * radius) / 100 + centrePoint3f.x;
		movedVector[1] += movedDistanceY;
		movedVector[2] = (float) (Math.sin(angle) * radius) / 100 + centrePoint3f.z;

		nowDirection += movedAngle;

		this.declination = angle;

		return movedVector;
	}
}
