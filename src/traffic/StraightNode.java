package traffic;

import javax.vecmath.Point3f;

import tools.math.EquationStraight;

public class StraightNode extends Node {

	/**
	 * 偏角(ラジアン)
	 */
	double declination;

	/**
	 * 勾配(tanθ)
	 */
	double slope;

	/**
	 * @param lenXZ 高さや勾配を無視したときの長さ（高さを考えたNodeの長さをLとすると lenXZ = Lcosθ）
	 * @param decli 偏角
	 * @param startPoint 開始地点
	 * @param upHeight このNodeにいる間に上昇する高さ
	 */
	public StraightNode(float lenXZ, double decli, Point3f startPoint, float upHeight) {
		declination = decli;

		nowDirection += declination;

		slope = upHeight / lenXZ;
		length = (float) Math.sqrt(upHeight * upHeight + lenXZ * lenXZ);

		double[] point = {startPoint.x, startPoint.z};
		equationStraight = new EquationStraight(point, -decli - Math.PI / 2);
		//System.out.println(point[0] + ", " + point[1] + ", " + (Math.PI * 2 - decli) / Math.PI * 180);
		//System.out.println(equationStraight.getStringValue());

		limitSpeed = 27.77778;
	}

	@Override
	public float[] move(float movedDistance, float[] movedVector) {
		float movedDistanceXZ = (float) (movedDistance / Math.sqrt(slope * slope + 1));
		float movedDistanceY = (float) (movedDistanceXZ * slope);

		movedVector[0] -= movedDistanceXZ * Math.sin(declination) / 100;
		movedVector[1] += movedDistanceY / 100;
		movedVector[2] += movedDistanceXZ * Math.cos(declination) / 100;

		return movedVector;
	}
}
