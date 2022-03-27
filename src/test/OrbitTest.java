package test;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

import tools.Colors;

public class OrbitTest extends JFrame {

	private SimpleUniverse universe;
	@SuppressWarnings("unused")
	private BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);

	TimerInput tInput;

	TransformGroup viewTransformGroup;
	Transform3D viewTransform3d = new Transform3D();

	public static void main(String args[]) {
		new OrbitTest();
	}

	public OrbitTest() {
		setTitle("traffic sim");
		setBounds(400, 400, 600, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		tInput = new TimerInput();
		Timer timer = new Timer();
		timer.schedule(tInput, 1000, 30);

		Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		add(canvas);
		canvas.addKeyListener(new KeyInput());

		universe = new SimpleUniverse(canvas);
		universe.getViewingPlatform().setNominalViewingTransform();


		viewTransformGroup = universe.getViewingPlatform().getViewPlatformTransform();
		/*
		Transform3D viewTransform3d2 = new Transform3D();
		viewTransform3d2.rotX(-Math.PI / 6);
		viewTransform3d.mul(viewTransform3d2);
		viewTransformGroup.setTransform(viewTransform3d2);*/

		universe.addBranchGraph(createScene());
		universe.addBranchGraph(createEnvironment());

		setVisible(true);
	}

	public BranchGroup createScene() {
		BranchGroup root = new BranchGroup();

		TransformGroup transformGroup = new TransformGroup();
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		Transform3D transform3d1 = new Transform3D();
		Transform3D transform3d2 = new Transform3D();

		transform3d1.rotX(Math.PI / 6);
		transform3d2.rotY(Math.PI / 6);
		transform3d1.mul(transform3d2);
		transformGroup.setTransform(transform3d1);

		ColorCube colorCube = new ColorCube(1.0f);
		transformGroup.addChild(colorCube);
		root.addChild(transformGroup);

		return root;
	}

	public BranchGroup createEnvironment() {
		BranchGroup root = new BranchGroup();

		Point3d[] vertices = {new Point3d(-10000.0, -3.0, -10000.0), new Point3d(-10000.0, -3.0, 10000.0),
							  new Point3d(10000.0, -3.0, 10000.0), new Point3d(10000.0, -3.0, -10000.0)};

		QuadArray field = new QuadArray(vertices.length, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
		field.setCoordinates(0, vertices);
		Color3f[] groundColors = {Colors.GRASS, Colors.GRASS, Colors.GRASS, Colors.GRASS};
		field.setColors(0, groundColors);

		BoundingSphere boundingSphere = new BoundingSphere(new Point3d(), 100.0);
		Background background = new Background(Colors.SKY);
		background.setApplicationBounds(boundingSphere);
		root.addChild(background);

		Shape3D shape = new Shape3D(field);
		root.addChild(shape);

		return root;

	}

	class KeyInput implements KeyListener{


		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			tInput.pressed(e.getKeyCode());
		}

		@Override
		public void keyReleased(KeyEvent e) {
			tInput.released(e.getKeyCode());
		}

	}

	class TimerInput extends TimerTask {

		boolean left = false;
		boolean right = false;
		boolean up = false;
		boolean down = false;
		boolean pgup = false;
		boolean pgdown = false;
		boolean rotleft = false;
		boolean rotright = false;

		float vecx = 0.0f;
		float vecy = 0.0f;
		float vecz = 5.0f;

		double rot = 0;

		Vector3f viewTransformVector3f = new Vector3f(vecx, vecy, vecz);
		Transform3D viewTransform3d2 = new Transform3D();


		@Override
		public void run() {
			if(left) {
				vecx -= 0.1f;
			}
			if(right) {
				vecx += 0.1f;
			}
			if(up) {
				vecz -= 0.1f;
			}
			if(down) {
				vecz += 0.1f;
			}
			if(pgup) {
				vecy += 0.1f;
			}
			if(pgdown) {
				vecy -= 0.1f;
			}
			if(rotleft) {
				viewTransform3d2.rotY(Math.PI / 60);
			}
			if (rotright) {
				viewTransform3d2.rotY(-Math.PI / 60);
			}


			viewTransformVector3f.x = vecx;
			viewTransformVector3f.y=  vecy;
			viewTransformVector3f.z = vecz;
			viewTransform3d.mul(viewTransform3d2);
			viewTransform3d.setTranslation(viewTransformVector3f);
			viewTransformGroup.setTransform(viewTransform3d);
		}

		public void pressed(int keyCode) {
			switch(keyCode) {
			case KeyEvent.VK_LEFT:
				left = true;
				break;
			case KeyEvent.VK_RIGHT:

				right = true;
				break;
			case KeyEvent.VK_UP:
				up = true;
				break;
			case KeyEvent.VK_DOWN:
				down = true;
				break;
			case KeyEvent.VK_PAGE_UP:
				pgup = true;
				break;
			case KeyEvent.VK_PAGE_DOWN:
				pgdown = true;
				break;
			case KeyEvent.VK_SLASH:
				rotleft = true;
				break;
			case KeyEvent.VK_BACK_SLASH:
				rotright = true;
				break;
			}
		}

		public void released(int keyCode) {
			switch(keyCode) {
			case KeyEvent.VK_LEFT:
				left = false;
				break;
			case KeyEvent.VK_RIGHT:
				right = false;
				break;
			case KeyEvent.VK_UP:
				up = false;
				break;
			case KeyEvent.VK_DOWN:
				down = false;
				break;
			case KeyEvent.VK_PAGE_UP:
				pgup = false;
				break;
			case KeyEvent.VK_PAGE_DOWN:
				pgdown = false;
				break;
			case KeyEvent.VK_SLASH:
				rotleft = false;
				break;
			case KeyEvent.VK_BACK_SLASH:
				rotright = false;
				break;
			}
		}

	}
}
