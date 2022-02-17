package traffic;

import javax.vecmath.Point3f;

import tools.math.EquationStraight;

public class StraightNode extends Node {

	double declination;
	float[] movedVector = new float[3];
	double slope;

	public StraightNode(float lenXZ, double decli, Point3f startPoint, float upHeight) {
		declination = decli;
		
		movedVector[0] = startPoint.x;
		movedVector[1] = startPoint.y;
		movedVector[2] = startPoint.z;
		
		nowDirection += declination;
		
		slope = upHeight / lenXZ;
		length = (float) Math.sqrt(upHeight * upHeight + lenXZ * lenXZ);
		
		double[] point = {startPoint.x, startPoint.z};
		equationStraight = new EquationStraight(point, -decli - Math.PI / 2);
		//System.out.println(point[0] + ", " + point[1] + ", " + (Math.PI * 2 - decli) / Math.PI * 180);
		//System.out.println(equationStraight.getStringValue());
	}

	@Override
	public float[] move(float movedDistance) {
		float movedDistanceXZ = (float) (movedDistance / Math.sqrt(slope * slope + 1));
		float movedDistanceY = (float) (movedDistanceXZ * slope);
		
		movedVector[0] -= movedDistanceXZ * Math.sin(declination) / 100;
		movedVector[1] += movedDistanceY / 100;
		movedVector[2] += movedDistanceXZ * Math.cos(declination) / 100;
		
		return movedVector;
	}
}
