package cars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import traffic.Node;
import traffic.StraightNode;

public class Car implements ActionListener {
	
	int nowNodeIndex = 0;
	int accel = 0;
	
	float movedDistance;
	float movedDistanceForCheckNode = 0;
	float movedDistanceRimainder = 0;
	float totalDistance, totalDistanceDisplay = 0;
	float[] movedVector;
	double time;
	double speed = 0;
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
		
		Timer timer = new Timer();
		timer.schedule(new TimerMove(), 1000, loadingTime);
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
	
	public void changeLane(Node changedNode, ArrayList<Node> changedNodeGroup) {
		
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
	
	class TimerMove extends TimerTask{
		Vector3f movedVector3f = new Vector3f(0.0f, 0.0f, 0.0f);
		Transform3D movedTransform3d = new Transform3D();
		
		//field for curve
		double angle;
		double movedAngle;
		double declination = 0;
		
		double[] printed;
		
		float[] movedVector;
		
		@Override
		public void run() {
			moveCulculation();
			totalDistance += movedDistance;
			carObjectGroup.setTransform(movedTransform3d);
			speed = Math.ceil(speed * 10) / 10;
			totalDistanceDisplay = (float) (Math.ceil(totalDistance * 10) / 10);
			//System.out.println(movedVector[0] + ",  " +  movedVector[1] + ",  " + movedVector[2]);
		}
		
		private void moveCulculation() {
			movedVector = move();
			movedVector3f.x = movedVector[0];
			movedVector3f.y = movedVector[1];
			movedVector3f.z = movedVector[2];
			movedTransform3d.setIdentity();
			movedTransform3d.setTranslation(movedVector3f);
			//printtransform();
			movedTransform3d.mul(nowNode.getAngleTransform3d());
			updateNode();
		}

		@SuppressWarnings("unused")
		private void printtransform() {
			System.out.println(movedTransform3d.toString());
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		switch(command) {
		case "accel":
			accel++;
			if(accel > 4) {
				accel--;
			}
			setAcceralation(accel * 3);
			break;
		case "brake":
			accel--;
			if(accel < -4) {
				accel++;
			}
			setAcceralation(accel * 6);
			break;
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
	
	public int getAccel() {
		return accel;
	}
	
	public float getTotalDistance() {
		return totalDistance;
	}
	
	public float getTotalDistanceDisplay() {
		return totalDistanceDisplay;
	}
	
	public float getMovedDistance() {
		return movedDistance;
	}
	
	public void setAcceralation(double accel) {
		acceralation = accel;
	}
}
