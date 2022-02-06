package cars;

import java.util.ArrayList;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;

import traffic.Node;
import traffic.StraightNode;

public class Car {
	
	int nowNodeIndex = 0;
	
	float movedDistance;
	float movedDistanceForCheckNode = 0;
	float movedDistanceRimainder = 0;
	float[] movedVector;
	double time;
	double speed = 60;
	double acceralation = 0;
	
	Point3f frontCoord;
	Point3f backCoord;
	public TransformGroup carObjectGroup;
	
	ArrayList<Node> nodeGroup = new ArrayList<>();
	Node nowNode;
	
	final Node STOPNODE = new StraightNode(0, 0, new Point3f(), 0);
	
	public Car(int loadingTime, ArrayList<Node> nodes, Point3f startPoint, double direction) {
		time = loadingTime / 1000.0; //ミリ秒 → 秒
		nodeGroup = nodes;
		nowNode = nodeGroup.get(nowNodeIndex);
	}
	
	public float[] move() {
		movedDistance = movedDistanceRimainder;
		movedDistanceRimainder = 0;
		
		movedDistance = (float)(speed * time + acceralation * time * time / 2); //x = V0t + 1/2at^2
		speed = speed + acceralation * time; //V = V0 + at
		
		if(speed < 0) {
			stop();
		}
		
		if (nowNode.equals(STOPNODE)) {
			stop();
			return movedVector;
		}
		
		movedVector = nowNode.move(movedDistance);
		
		movedDistanceForCheckNode += movedDistance;
		
		return movedVector;
	}
	
	public void changeLane() {
		
	}
	
	public void updateNode() {
		if(movedDistanceForCheckNode > nowNode.getLength() * 100) {
			nowNodeIndex++;
			if (nowNodeIndex >= nodeGroup.size()) {
				nowNode = STOPNODE;
				nowNodeIndex--;
				return;
			}
			movedDistanceRimainder = movedDistanceForCheckNode - nowNode.getLength() * 100;
			movedDistanceForCheckNode -= nowNode.getLength() * 100;
			nowNode = nodeGroup.get(nowNodeIndex);
		}
	}
	
	public void stop() {
		speed = 0;
		acceralation = 0;
	}
	
	public TransformGroup getCarObjectGroup() {
		return carObjectGroup;
	}
	
	public double getSpeed() {
		return speed;
	}
	
	public float getMovedDistance() {
		return movedDistance;
	}
	
	public Transform3D getNowNodeAngleTransform3d() {
		return nowNode.getAngleTransform3d();
	}
	
	public void setAcceralation(double accel) {
		acceralation = accel;
	}
}
