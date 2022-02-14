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

import traffic.CurveNode;
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
	double nowDirection = 0;
	
	Point3f frontCoord;
	Point3f backCoord;
	public TransformGroup carObjectGroup;
	Transform3D angleTransform3d = new Transform3D();
	
	ArrayList<Node> nodeGroup = new ArrayList<>();
	Node nowNode;
	
	
	Node atoNode;
	ArrayList<Node> atnodegroupArrayList;
	
	final Node STOPNODE = new StraightNode(0, 0, new Point3f(), 0);
	
	public Car(int loadingTime, ArrayList<Node> nodes, Point3f startPoint, double direction) {
		time = loadingTime / 1000.0; //ミリ秒 → 秒
		nodeGroup = nodes;
		nowNode = nodeGroup.get(nowNodeIndex);
		
		Timer timer = new Timer();
		timer.schedule(new TimerMove(), 1000, loadingTime);
	}
	
	public void move() {
		movedDistance = movedDistanceRimainder;
		movedDistanceRimainder = 0;
		
		movedDistance = (float)(speed * time + acceralation * time * time / 2); //x = V0t + 1/2at^2
		speed = speed + acceralation * time; //V = V0 + at
		
		if(speed < 0) {
			stop();
		}
		
		if (nowNode.equals(STOPNODE)) {
			stop();
			return;
		}
		
		movedVector = nowNode.move(movedDistance);
		
		movedDistanceForCheckNode += movedDistance;
		
		return;
	}
	
	public void changeLane(Node targetNode, ArrayList<Node> targetNodeGroup) {
		float distance = (float) nowNode.getEquationStraight().getDistanceLine(targetNode.getEquationStraight());
		distance /= 2;
		
		final float CHANGELANE_INTERVAL = 0.10f;
		
		float radius = (distance * distance + CHANGELANE_INTERVAL * CHANGELANE_INTERVAL) / (2 * distance);
		double declination = nowNode.getNowDirection();
		double angle = Math.acos((radius - distance) / radius);
		Point3f centrePoint3f1 =
				new Point3f((float)(-Math.cos(declination) * radius) + movedVector[0], movedVector[1], (float) Math.sin(declination) * radius + movedVector[2]);
		Point3f centrePoint3f2 = 
				new Point3f((float)(-distance * Math.cos(declination) - CHANGELANE_INTERVAL * 2 * Math.cos(Math.PI / 2 - declination)) + movedVector[0], movedVector[1], 
						(float) (distance * Math.sin(declination) + CHANGELANE_INTERVAL * 2 * Math.sin(Math.PI / 2 - declination) + movedVector[2]));
		
		CurveNode changeLaneNode1 = new CurveNode(radius, declination, angle, centrePoint3f1, 0, movedVector[1]);
		CurveNode changeLaneNode2 = new CurveNode(radius, declination + angle / 2, angle, centrePoint3f2, 0, movedVector[1]);
		
		nodeGroup.add(changeLaneNode1);
		nodeGroup.add(changeLaneNode2);
	}
	
	public void setAtoNode(Node atoNode) {
		this.atoNode = atoNode;
	}

	public void setAtnodegroupArrayList(ArrayList<Node> atnodegroupArrayList) {
		this.atnodegroupArrayList = atnodegroupArrayList;
	}

	public void updateNode() {
		if(movedDistanceForCheckNode > nowNode.getLength() * 100) {
			nowNodeIndex++;
			if (nowNodeIndex >= nodeGroup.size()) {
				//nowNode = STOPNODE;
				nowNodeIndex--;
				changeLane(atoNode, atnodegroupArrayList);
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
			move();
			movedVector3f.x = movedVector[0];
			movedVector3f.y = movedVector[1];
			movedVector3f.z = movedVector[2];
			
			movedTransform3d.setIdentity();
			movedTransform3d.setTranslation(movedVector3f);
			//printtransform();
			angleTransform3d.rotY(nowNode.getNowDirection());
			movedTransform3d.mul(angleTransform3d);
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
