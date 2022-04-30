/* Trafic Simulator ver1.0.0
 * 単位：0.01f = 1m
 */
package traffic;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.sun.j3d.utils.universe.SimpleUniverse;

import cars.PassengerCar;
import tools.Colors;
import tools.NowLoading;

public class MainFrame extends JFrame {

	TransformGroup viewTransformGroup;
	Transform3D viewTransform3d = new Transform3D();
	BranchGroup carBranchGroup;
	PassengerCar[] cars;

	ArrayList<CarList> carLists = new ArrayList<>();

	JLabel accelDisplay, speedDisplay, distanceDisplay;

	//PassengerCar car1, car2, car3;

	int militime = 40;

	public static void main(String[] args) {
		new MainFrame();
	}

	/**
	 * 行っていること<br>
	 * <ul>
	 *     <li>ロード画面の作成</li>
	 *     <li>フレームの初期設定</li>
	 *     <li>Java3Dの設定（Universeの生成など）</li>
	 *     <li>ViewChangeクラスの設定</li>
	 *     <li>出力するExcelファイルの作成</li>
	 * </ul>
	 */
	public MainFrame() {
		//setting loading gamen
		NowLoading loading = new NowLoading();
		loading.setVisible(true);

		//setting swing
		setTitle("traffic sim");
		setBounds(0, 0, 1800, 1000);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		//setting java3D
		Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		canvas.setBounds(0, 0, 1600, 900);
		JPanel cp = new JPanel();
		cp.setLayout(null);
		cp.add(canvas);
		add(cp);

		SimpleUniverse universe = new SimpleUniverse(canvas);

		ViewChange viewChange = new ViewChange(canvas, universe, 15.0f);
		viewChange.setSensitivity(0.01f);

		viewTransformGroup = universe.getViewingPlatform().getViewPlatformTransform();

		createCarList();
		carBranchGroup = createScene();

		createRoad();

		carBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

		universe.addBranchGraph(carBranchGroup);
		universe.addBranchGraph(createEnvironment());

		createExcel();

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exportSheet();
			}
		});

		JButton change = new JButton("車線変更");
		change.setBounds(1650, 100, 100, 50);
		change.addActionListener(cars[2]);
		cp.add(change);

		Timer carTimer = new Timer();
		carTimer.schedule(new carTimer(), 2000, 600);

		Timer carTime2r = new Timer();
		//carTime2r.schedule(new carTime2r(), 1000);

		loading.setVisible(false);
		setVisible(true);
	}

	Workbook workbook = new HSSFWorkbook();
	Sheet sheet1;

	//作るだけ
	private void createExcel() {
		sheet1 = workbook.createSheet();
		workbook.setSheetName(0,"yahhhhh");
	}

	//ウィンドウ閉じたときに呼ばれる
	private void exportSheet(){
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter dFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
		//String folderpath = "C:\\Users\\kouhe\\Desktop\\program files\\traffic\\TrafficSim_";
		String folderpath = "C:\\Users\\kohei\\Documents\\program files\\traffic\\TrafficSim_";
		String filepath = folderpath + dFormatter.format(localDateTime) + ".xlsx";

		FileOutputStream fStream = null;
		try {
			fStream = new FileOutputStream(filepath);
			workbook.write(fStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}



	//add cars to universe and create nodes
	private BranchGroup createScene() {
		BranchGroup root = new BranchGroup();

		cars = new PassengerCar[9];

		cars[0] = new PassengerCar(militime, createNodeGroup2(), new Point3f(0, 0, 2.5f), Math.PI, 13.88889);
		cars[1] = new PassengerCar(militime, createNodeGroup3(), new Point3f(-1.6f, 0.08f, -1.67f), Math.PI, 13.88889);
		cars[2] = new PassengerCar(militime, createNodeGroup4(), new Point3f(-3.30f, 0.08f, -1.59f), Math.PI, 27.77778);
		cars[3] = new PassengerCar(militime, createNodeGroup4(), new Point3f(-3.30f, 0.08f, -1.59f), Math.PI, 27.77778);
		cars[4] = new PassengerCar(militime, createNodeGroup5(), new Point3f(-3.30f, 0.08f, -1.55f), Math.PI, 27.77778);
		cars[5] = new PassengerCar(militime, createNodeGroup6(), new Point3f(6.70f, 0.08f, -1.47f), Math.PI, 27.77778);
		cars[6] = new PassengerCar(militime, createNodeGroup7(), new Point3f(6.70f, 0.08f, -1.43f), Math.PI, 27.77778);
		cars[7] = new PassengerCar(militime, createNodeGroup8(), new Point3f(0, 0, 2.5f), Math.PI, 27.77778);
		cars[8] = new PassengerCar(militime, createNodeGroup9(), new Point3f(3.23f, 0.08f, -1.35f), Math.PI, 27.77778);

		for (int i = 0; i < cars.length; i++) {
			if (i < 2){
				cars[i].setNodegroupIndex(i + 2);
			}else {
				cars[i].setNodegroupIndex(i + 1);
			}
			root.addChild(cars[i].carObjectGroup);
		}

		return root;
	}

	private void createCarList() {
		for(int i = 0; i < 8; i++) {
		    carLists.add(new CarList());
		}

		carLists.get(2).setToChangeLane(createNodeGroup5());
		carLists.get(3).setToChangeLane(createNodeGroup4());
		carLists.get(4).setToChangeLane(createNodeGroup7());
		carLists.get(5).setToChangeLane(createNodeGroup6());

		carLists.get(0).setBunkiCarList(carLists.get(6));
	}

	private void createRoad(){
		Road[] roads = new Road[2];

		roads[0] = new Road();
		roads[1] = new Road();

		roads[0].add(carLists.get(2));
		roads[0].add(carLists.get(3));
		roads[1].add(carLists.get(5));
		roads[1].add(carLists.get(4));

		carLists.get(2).setRoad(roads[0]);
		carLists.get(3).setRoad(roads[0]);
		carLists.get(4).setRoad(roads[1]);
		carLists.get(5).setRoad(roads[1]);
	}

	/*　＿
	　／　 ＼＿→
	　│　
	　↑　　
	 */
	private NodeList createNodeGroup2() {
		NodeList nodeGroup2 = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0, 2.5f), 0);
		nodes[1] = new StraightNode(2.2f, Math.PI, new Point3f(0, 0, 0), 0);
		nodes[2] = new CurveNode(0.54f, Math.PI, Math.PI * 3 / 4, new Point3f(0.54f, 0.0f, -2.2f), 0.06f, 0);
		nodes[3] = new StraightNode
				(0.71f, Math.PI * 7 / 4, new Point3f((float)(0.54f + 0.54f / Math.sqrt(2)), 0.06f, (float) (-2.2f - 0.54f / Math.sqrt(2))), 0.02f);
		nodes[4] = new CurveNode(-1.38f, Math.PI * 7 / 4,
				-Math.PI / 4, new Point3f((float)(0.54f + 2.61f / Math.sqrt(2)), 0, (float) (-2.2f - 1.2f / Math.sqrt(2))), 0.0f, 0.08f);
		nodes[5] = new StraightNode(2.0f, Math.PI * 3 / 2,
				new Point3f((float)(0.54f + 2.61f / Math.sqrt(2)), 0.08f, (float) (-0.82 - 1.2f / Math.sqrt(2))), 0.0f);

		nodes[0].setLimitSpeed(11.11111);
		nodes[1].setLimitSpeed(11.11111);
		nodes[3].setLimitSpeed(11.11111);
		nodes[5].setLimitSpeed(11.11111);

		for (Node node : nodes) {
			nodeGroup2.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup2;
	}

	/*　　 ＿
	　　 ／ 　＼
	　　│　　　│
　　→＿＿＿＿＿／
	　　│
	　　↓
	 */
	private NodeList createNodeGroup3() {
		NodeList nodeGroup3 = new NodeList();

		Node[] nodes = new Node[5];

		nodes[0] = new StraightNode(2.07f, Math.PI * 3 / 2, new Point3f(-1.6f, 0.08f, -1.67f), 0);
		nodes[1] = new CurveNode(-0.53f, Math.PI * 3 / 2, -Math.PI / 2, new Point3f(0.47f, 0, -2.2f), 0, 0.08f);
		nodes[2] = new CurveNode(-0.46f, Math.PI, -Math.PI, new Point3f(0.54f, 0, -2.2f), -0.08f, 0.08f);
		nodes[3] = new StraightNode(2.2f, 0, new Point3f(0.08f, 0, -2.2f), 0);
		nodes[4] = new StraightNode(2.5f, 0, new Point3f(0.08f, 0, 0), 0);

		nodes[0].setLimitSpeed(11.11111);
		nodes[3].setLimitSpeed(11.11111);
		nodes[4].setLimitSpeed(11.11111);

		for (Node node : nodes) {
			nodeGroup3.add(node);
			node.setNowOnCars(carLists.get(1));
		}

		return nodeGroup3;
	}

	private NodeList createNodeGroup4() {
		NodeList nodeGroup4 = new NodeList();

		Node[] nodes = new Node[4];

		nodes[0] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-3.30f, 0.08f, -1.59f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-0.80f, 0.08f, -1.59f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(1.70f, 0.08f, -1.59f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(4.20f, 0.08f, -1.59f), 0);

		for (Node node : nodes) {
			nodeGroup4.add(node);
			node.setNowOnCars(carLists.get(2));
		}
		nodeGroup4.setCarList(carLists.get(2));

		return nodeGroup4;
	}

	private NodeList createNodeGroup5() {
		NodeList nodeGroup5 = new NodeList();

		Node[] nodes = new Node[4];

		nodes[0] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-3.30f, 0.08f, -1.55f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-0.80f, 0.08f, -1.55f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(1.70f, 0.08f, -1.55f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(4.20f, 0.08f, -1.55f), 0);

		for (Node node : nodes) {
			nodeGroup5.add(node);
			node.setNowOnCars(carLists.get(3));
		}
		nodeGroup5.setCarList(carLists.get(3));

		return nodeGroup5;
	}

	private NodeList createNodeGroup6() {
		NodeList nodeGroup6 = new NodeList();

		Node[] nodes = new Node[4];

		nodes[0] = new StraightNode(2.5f, Math.PI / 2, new Point3f(6.70f, 0.08f, -1.47f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI / 2, new Point3f(4.20f, 0.08f, -1.47f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI / 2, new Point3f(1.70f, 0.08f, -1.47f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-0.80f, 0.08f, -1.47f), 0);

		for (Node node : nodes) {
			nodeGroup6.add(node);
			node.setNowOnCars(carLists.get(4));
		}
		nodeGroup6.setCarList(carLists.get(4));

		return nodeGroup6;
	}

	private NodeList createNodeGroup7() {
		NodeList nodeGroup7 = new NodeList();

		Node[] nodes = new Node[4];

		nodes[0] = new StraightNode(2.5f, Math.PI / 2, new Point3f(6.70f, 0.08f, -1.43f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI / 2, new Point3f(4.20f, 0.08f, -1.43f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI / 2, new Point3f(1.70f, 0.08f, -1.43f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-0.80f, 0.08f, -1.43f), 0);

		for (Node node : nodes) {
			nodeGroup7.add(node);
			node.setNowOnCars(carLists.get(5));
		}
		nodeGroup7.setCarList(carLists.get(5));

		return nodeGroup7;
	}

	private NodeList createNodeGroup8(){
		NodeList nodeGroup8 = new NodeList();

		Node[] nodes = new Node[5];

		nodes[0] = new StraightNode(2.30f, Math.PI, new Point3f(0, 0, 2.5f), 0);
		nodes[1] = new CurveNode(-0.26f, Math.PI, -Math.asin(10.0 / 26.0), new Point3f(-0.26f, 0, 0.20f), 0, 0);
		nodes[2] = new CurveNode(0.26f, Math.PI - Math.asin(10.0 / 26.0), Math.asin(10.0 / 26.0), new Point3f(0.22f, 0, 0), 0, 0);
		nodes[3] = new CurveNode(-1.35f, Math.PI, -Math.PI / 2, new Point3f(-1.39f, 0.0f, 0.0f), 0.08f, 0);
		nodes[4] = new StraightNode(1.80f, Math.PI / 2, new Point3f(-1.39f, 0.08f, -1.35f), 0);

		nodes[0].setLimitSpeed(11.11111);
		nodes[1].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[2].setType(NodeType.CHANGE_LANE_SECOND);

		for (Node node : nodes){
			nodeGroup8.add(node);
			node.setNowOnCars(carLists.get(6));
		}

		nodes[0].setNowOnCars(carLists.get(0));
		nodes[1].setNowOnCars(carLists.get(0));
		nodes[2].setNowOnCars(carLists.get(0));

		return nodeGroup8;
	}

	private NodeList createNodeGroup9(){
		NodeList nodeGroup9 = new NodeList();

		Node[] nodes = new Node[2];

		nodes[0] = new StraightNode(1.80f, Math.PI / 2, new Point3f(3.27f, 0.08f, -1.35f), 0);
		nodes[1] = new CurveNode(-1.35f, Math.PI / 2, -Math.PI / 2, new Point3f(1.47f, 0.0f, 0.0f), -0.08f, 0.08f);

		for (Node node : nodes){
			nodeGroup9.add(node);
			node.setNowOnCars(carLists.get(7));
		}

		return nodeGroup9;
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

	/**
	 * 車を生成するクラス
	 */
	class carTimer extends TimerTask{

		int rowIndex = 1;

		@Override
		public void run() {
			BranchGroup tempBranchGroup = new BranchGroup();
			PassengerCar car;

			int rand = (int) (Math.random() * 8);
			int startIndex = carLists.get(rand).getCarSize() - 1;
			double startSpeed;
			if (startIndex >= 0) {
				startSpeed = carLists.get(rand).getCar(startIndex).getSpeed();
			}else {
				if (rand >= 2 && rand <= 5) {
					startSpeed = 27.77778;
				}else {
					startSpeed = 11.11111;
				}
			}
			switch(rand) {
			case 0:
				car = new PassengerCar(militime, createNodeGroup2(), new Point3f(0, 0, 2.5f), Math.PI, startSpeed);
				car.setNodegroupIndex(2);
				break;
			case 1:
				car = new PassengerCar(militime, createNodeGroup3(), new Point3f(-1.6f, 0.08f, -1.67f), Math.PI * 3 / 2, startSpeed);
				car.setNodegroupIndex(3);
				break;
			case 2:
				car = new PassengerCar(militime, createNodeGroup4(), new Point3f(-3.30f, 0.08f, -1.59f), Math.PI * 3 / 2, startSpeed);
				car.setNodegroupIndex(4);
				break;
			case 3:
				car = new PassengerCar(militime, createNodeGroup5(), new Point3f(-3.30f, 0.08f, -1.55f), Math.PI * 3 / 2, startSpeed);
				car.setNodegroupIndex(5);
				break;
			case 4:
				car = new PassengerCar(militime, createNodeGroup6(), new Point3f(6.70f, 0.08f, -1.47f), Math.PI / 2, startSpeed);
				car.setNodegroupIndex(6);
				break;
			case 5:
				car = new PassengerCar(militime, createNodeGroup7(), new Point3f(6.70f, 0.08f, -1.43f), Math.PI / 2, startSpeed);
				car.setNodegroupIndex(7);
				break;
			case 6:
				car = new PassengerCar(militime, createNodeGroup8(), new Point3f(0.0f, 0.0f, 2.5f), Math.PI, startSpeed);
				car.setNodegroupIndex(8);
				break;
			case 7:
				car = new PassengerCar(militime, createNodeGroup9(), new Point3f(3.23f, 0.08f, -1.35f), Math.PI / 2, startSpeed);
				car.setNodegroupIndex(9);
				break;
			default:
				car = new PassengerCar();
			}
			tempBranchGroup.addChild(car.carObjectGroup);
			carBranchGroup.addChild(tempBranchGroup);

			car.setExpSheet(sheet1);
			car.setSheetIndex(rowIndex);
			rowIndex++;
		}

	}

	/**
	 * 確認用
	 */
	class carTime2r extends TimerTask{

		PassengerCar car;

		public carTime2r() {
			Timer timer = new Timer();
			timer.schedule(new tm2(), 200, 50);
		}

		@Override
		public void run() {
			BranchGroup tempBranchGroup = new BranchGroup();

			car = new PassengerCar(militime, createNodeGroup4(), new Point3f(-3.30f, 0.08f, -1.59f), Math.PI, 27.77778);

			tempBranchGroup.addChild(car.carObjectGroup);
			carBranchGroup.addChild(tempBranchGroup);

		}

		class tm2 extends TimerTask{

			@Override
			public void run() {
				//System.out.println(car.getSpeed());
			}

		}

	}
}
