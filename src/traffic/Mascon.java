/*
package traffic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

import cars.Car;

public class Mascon implements ActionListener {
	
	int accel = 0;
	double speed = 0;
	float totalDistance = 0, totalDistanceDisplay;
	
	TransformGroup carTransformGroup;
	Car car;
	
	public Mascon(Car car, int militime) {
		
		
		carTransformGroup = car.getCarObjectGroup();
		this.car = car;
		
		Timer timer = new Timer();
		timer.schedule(new TimerMoving(), 0, militime);
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
			car.setAcceralation(accel * 3);
			break;
		case "brake":
			accel--;
			if(accel < -4) {
				accel++;
			}
			car.setAcceralation(accel * 6);
			break;
		}
	}
	
	class TimerMoving extends TimerTask{

		float movedDistance = 0;
		Vector3f movedVector3f = new Vector3f(0.0f, 0.0f, 0.0f);
		Transform3D movedTransform3d = new Transform3D();
		Transform3D movedAngleTransform3d = new Transform3D();
		
		//field for curve
		double angle;
		double movedAngle;
		double declination = 0;
		
		float[] movedVector;
		
		@Override
		public void run() {
			/*move();
			movedDistance = car.getMovedDistance();
			totalDistance += movedDistance;
			carTransformGroup.setTransform(movedTransform3d);
			speed = car.getSpeed();
			speed = Math.ceil(speed * 10) / 10;
			totalDistanceDisplay = (float) (Math.ceil(totalDistance * 10) / 10);
			//System.out.println(movedVector[0] + ",  " +  movedVector[1] + ",  " + movedVector[2]);
		}
		
		private void move() {
			movedVector = car.move();
			movedAngleTransform3d = car.getNowNodeAngleTransform3d();
			movedVector3f.x = movedVector[0];
			movedVector3f.y = movedVector[1];
			movedVector3f.z = movedVector[2];
			movedTransform3d.setTranslation(movedVector3f);
			movedTransform3d.mul(movedAngleTransform3d);
			car.updateNode();
		}
		
	}

}
*/
