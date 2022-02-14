package traffic;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;

public class CurveNode extends Node {

	float radius;
	float[] movedVector = new float[3];
	double wholeAngle;
	double declination;
	double slope;

	Point3f centrePoint3f;
	Transform3D angleTempTransform3d = new Transform3D();

	public CurveNode(float radius, double declination, double angle, Point3f centre, float upHeight, float nowHeight) {
		this.radius = radius * 100;
		this.declination = declination;
		
		centrePoint3f = centre;
		wholeAngle = angle;
		movedVector[1] = nowHeight;
		
		nowDirection -= declination;
		
		float lengthTemp = (float) (radius * 2 * Math.PI * (wholeAngle / (Math.PI * 2)));
		slope = upHeight / lengthTemp;
		length = (float) Math.sqrt(upHeight * upHeight + lengthTemp * lengthTemp);
	}

	@Override
	public float[] move(float movedDistance) {
		double movedAngle;
		double angle;
		
		float movedDistanceXZ = (float) (movedDistance / Math.sqrt(slope * slope / 10000 + 1));
		float movedDistanceY = (float) (movedDistanceXZ * slope / 100);

		movedAngle = movedDistance / radius;
		angle = declination + movedAngle;

		movedVector[0] = (float) (Math.cos(angle) * radius) / 100 + centrePoint3f.x;
		movedVector[1] += movedDistanceY;
		movedVector[2] = (float) (Math.sin(angle) * radius) / 100 + centrePoint3f.z;

		nowDirection -= movedAngle;

		this.declination = angle;

		return movedVector;
	}
}
