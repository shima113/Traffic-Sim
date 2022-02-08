package traffic;

import javax.media.j3d.Transform3D;

public abstract class Node {

	float length;
	Transform3D angleTransform3d = new Transform3D();
	
	abstract public float[] move(float movedDistance);
	
	public Transform3D getAngleTransform3d() {
		return angleTransform3d;
	}
	
	public float getLength() {
		return length;
	}
	
}
