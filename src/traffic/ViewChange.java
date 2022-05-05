package traffic;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class ViewChange {

	int preX, preY;
	int nowX, nowY;

	float cameraDistance;
	float sense;
	float angleY = (float)Math.PI / 2;
	float angleXZ = 0;

	float cameraX, cameraY, cameraZ = 0;
	float movedCameraX, movedCameraY, movedCameraZ = 0;

	public final float DEFAULT_SENSE = 0.001f;

	Vector3f cameraPositionVector;

	Transform3D cameraPositionTransform;
	Transform3D cameraAngleYTransform;
	Transform3D cameraAngleXZTransform;

	TransformGroup cameraTransformGroup;

	public ViewChange(Canvas3D canvas, SimpleUniverse universe, float distance) {
		cameraDistance = distance;

		ViewingPlatform viewingPlatform = universe.getViewingPlatform();
		cameraTransformGroup = viewingPlatform.getViewPlatformTransform();

		cameraX = (float) (cameraDistance * Math.cos(angleY) * Math.sin(angleXZ));
		cameraY = (float) (cameraDistance * Math.sin(angleY));
		cameraZ = (float) (cameraDistance * Math.cos(angleY) * Math.cos(angleXZ));

		cameraPositionVector = new Vector3f(cameraX, cameraY, cameraZ);

		cameraPositionTransform = new Transform3D();
		cameraPositionTransform.setTranslation(cameraPositionVector);

		cameraAngleYTransform = new Transform3D();
		cameraAngleXZTransform = new Transform3D();

		cameraAngleYTransform.rotX(-angleY);
		cameraAngleXZTransform.rotY(angleXZ);

		cameraAngleXZTransform.mul(cameraAngleYTransform);
		cameraPositionTransform.mul(cameraAngleXZTransform);

		cameraTransformGroup.setTransform(cameraPositionTransform);

		MouseViewChange mouseViewChange = new MouseViewChange();
		canvas.addMouseMotionListener(mouseViewChange);
		canvas.addMouseWheelListener(mouseViewChange);
		canvas.addKeyListener(mouseViewChange);

		sense = DEFAULT_SENSE;
	}

	public void setSensitivity(float sensit) {
		sense = sensit;
	}

	class MouseViewChange implements MouseMotionListener, MouseWheelListener, KeyListener {

		int keycode;
		
		@Override
		public void mouseDragged(MouseEvent e) {
			nowX = e.getX();
			nowY = e.getY();

			angleXZ -= sense * (nowX - preX);
			angleY += sense * (nowY - preY);

			setValue();

			preX = e.getX();
			preY = e.getY();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			preX = e.getX();
			preY = e.getY();
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			cameraDistance += e.getPreciseWheelRotation() / 2;
			setValue();
		}
		
		private void setValue() {
			cameraX = (float) (cameraDistance * Math.cos(angleY) * Math.sin(angleXZ));
			cameraY = (float) (cameraDistance * Math.sin(angleY));
			cameraZ = (float) (cameraDistance * Math.cos(angleY) * Math.cos(angleXZ));

			cameraPositionVector.x = cameraX + movedCameraX;
			cameraPositionVector.y = cameraY + movedCameraY;
			cameraPositionVector.z = cameraZ + movedCameraZ;

			cameraPositionTransform.setIdentity();
			cameraPositionTransform.setTranslation(cameraPositionVector);

			cameraAngleYTransform.rotX(-angleY);
			cameraAngleXZTransform.rotY(angleXZ);

			cameraAngleXZTransform.mul(cameraAngleYTransform);
			cameraPositionTransform.mul(cameraAngleXZTransform);

			cameraTransformGroup.setTransform(cameraPositionTransform);
		}

		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			keycode = e.getKeyCode();
			
			switch(keycode) {
			case KeyEvent.VK_LEFT:
				movedCameraX -= 0.2f;
				break;
			case KeyEvent.VK_RIGHT:
				movedCameraX += 0.2f;
				break;
			case KeyEvent.VK_UP:
				movedCameraZ -= 0.2f;
				break;
			case KeyEvent.VK_DOWN:
				movedCameraZ += 0.2f;
				break;
			case KeyEvent.VK_PAGE_UP:
				movedCameraY += 0.2f;
				break;
			case KeyEvent.VK_PAGE_DOWN:
				movedCameraY -= 0.2f;
				break;
			case KeyEvent.VK_SEMICOLON:
				cameraDistance -= 1;
				break;
			case KeyEvent.VK_MINUS:
				cameraDistance += 1;
				break;
			}
			
			setValue();
		}

		@Override
		public void keyReleased(KeyEvent e) {}
	}
	
	
}
