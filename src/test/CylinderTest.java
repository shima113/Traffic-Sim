package test;

import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

import javax.media.j3d.Transform3D;
import javax.swing.JFrame;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class CylinderTest extends JFrame {
	
	double x, y = 0;

	public static void main(String args[]) {

		new CylinderTest();
		
		
		
	}
	
	public CylinderTest() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(200, 200);
		setVisible(true);
		
		Timer timer = new Timer();
		timer.schedule(new TimerMoving(), 1000, 500);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		rerepaint();
	}
	
	private void rerepaint() {
		Graphics graphics = getContentPane().getGraphics();
		graphics.fillRect((int)x, (int)y, 3, 3);
	}

	class TimerMoving extends TimerTask{

		float movedDistance = 0;
		Vector3f movedVector3f = new Vector3f(100.0f, 0.0f, 100.0f);
		Transform3D movedTransform3d = new Transform3D();
		Transform3D movedAngleTransform3d = new Transform3D();
		
		//field for curve
		float radius;
		float string;
		double angleMoved;
		double angleMovedHalf;
		double angleVertical;
		double angleLastCalculation;
		Vector3f radiusVector = new Vector3f(0.0f, 0.0f, 0.0f);
		
		@Override
		public void run() {
			movedDistance = 10;
			curve(movedVector3f, new Point3f(110.0f, 0.0f, 100.0f));
			movedTransform3d.setTranslation(movedVector3f);
			//carTransformGroup.setTransform(movedTransform3d);
			//speed = car.getSpeed();
			//speedDisplay.setText("速度：" + speed + "km/h");
		}
		
		@SuppressWarnings("unused")
		private void straight() {
			movedVector3f.z += movedDistance / 10;
		}
		
		private void curve(Vector3f center, Point3f nowPoint) {
			radiusVector.x = center.x - nowPoint.x;
			radiusVector.z = center.z - nowPoint.z;
			
			//System.out.println(radiusVector.x);
			//System.out.println(radiusVector.z);
			
			radius = radiusVector.length();
			angleVertical = Math.atan(radiusVector.z / radiusVector.x);
			
			angleMoved = movedDistance / radius;
			angleMovedHalf = angleMoved / 2;
			
			string = (float) (radius * Math.sin(angleMoved) / Math.cos(angleMovedHalf));
			
			angleLastCalculation = Math.PI / 2 - angleMovedHalf - angleVertical;
			
			
			//System.out.println(angleVertical);
			//System.out.println(angleLastCalculation);
			//System.out.println(angleMoved);
			
			movedVector3f.x -= -(float) (Math.cos(angleLastCalculation) * string);
			movedVector3f.z -= -(float) (Math.sin(angleLastCalculation) * string);
			
			x = movedVector3f.x;
			y = movedVector3f.z;
			System.out.println(x);
			System.out.println(y);
			paint(getGraphics());
		}
		
	}
}
