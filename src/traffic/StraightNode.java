package traffic;

import javax.vecmath.Point3f;

public class StraightNode extends Node {

	double declination;
	float[] movedVector = new float[3];
	Point3f start;
	double slope;

	public StraightNode(float lenXZ, double decli, Point3f startPoint, float upHeight) {
		declination = decli;
		start = startPoint;
		movedVector[0] = start.x;
		movedVector[1] = start.y;
		movedVector[2] = start.z;
		angleTransform3d.rotY(-declination);
		slope = upHeight / lenXZ;
		length = (float) Math.sqrt(upHeight * upHeight + lenXZ * lenXZ);
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
