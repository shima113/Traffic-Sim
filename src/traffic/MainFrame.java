/* Trafic Simulator ver1.0.0
 * 単位：0.01f = 1m 
 */
package traffic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import com.sun.j3d.utils.universe.SimpleUniverse;

import cars.PassengerCar;
import tools.Colors;
import tools.NowLoading;

public class MainFrame extends JFrame {
	
	private SimpleUniverse universe;
	
	TransformGroup viewTransformGroup;
	Transform3D viewTransform3d = new Transform3D();
	BranchGroup carBranchGroup;
	
	JLabel accelDisplay, speedDisplay, distanceDisplay;
	
	ArrayList<Node> nodeGroup2, nodeGroup3, nodeGroup4;
	
	PassengerCar car1, car2, car3;
	
	int militime = 40;
	
	public static void main(String args[]) {
		new MainFrame();
	}
	
	public MainFrame() {
		//setting loading gamen
		NowLoading loading = new NowLoading();
		loading.setVisible(true);
		
		//setting swing
		setTitle("traffic sim");
		setBounds(500, 300, 1800, 1800);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//setting java3D
		Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		canvas.setBounds(0, 0, 1600, 1600);
		JPanel cp = new JPanel();
		cp.setLayout(null);
		cp.add(canvas);
		add(cp);
		
		universe = new SimpleUniverse(canvas);
		
		ViewChange viewChange = new ViewChange(canvas, universe, 15.0f);
		viewChange.setSensitivity(0.01f);
		
		viewTransformGroup = universe.getViewingPlatform().getViewPlatformTransform();
		
		carBranchGroup = createScene();	
		
		carBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

		universe.addBranchGraph(carBranchGroup);
		universe.addBranchGraph(createEnvironment());
		
		JButton accelButton = new JButton("加速");
		accelButton.setBounds(20, 1650, 100, 30);
		accelButton.addActionListener(car1);
		accelButton.addActionListener(car2);
		accelButton.addActionListener(car3);
		accelButton.setActionCommand("accel");
		cp.add(accelButton);
		
		accelDisplay = new JLabel("状態：" + car1.getAccel());
		accelDisplay.setBounds(130, 1650, 100, 30);
		cp.add(accelDisplay);
		
		JButton brakeButton = new JButton("ブレーキ");
		brakeButton.setBounds(20, 1720, 100, 30);
		brakeButton.addActionListener(car1);
		brakeButton.addActionListener(car2);
		brakeButton.addActionListener(car3);
		brakeButton.setActionCommand("brake");
		cp.add(brakeButton);
		
		speedDisplay = new JLabel("速度：" + car1.getSpeed());
		speedDisplay.setBounds(250, 1650, 100, 30);
		cp.add(speedDisplay);
		
		distanceDisplay = new JLabel("走行距離：" + car1.getTotalDistance());
		distanceDisplay.setBounds(250, 1700, 100, 30);
		cp.add(distanceDisplay);
		
		JButton reflesh = new JButton("reload");
		reflesh.setBounds(1700, 200, 50, 50);
		reflesh.addActionListener(new RefleshButton());
		cp.add(reflesh);
		
		Timer timer = new Timer();
		timer.schedule(new TimerReculc(), 0, militime);
		
		Timer carTimer = new Timer();
		carTimer.schedule(new carTimer(), 5000, 1000);
		
		loading.setVisible(false);
		setVisible(true);
	}
	
	//add cars to universe and create nodes
	private BranchGroup createScene() {
		BranchGroup root = new BranchGroup();
		
		/*StraightNode node1 = new StraightNode(5.0f, 0);
		CurveNode node2 = new CurveNode(1.5f, 0, Math.PI / 4, new Point3f(-1.5f, 0.0f, 2.0f), 0, 0);
		StraightNode node = new StraightNode(2.0f, 0, new Point3f(0.0f, 0.0f, 0.0f), 0);
		StraightNode node3 = new StraightNode
				(3.0f, Math.PI / 4, new Point3f((float) (-(1.5 - 1.5 / Math.sqrt(2.0))), 0.0f, (float) (2.0f + 1.5 / Math.sqrt(2.0))), 1.0f);
		ArrayList<Node> nodeGroup1 = new ArrayList<>();
		nodeGroup1.add(node);
		nodeGroup1.add(node2);
		nodeGroup1.add(node3);*/
		
		StraightNode node4 = new StraightNode(2.2f, Math.PI, new Point3f(0, 0, 0), 0);
		CurveNode node5 = new CurveNode(0.54f, Math.PI, Math.PI * 3 / 4, new Point3f(0.54f, 0.0f, -2.2f), 0.06f, 0);
		StraightNode node6 = new StraightNode
				(0.71f, Math.PI * 7 / 4, new Point3f((float)(0.54f + 0.54f / Math.sqrt(2)), 0.06f, (float) (-2.2f - 0.54f / Math.sqrt(2))), 0.02f);
		CurveNode node7 = new CurveNode(-1.38f, Math.PI * 7 / 4,
				-Math.PI / 4, new Point3f((float)(0.54f + 2.61f / Math.sqrt(2)), 0, (float) (-2.2f - 1.2f / Math.sqrt(2))), 0.0f, 0.08f);
		StraightNode node8 = new StraightNode(2.0f, Math.PI * 3 / 2, 
				new Point3f((float)(0.54f + 2.61f / Math.sqrt(2)), 0.08f, (float) (-0.82 - 1.2f / Math.sqrt(2))), 0.0f);
		
		nodeGroup2 = new ArrayList<>();
		nodeGroup2.add(node4);
		nodeGroup2.add(node5);
		nodeGroup2.add(node6);
		nodeGroup2.add(node7);
		nodeGroup2.add(node8);
		
		StraightNode node9 = new StraightNode(2.07f, Math.PI * 3 / 2, new Point3f(-1.6f, 0.08f, -1.67f), 0);
		CurveNode node10 = new CurveNode(-0.53f, Math.PI * 3 / 2, -Math.PI / 2, new Point3f(0.47f, 0, -2.2f), 0, 0.08f);
		CurveNode node11 = new CurveNode(-0.46f, Math.PI, -Math.PI, new Point3f(0.54f, 0, -2.2f), -0.08f, 0.08f);
		StraightNode node12 = new StraightNode(2.2f, 0, new Point3f(0.08f, 0, -2.2f), 0);
		
		nodeGroup3 = new ArrayList<>();
		nodeGroup3.add(node9);
		nodeGroup3.add(node10);
		nodeGroup3.add(node11);
		nodeGroup3.add(node12);
		
		StraightNode node13 = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-3.30f, 0.08f, -1.59f), 0);
		StraightNode node14 = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-0.80f, 0.08f, -1.59f), 0);
		StraightNode node15 = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(1.70f, 0.08f, -1.59f), 0);
		StraightNode node16 = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(4.20f, 0.08f, -1.59f), 0);
		
		nodeGroup4 = new ArrayList<>();
		nodeGroup4.add(node13);
		nodeGroup4.add(node14);
		nodeGroup4.add(node15);
		nodeGroup4.add(node16);
		
		car1 = new PassengerCar(militime, nodeGroup3, new Point3f(-1.6f, 0.08f, -1.67f), Math.PI * 3 / 2);
		car2 = new PassengerCar(militime, nodeGroup2, new Point3f(0, 0, 0), Math.PI);
		car3 = new PassengerCar(militime, nodeGroup4, new Point3f(-3.30f, 0.08f, -1.59f), Math.PI * 3 / 2);
		root.addChild(car1.carObjectGroup);
		root.addChild(car2.carObjectGroup);
		root.addChild(car3.carObjectGroup);
		
		car2.setAtoNode(node16);
		car2.setAtnodegroupArrayList(nodeGroup4);
		
		car1.setCarnum(2);
		car2.setCarnum(10);
		car3.setCarnum(3);
		
		return root;
	}
	
	//create background(sky), ground(grass)
	private BranchGroup createEnvironment() {
		BranchGroup root = new BranchGroup();
		
		Point3d[] vertices = {new Point3d(-10000.0, -0.075, -10000.0), new Point3d(-10000.0, -0.075, 10000.0),
							  new Point3d(10000.0, -0.075, 10000.0), new Point3d(10000.0, -0.075, -10000.0)};
		
		QuadArray field = new QuadArray(vertices.length, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
		field.setCoordinates(0, vertices);
		Color3f[] groundColors = {Colors.GRASS, Colors.GRASS, Colors.GRASS, Colors.GRASS};
		field.setColors(0, groundColors);

		BoundingSphere boundingSphere = new BoundingSphere(new Point3d(), 10000.0);
		Background background = new Background(Colors.SKY);
		background.setApplicationBounds(boundingSphere);
		root.addChild(background);
		
		Shape3D shape = new Shape3D(field);
		root.addChild(shape);
		
		return root;
		
	}
	
	class RefleshButton implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			new MainFrame();
		}
		
	}
	
	class TimerReculc extends TimerTask{

		@Override
		public void run() {
			speedDisplay.setText("速度：" + car1.getSpeed() + "km/h");
			distanceDisplay.setText("走行距離" + car1.getTotalDistanceDisplay() + "m");
			accelDisplay.setText("状態：" + car1.getAccel());
		}
		
	}
	
	class carTimer extends TimerTask{

		@Override
		public void run() {
			/*
			BranchGroup tempBranchGroup = new BranchGroup();
			PassengerCar car = new PassengerCar(militime, nodeGroup2, new Point3f(0, 0, 0), Math.PI);
			Mascon mascon = new Mascon(car, militime);
			tempBranchGroup.addChild(car.carObjectGroup);
			carBranchGroup.addChild(tempBranchGroup);
			*/
		}
		
	}
}
