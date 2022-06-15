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

import cars.Car;
import cars.Track;
import org.apache.commons.compress.harmony.pack200.NewAttribute;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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

	//PassengerCar car1, car2, car3;

	int militime = 40;

	public static void main(String[] args) {
		Timer exitTimer = new Timer();
		exitTimer.schedule(new TimerTask() {
			MainFrame f;
			int kaisu = 0;
			@Override
			public void run() {
				if (kaisu == 0){
					f = new MainFrame();
					kaisu++;
				} else if (kaisu == 3) {
					f.exportSheet();
					System.exit(0);
				} else {
					f.exportSheet();
					f.setVisible(false);
					f = null;
					f = new MainFrame();
					kaisu++;
				}
			}
		}, 0, 300000);
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
		setBounds(250, 50, 1650, 1000);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		//setting java3D
		Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		canvas.setBounds(0, 0, 1600, 900);
		JPanel cp = new JPanel();
		cp.setLayout(null);
		cp.add(canvas);
		add(cp);

		SimpleUniverse universe = new SimpleUniverse(canvas);

		ViewChange viewChange = new ViewChange(canvas, universe, 17.0f);
		viewChange.setSensitivity(0.01f);

		viewTransformGroup = universe.getViewingPlatform().getViewPlatformTransform();

		createCarList();
		carBranchGroup = createScene();

		createRoad();

		carBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

		universe.addBranchGraph(carBranchGroup);
		universe.addBranchGraph(createEnvironment());

		createExcel();

		/*this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exportSheet();
			}
		});*/

		JButton change = new JButton("車線変更");
		change.setBounds(1650, 100, 100, 50);
		change.addActionListener(cars[2]);
		cp.add(change);;

		Timer mainTimer = new Timer();
		mainTimer.schedule(new carTimer(), 2000, 4000);

		Timer lampTimer = new Timer();
		lampTimer.schedule(new LampTimer(), 2700, 4000);

		Timer carTime2r = new Timer();
		//carTime2r.schedule(new carTime2r(), 1000);

		Timer logTimer = new Timer();
		/*logTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				for (int i = 0; i < carLists.get(7).getCarSize(); i++) {
					System.out.print(carLists.get(7).getCar(i).getTotalDistance());
					System.out.print("[" + carLists.get(7).getCar(i).getNodegroupIndex() + "]" + ", ");
				}
				System.out.println("");
			}
		}, 0, 40);*/

		Timer loger = new Timer();
		loger.schedule(new TimerTask() {
			double t = -0.5;
			@Override
			public void run() {
				t += 0.5;
				System.out.println(t + "分");
			}
		}, 0, 30000);

		loading.setVisible(false);
		setVisible(true);
	}

	Workbook workbook = new XSSFWorkbook();
	Sheet sheet1;

		//作るだけ
		private void createExcel() {
		sheet1 = workbook.createSheet();
		workbook.setSheetName(0,"data");

		for (int i = 0; i < 8; i++) {
			Row row = sheet1.createRow(i);
			Cell num = row.createCell(0);
			Cell ave = row.createCell(1);

			String rowIndex = String.valueOf(i + 1);

			num.setCellValue(String.valueOf(i + 2));
			ave.setCellFormula("AVERAGEIF($D$10:$D$2000,A" + rowIndex + ",$C$10:$C$2000)");
		}
	}

	//ウィンドウ閉じたときに呼ばれる
	private void exportSheet(){
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter dFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
		//String folderpath = "C:\\Users\\kouhe\\Desktop\\program files\\traffic\\TrafficSim_";
		String folderpath = "C:\\Users\\kohei\\Documents\\Excel\\traffic\\TrafficSim_";
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



	//ここから下はシステム内部関連
	//add cars to universe and create nodes
	private BranchGroup createScene() {
		BranchGroup root = new BranchGroup();

		cars = new PassengerCar[9];

		/*cars[0] = new PassengerCar(militime, createNodeGroup2(), new Point3f(0, 0.08f, 2.5f), Math.PI, 27.77778);
		cars[1] = new PassengerCar(militime, createNodeGroup3(), new Point3f(-5.80f, 0.08f, -1.59f), Math.PI, 27.77778);
		cars[2] = new PassengerCar(militime, createNodeGroup4(), new Point3f(-5.80f, 0.08f, -1.59f), Math.PI, 27.77778);
		cars[3] = new PassengerCar(militime, createNodeGroup4(), new Point3f(-5.80f, 0.08f, -1.59f), Math.PI, 27.77778);
		cars[4] = new PassengerCar(militime, createNodeGroup5(), new Point3f(-5.80f, 0.08f, -1.55f), Math.PI, 27.77778);
		cars[5] = new PassengerCar(militime, createNodeGroup6(), new Point3f(6.70f, 0.08f, -1.47f), Math.PI, 27.77778);
		cars[6] = new PassengerCar(militime, createNodeGroup7(), new Point3f(6.70f, 0.08f, -1.43f), Math.PI, 27.77778);
		cars[7] = new PassengerCar(militime, createNodeGroup8(), new Point3f(0, 0.08f, 2.5f), Math.PI, 13.88889);
		cars[8] = new PassengerCar(militime, createNodeGroup9(), new Point3f(6.70f, 0.08f, -1.43f), Math.PI, 27.77778);*/

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
		    CarList car = new CarList();

			carLists.add(car);
			car.setNodegroupIndex(i + 2);
		}

		carLists.get(2).setToChangeLane(createNodeGroup5());
		carLists.get(3).setToChangeLane(createNodeGroup4());
		carLists.get(4).setToChangeLane(createNodeGroup7());
		carLists.get(5).setToChangeLane(createNodeGroup6());

		carLists.get(0).setBunkiCarList(carLists.get(6));
		carLists.get(2).setBunkiCarList(carLists.get(1));
		carLists.get(5).setBunkiCarList(carLists.get(7));

		carLists.get(6).setGouryuCarList(carLists.get(5));
		carLists.get(7).setGouryuCarList(carLists.get(1));
		carLists.get(0).setGouryuCarList(carLists.get(2));
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

	private final double CHANGELANE_ANGLE = Math.asin(10.0 / 26.0);

	private NodeList createNodeGroup1(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, 7.5f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, 5.0f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, 2.5f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, 0.0f), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, -2.5f), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, -5.0f), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
	}

	private NodeList createNodeGroup2(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, Math.PI, new Point3f(0.04f, 0.08f, 7.5f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI, new Point3f(0.04f, 0.08f, 5.0f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI, new Point3f(0.04f, 0.08f, 2.5f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI, new Point3f(0.04f, 0.08f, 0.0f), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI, new Point3f(0.04f, 0.08f, -2.5f), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI, new Point3f(0.04f, 0.08f, -5.0f), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
	}

	private NodeList createNodeGroup3(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[1] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, -5.0f), 0);
		nodes[0] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, -7.5f), 0);
		nodes[2] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, -2.5f), 0);
		nodes[3] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, 0.0f), 0);
		nodes[4] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, 2.5f), 0);
		nodes[5] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, 5.0f), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
	}

	private NodeList createNodeGroup4(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, -7.5f), 0);
		nodes[1] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, -5.0f), 0);
		nodes[2] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, -2.5f), 0);
		nodes[3] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, 0.0f), 0);
		nodes[4] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, 2.5f), 0);
		nodes[5] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, 5.0f), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
	}

	private NodeList createNodeGroup5(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-7.5f, 0, 0), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-5.0f, 0, 0), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-2.5f, 0, 0), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(0.0f, 0, 0), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(2.5f, 0, 0), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(5.0f, 0, 0), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
	}

	private NodeList createNodeGroup6(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-7.5f, 0, 0.04f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-5.0f, 0, 0.04f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-2.5f, 0, 0.04f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(0.0f, 0, 0.044f), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(2.5f, 0, 0.04f), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(5.0f, 0, 0.04f), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
	}

	private NodeList createNodeGroup7(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, Math.PI / 2, new Point3f(7.5f, 0, 0.12f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI / 2, new Point3f(5.0f, 0, 0.12f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI / 2, new Point3f(2.5f, 0, 0.12f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI / 2, new Point3f(0.0f, 0, 0.12f), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-2.5f, 0, 0.12f), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-5.0f, 0, 0.12f), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
	}

	private NodeList createNodeGroup8(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, Math.PI / 2, new Point3f(7.5f, 0, 0.16f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI / 2, new Point3f(5.0f, 0, 0.16f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI / 2, new Point3f(2.5f, 0, 0.16f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI / 2, new Point3f(0.0f, 0, 0.16f), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-2.5f, 0, 0.16f), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-5.0f, 0, 0.16f), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
	}

	private final float x = 271.289321881345f;

	private NodeList createNodeGroup9(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[3];

		nodes[0] = new CurveNode(-x, Math.PI, -Math.PI / 4, new Point3f(-0.08f - x, 0, 3.66f), 0, 0.08f);
		nodes[1] = new StraightNode(1.00f, Math.PI * 3 / 4,
				new Point3f((float) (-0.08 - x * (1 - Math.cos(Math.PI / 4))), 0.08f, (float) (3.66 - x * Math.sin(Math.PI / 4))), -0.08f);
		nodes[2] = new CurveNode(-x, Math.PI * 3 / 4, -Math.PI / 4, new Point3f(-3.50f, 0, 0.24f + x), 0, 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
	}

	private NodeList createNodeGroup10(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[3];

		nodes[0] = new CurveNode(-x, Math.PI * 3 / 2, -Math.PI / 4, new Point3f(-3.50f, 0, -0.08f - x), 0, 0);
		nodes[1] = new StraightNode(1.00f, Math.PI * 5 / 4,
				new Point3f((float) (-3.50 + x * Math.sin(Math.PI / 4)), 0, (float) (-0.08 - x * (1 - Math.cos(Math.PI / 4)))), 0.08f);
		nodes[2] = new CurveNode(-x, Math.PI * 5 / 4, -Math.PI / 4, new Point3f())

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
	}

	private NodeList createNodeGroup11(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[3];

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
	}

	private NodeList createNodeGroup12(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[3];

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
	}

	private NodeList createNodeGroup13(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[3];

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
	}

	private NodeList createNodeGroup14(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[3];

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
	}

	private NodeList createNodeGroup15(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[3];

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
	}

	private NodeList createNodeGroup16(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[3];

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		return nodeGroup;
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

	int rowIndex = 9;

	/**
	 * 車を生成するクラス
	 */
	class carTimer extends TimerTask{

		@Override
		public void run() {
			BranchGroup tempBranchGroup = new BranchGroup();
			Car car;

			int rand = (int) (Math.random() * 4);
			int carType = (int) (Math.random() * 4);
			int startIndex = carLists.get(rand).getCarSize() - 1;
			double startSpeed;

			if (startIndex >= 0) startSpeed = carLists.get(rand).getCar(startIndex).getSpeed();
			else startSpeed = rand >= 2 && rand <= 5 ? 27.77778 : 11.11111;

			NodeList nodeList = null;
			Point3f start = null;
			double direction = 0;

			if (rand == 0) {
				nodeList = createNodeGroup4();
				start = new Point3f(-5.80f, 0.08f, -1.59f);
				direction = Math.PI * 3 / 2;
			} else if (rand == 1) {
				nodeList = createNodeGroup5();
				start = new Point3f(-5.80f, 0.08f, -1.55f);
				direction = Math.PI * 3 / 2;
			} else if (rand == 2) {
				nodeList = createNodeGroup6();
				start = new Point3f(6.70f, 0.08f, -1.47f);
				direction = Math.PI / 2;
			} else if (rand == 3) {
				nodeList = createNodeGroup7();
				start = new Point3f(6.70f, 0.08f, -1.43f);
				direction = Math.PI / 2;
			}

			if (carType == 0){
				car = new Track(militime, nodeList, start, direction, startSpeed);
			}else {
				car = new PassengerCar(militime, nodeList, start, direction, startSpeed);
			}

			car.setNodegroupIndex(rand + 4);

			tempBranchGroup.addChild(car.carObjectGroup);
			carBranchGroup.addChild(tempBranchGroup);

			car.setExpSheet(sheet1);
			car.setSheetIndex(rowIndex);
			rowIndex++;
		}

	}

	class LampTimer extends TimerTask{

		@Override
		public void run() {
			BranchGroup tempBranchGroup = new BranchGroup();
			Car car;

			int rand = (int) (Math.random() * 4);
			int carType = (int) (Math.random() * 4);
			int startIndex = carLists.get(rand).getCarSize() - 1;
			int nodeGroupIndex = 0;
			double startSpeed;

			if (startIndex >= 0) startSpeed = carLists.get(rand).getCar(startIndex).getSpeed();
			else startSpeed = rand >= 2 && rand <= 5 ? 27.77778 : 11.11111;

			NodeList nodeList = null;
			Point3f start = null;
			double direction = 0;

			if (rand == 0) {
				nodeList = createNodeGroup2();
				start = new Point3f(0, 0.08f, 2.5f);
				direction = Math.PI;
				nodeGroupIndex = 2;
			} else if (rand == 1) {
				nodeList = createNodeGroup3();
				start = new Point3f(-5.80f, 0.08f, -1.59f);
				direction = Math.PI * 3 / 2;
				nodeGroupIndex = 3;
			} else if (rand == 2) {
				nodeList = createNodeGroup8();
				start = new Point3f(0, 0.08f, 2.5f);
				direction = Math.PI;
				nodeGroupIndex = 8;
			} else if (rand == 3) {
				nodeList = createNodeGroup9();
				start = new Point3f(6.70f, 0.08f, -1.43f);
				direction = Math.PI / 2;
				nodeGroupIndex = 9;
			}

			if (carType == 0){
				car = new Track(militime, nodeList, start, direction, startSpeed);
			}else {
				car = new PassengerCar(militime, nodeList, start, direction, startSpeed);
			}

			car.setNodegroupIndex(nodeGroupIndex);

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
