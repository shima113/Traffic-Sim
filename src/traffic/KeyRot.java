package traffic;

import java.awt.GraphicsConfiguration;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;

import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;

import tools.NowLoading;

public class KeyRot extends JFrame {
	
	SimpleUniverse universe = null;

	public static void main(String[] args) {
		new KeyRot();
	}
	
	public KeyRot() {
		NowLoading loading = new NowLoading();
		loading.setVisible(true);
		
		setTitle("Keyrot");
		setBounds(500, 400, 600, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		GraphicsConfiguration graphicsConfiguration = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(graphicsConfiguration);
		add(canvas);
		
		universe = new SimpleUniverse(canvas);
		universe.getViewingPlatform().setNominalViewingTransform();
		
		universe.addBranchGraph(CreateScene2());
		
		loading.setVisible(false);
		setVisible(true);
	}
	
	public BranchGroup CreateScene2() {
		BranchGroup scene = new BranchGroup();
		BoundingSphere boundingSphere = new BoundingSphere(new Point3d(), 100.0);
		
		TransformGroup viewTransformGroup = universe.getViewingPlatform().getViewPlatformTransform();
		
		KeyNavigatorBehavior keyNavigatorBehavior = new KeyNavigatorBehavior(viewTransformGroup);
		keyNavigatorBehavior.setSchedulingBounds(boundingSphere);
		PlatformGeometry platgeo = new PlatformGeometry();
		platgeo.addChild(keyNavigatorBehavior);
		universe.getViewingPlatform().setPlatformGeometry(platgeo);
		
		Transform3D transform3d = new Transform3D();
		transform3d.setRotation(new AxisAngle4d(0.2, 0.3, -0.1, Math.PI / 4.0));
		
		TransformGroup transformGroup = new TransformGroup(transform3d);
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		transformGroup.addChild(new ColorCube(0.4));
		scene.addChild(transformGroup);
		
		return scene;
	}
	
	public BranchGroup CreateScene() {
		BranchGroup scene = new BranchGroup();
		BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);
		
		TransformGroup tGroup = new TransformGroup();
		tGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		tGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		KeyNavigatorBehavior keybehavior = new KeyNavigatorBehavior(tGroup);
		keybehavior.setSchedulingBounds(bounds);
		scene.addChild(keybehavior);
		
		tGroup.addChild(new ColorCube(0.4));
		scene.addChild(tGroup);
		
		return scene;
	}

}
