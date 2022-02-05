package traffic;

import javax.vecmath.Point3f;

public class CurveNode extends Node {

	float radius;
	float[] movedVector = new float[3];
	double angle;
	double movedAngle;
	double wholeAngle;
	double declination;
	double slope;

	Point3f centrePoint3f;

	public CurveNode(float radius, double declination, double angle, Point3f centre, float upHeight, float nowHeight) {
		this.radius = radius * 100;
		this.declination = declination;
		centrePoint3f = centre;
		wholeAngle = angle;
		movedVector[1] = nowHeight;
		float lengthTemp = (float) (radius * 2 * Math.PI * (wholeAngle / (Math.PI * 2)));
		slope = upHeight / lengthTemp;
		length = (float) Math.sqrt(upHeight * upHeight + lengthTemp * lengthTemp);
	}

	@Override
	public float[] move(float movedDistance) {
		float movedDistanceXZ = (float) (movedDistance / Math.sqrt(slope * slope / 10000 + 1));
		float movedDistanceY = (float) (movedDistanceXZ * slope / 100);

		movedAngle = movedDistance / radius;
		angle = declination + movedAngle;

		movedVector[0] = (float) (Math.cos(angle) * radius) / 100 + centrePoint3f.x;
		movedVector[1] += movedDistanceY;
		movedVector[2] = (float) (Math.sin(angle) * radius) / 100 + centrePoint3f.z;

		angleTransform3d.rotY(-movedAngle);

		this.declination = angle;

		return movedVector;
	}
}
