/* Trafic Simulator ver1.0.0
 * 単位：0.01f = 1m
 */
package traffic;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import cars.Car;
import cars.Track;
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

	Timer mainTimer, lampTimer;

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
					f.stopping();

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
		//setBounds(250, 50, 1650, 1000);   //for notebook
		setBounds(1000, 50, 1600, 1100);   //for desktop
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		//setting java3D
		Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		//canvas.setBounds(0, 0, 1600, 900);   //for notebook
		canvas.setBounds(0, 0, 1500, 1000);   //for desktop
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
		cp.add(change);

		mainTimer = new Timer();
		mainTimer.schedule(new  carTimer(), 2000, 4000);

		lampTimer = new Timer();
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

		Timer newCarTimer = new Timer();
		/*newCarTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				BranchGroup tempBranchGroup = new BranchGroup();

				Car[] cars = new PassengerCar[16];

				cars[0] = new PassengerCar(militime, createNodeGroup1(), new Point3f(0, 0.08f, 10.0f), Math.PI, 80);
				cars[1] = new PassengerCar(militime, createNodeGroup2(), new Point3f(0.04f, 0.08f, 10.0f), Math.PI, 80);
				cars[2] = new PassengerCar(militime, createNodeGroup3(), new Point3f(0.12f, 0.08f, -5.0f), Math.PI, 80);
				cars[3] = new PassengerCar(militime, createNodeGroup4(), new Point3f(0.16f, 0.08f, -5.0f), Math.PI, 80);
				cars[4] = new PassengerCar(militime, createNodeGroup5(), new Point3f(-10.0f, 0, 0), Math.PI, 80);
				cars[5] = new PassengerCar(militime, createNodeGroup6(), new Point3f(-10.0f, 0, 0.04f), Math.PI, 80);
				cars[6] = new PassengerCar(militime, createNodeGroup7(), new Point3f(5.0f, 0, 0.12f), Math.PI, 80);
				cars[7] = new PassengerCar(militime, createNodeGroup8(), new Point3f(5.0f, 0, 0.16f), Math.PI, 80);
				cars[8] = new PassengerCar(militime, createNodeGroup9(), new Point3f(0, 0.08f, 10.0f), Math.PI, 80);
				cars[9] = new PassengerCar(militime, createNodeGroup10(), new Point3f(-10.0f, 0, 0), Math.PI, 80);
				cars[10] = new PassengerCar(militime, createNodeGroup11(), new Point3f(0.16f, 0.08f, -5.0f), Math.PI, 80);
				cars[11] = new PassengerCar(militime, createNodeGroup12(), new Point3f(5.0f, 0, 0.16f), Math.PI, 80);
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				cars[12] = new PassengerCar(militime, createNodeGroup13(), new Point3f(0, 0.08f, 10.0f), Math.PI, 80);
				cars[13] = new PassengerCar(militime, createNodeGroup14(), new Point3f(-10.0f, 0, 0), Math.PI, 80);
				cars[14] = new PassengerCar(militime, createNodeGroup15(), new Point3f(0.16f, 0.08f, -5.0f), Math.PI, 80);
				cars[15] = new PassengerCar(militime, createNodeGroup16(), new Point3f(5.0f, 0, 0.16f), Math.PI, 80);

				for (int i = 0; i < cars.length; i++) {
					cars[i].setNodegroupIndex(i + 1);
					tempBranchGroup.addChild(cars[i].carObjectGroup);
				}

				carBranchGroup.addChild(tempBranchGroup);
			}
		}, 1000, 250);*/

		loading.setVisible(false);
		setVisible(true);
	}

	Workbook workbook = new XSSFWorkbook();
	Sheet sheet1;

		//作るだけ
	private void createExcel() {
		sheet1 = workbook.createSheet();
		workbook.setSheetName(0,"data");

		for (int i = 1; i <= 16; i++) {
			Row row = sheet1.createRow(i - 1);
			Cell num = row.createCell(0);
			Cell ave = row.createCell(1);

			String rowIndex = String.valueOf(i);

			num.setCellValue(String.valueOf(i));
			ave.setCellFormula("AVERAGEIF($D$10:$D$10000,A" + rowIndex + ",$C$10:$C$10000)");
		}
	}

	//ウィンドウ閉じたときに呼ばれる
	private void exportSheet(){
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter dFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
		String folderpath = "C:\\Users\\kouhe\\Documents\\program files\\Java\\traffic\\honban\\TrafficSim_";
		//String folderpath = "C:\\Users\\kohei\\Documents\\Excel\\traffic\\TrafficSim_";
		String filepath = folderpath + dFormatter.format(localDateTime) + ".xlsx";

		for (CarList carList : carLists) {
			for (Car car : carList.cars) {
				car.exit();
			}
		}

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

		cars = new PassengerCar[16];

		cars[0] = new PassengerCar(militime, createNodeGroup1(), new Point3f(0, 0.08f, 10.0f), Math.PI, 27.77778);
		cars[1] = new PassengerCar(militime, createNodeGroup2(), new Point3f(0.04f, 0.08f, 10.0f), Math.PI, 27.77778);
		cars[2] = new PassengerCar(militime, createNodeGroup3(), new Point3f(0.12f, 0.08f, -5.0f), Math.PI, 27.77778);
		cars[3] = new PassengerCar(militime, createNodeGroup4(), new Point3f(0.16f, 0.08f, -5.0f), Math.PI, 27.77778);
		cars[4] = new PassengerCar(militime, createNodeGroup5(), new Point3f(-10.0f, 0, 0), Math.PI, 27.77778);
		cars[5] = new PassengerCar(militime, createNodeGroup6(), new Point3f(-10.0f, 0, 0.04f), Math.PI, 27.77778);
		cars[6] = new PassengerCar(militime, createNodeGroup7(), new Point3f(5.0f, 0, 0.12f), Math.PI, 27.77778);
		cars[7] = new PassengerCar(militime, createNodeGroup8(), new Point3f(5.0f, 0, 0.16f), Math.PI, 27.77778);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cars[8] = new PassengerCar(militime, createNodeGroup9(), new Point3f(0, 0.08f, 10.0f), Math.PI, 27.77778);
		cars[9] = new PassengerCar(militime, createNodeGroup10(), new Point3f(-10.0f, 0, 0), Math.PI, 27.77778);
		cars[10] = new PassengerCar(militime, createNodeGroup11(), new Point3f(0.16f, 0.08f, -5.0f), Math.PI, 27.77778);
		cars[11] = new PassengerCar(militime, createNodeGroup12(), new Point3f(5.0f, 0, 0.16f), Math.PI, 27.77778);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cars[12] = new PassengerCar(militime, createNodeGroup13(), new Point3f(0, 0.08f, 10.0f), Math.PI, 27.77778);
		cars[13] = new PassengerCar(militime, createNodeGroup14(), new Point3f(-10.0f, 0, 0), Math.PI, 27.77778);
		cars[14] = new PassengerCar(militime, createNodeGroup15(), new Point3f(0.16f, 0.08f, -5.0f), Math.PI, 27.77778);
		cars[15] = new PassengerCar(militime, createNodeGroup16(), new Point3f(5.0f, 0, 0.16f), Math.PI, 27.77778);

		for (int i = 0; i < cars.length; i++) {
			cars[i].setNodegroupIndex(i + 1);
			root.addChild(cars[i].carObjectGroup);
		}

		return root;
	}

	private void createCarList() {
		for(int i = 0; i < 18; i++) {
		    CarList car = new CarList();

			carLists.add(car);
			car.setNodegroupIndex(i + 1);
		}

		carLists.get(0).setToChangeLane(createNodeGroup2());
		carLists.get(1).setToChangeLane(createNodeGroup1());
		carLists.get(2).setToChangeLane(createNodeGroup4());
		carLists.get(3).setToChangeLane(createNodeGroup3());
		carLists.get(4).setToChangeLane(createNodeGroup6());
		carLists.get(5).setToChangeLane(createNodeGroup5());
		carLists.get(6).setToChangeLane(createNodeGroup8());
		carLists.get(7).setToChangeLane(createNodeGroup7());

		/*carLists.get(0).setBunkiCarList(carLists.get(6));
		carLists.get(2).setBunkiCarList(carLists.get(1));
		carLists.get(5).setBunkiCarList(carLists.get(7));

		carLists.get(6).setGouryuCarList(carLists.get(5));
		carLists.get(7).setGouryuCarList(carLists.get(1));
		carLists.get(0).setGouryuCarList(carLists.get(2));*/
	}

	private void createRoad(){
		Road[] roads = new Road[4];

		for (int i = 0; i < 4; i++) {
			roads[i] = new Road();
		}

		roads[0].add(carLists.get(0));
		roads[0].add(carLists.get(1));
		roads[1].add(carLists.get(3));
		roads[1].add(carLists.get(2));
		roads[2].add(carLists.get(4));
		roads[2].add(carLists.get(5));
		roads[3].add(carLists.get(7));
		roads[3].add(carLists.get(6));

		carLists.get(0).setRoad(roads[0]);
		carLists.get(1).setRoad(roads[0]);
		carLists.get(2).setRoad(roads[1]);
		carLists.get(3).setRoad(roads[1]);
		carLists.get(4).setRoad(roads[2]);
		carLists.get(5).setRoad(roads[2]);
		carLists.get(6).setRoad(roads[3]);
		carLists.get(7).setRoad(roads[3]);
	}

	private final double CHANGELANE_ANGLE = Math.asin(10.0 / 26.0);

	private NodeList createNodeGroup1(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, 10.0f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, 7.5f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, 5.0f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, 2.5f), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, 0.0f), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, -2.5f), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(0));
		}

		nodeGroup.setCarList(carLists.get(0));

		return nodeGroup;
	}

	private NodeList createNodeGroup2(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, Math.PI, new Point3f(0.04f, 0.08f, 10.0f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI, new Point3f(0.04f, 0.08f, 7.5f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI, new Point3f(0.04f, 0.08f, 5.0f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI, new Point3f(0.04f, 0.08f, 2.5f), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI, new Point3f(0.04f, 0.08f, 0.0f), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI, new Point3f(0.04f, 0.08f, -2.5f), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(1));
		}

		nodeGroup.setCarList(carLists.get(1));

		return nodeGroup;
	}

	private NodeList createNodeGroup3(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, -5.0f), 0);
		nodes[1] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, -2.5f), 0);
		nodes[2] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, 0.0f), 0);
		nodes[3] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, 2.5f), 0);
		nodes[4] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, 5.0f), 0);
		nodes[5] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, 7.5f), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(2));
		}

		nodeGroup.setCarList(carLists.get(2));

		return nodeGroup;
	}

	private NodeList createNodeGroup4(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, -5.0f), 0);
		nodes[1] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, -2.5f), 0);
		nodes[2] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, 0.0f), 0);
		nodes[3] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, 2.5f), 0);
		nodes[4] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, 5.0f), 0);
		nodes[5] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, 7.5f), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(3));
		}

		nodeGroup.setCarList(carLists.get(3));

		return nodeGroup;
	}

	private NodeList createNodeGroup5(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-10.0f, 0, 0), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-7.5f, 0, 0), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-5.0f, 0, 0), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-2.5f, 0, 0), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(0.0f, 0, 0), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(2.5f, 0, 0), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(4));
		}

		nodeGroup.setCarList(carLists.get(4));

		return nodeGroup;
	}

	private NodeList createNodeGroup6(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-10.0f, 0, 0.04f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-7.5f, 0, 0.04f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-5.0f, 0, 0.04f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-2.5f, 0, 0.044f), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(0.0f, 0, 0.04f), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(2.5f, 0, 0.04f), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(5));
		}

		nodeGroup.setCarList(carLists.get(5));

		return nodeGroup;
	}

	private NodeList createNodeGroup7(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, Math.PI / 2, new Point3f(5.0f, 0, 0.12f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI / 2, new Point3f(2.5f, 0, 0.12f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI / 2, new Point3f(0.0f, 0, 0.12f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-2.5f, 0, 0.12f), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-5.0f, 0, 0.12f), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-7.5f, 0, 0.12f), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(6));
		}

		nodeGroup.setCarList(carLists.get(6));

		return nodeGroup;
	}

	private NodeList createNodeGroup8(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, Math.PI / 2, new Point3f(5.0f, 0, 0.16f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI / 2, new Point3f(2.5f, 0, 0.16f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI / 2, new Point3f(0.0f, 0, 0.16f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-2.5f, 0, 0.16f), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-5.0f, 0, 0.16f), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-7.5f, 0, 0.16f), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(7));
		}

		nodeGroup.setCarList(carLists.get(7));

		return nodeGroup;
	}

	private NodeList createNodeGroup9(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[13];

		nodes[0] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, 10.0f), 0);
		nodes[1] = new StraightNode(0.85f, Math.PI, new Point3f(0, 0.08f, 7.5f), 0);
		nodes[2] = new CurveNode(-0.52f, Math.PI, -CHANGELANE_ANGLE, new Point3f(-0.52f, 0, 6.65f), 0, 0.08f);
		nodes[3] = new CurveNode(0.52f, Math.PI - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(0.44f, 0, 6.25f), 0, 0.08f);
		nodes[4] = new StraightNode(1.80f, Math.PI, new Point3f(-0.08f, 0.08f, 6.25f), 0);
		nodes[5] = new CurveNode(-1.35f, Math.PI, -Math.PI / 2, new Point3f(-1.43f, 0, 4.45f), -0.08f, 0.08f);
		nodes[6] = new StraightNode(1.50f, Math.PI / 2, new Point3f(-1.43f, 0, 3.10f), 0);
		nodes[7] = new CurveNode(1.51f, Math.PI / 2, Math.PI / 2, new Point3f(-2.93f, 0, 1.59f), 0, 0);
		nodes[8] = new CurveNode(-1.35f, Math.PI, -Math.PI / 2, new Point3f(-5.79f, 0, 1.59f), 0, 0);
		nodes[9] = new StraightNode(1.80f, Math.PI / 2, new Point3f(-5.79f, 0, 0.24f), 0);
		nodes[10] = new CurveNode(0.52f, Math.PI / 2, CHANGELANE_ANGLE, new Point3f(-7.59f, 0, -0.28f), 0, 0);
		nodes[11] = new CurveNode(-0.52f, Math.PI / 2 + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(-7.99f, 0, 0.68f), 0, 0);
		nodes[12] = new StraightNode(2.5f - 0.49f, Math.PI / 2, new Point3f(-7.99f, 0, 0.16f), 0);

		nodes[2].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[3].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[10].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[11].setType(NodeType.CHANGE_LANE_SECOND);

		nodes[0].setNowOnCars(carLists.get(0));
		nodes[1].setNowOnCars(carLists.get(0));
		nodes[2].setNowOnCars(carLists.get(13));
		nodes[3].setNowOnCars(carLists.get(13));
		nodes[4].setNowOnCars(carLists.get(13));
		nodes[5].setNowOnCars(carLists.get(13));
		nodes[6].setNowOnCars(carLists.get(17));
		nodes[7].setNowOnCars(carLists.get(17));
		nodes[8].setNowOnCars(carLists.get(8));
		nodes[9].setNowOnCars(carLists.get(8));
		nodes[10].setNowOnCars(carLists.get(8));
		nodes[11].setNowOnCars(carLists.get(8));
		nodes[12].setNowOnCars(carLists.get(7));

		nodes[1].setNextNodeCarListChange(true);
		nodes[1].setChangeType(NodeChangeType.BUNKI);
		nodes[5].setNextNodeCarListChange(true);
		nodes[5].setChangeType(NodeChangeType.BUNKI);
		nodes[7].setNextNodeCarListChange(true);
		nodes[7].setChangeType(NodeChangeType.BUNKI);
		nodes[11].setNextNodeCarListChange(true);
		nodes[11].setChangeType(NodeChangeType.GORYU);
		nodes[11].setNextListMarge(1299);

		nodeGroup.addAll(Arrays.asList(nodes));

		return nodeGroup;
	}

	private NodeList createNodeGroup10(){
		NodeList nodeGroup = new NodeList();
		Node[] nodes = new Node[19];

		nodes[0] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-10.0f, 0, 0), 0);
		nodes[1] = new StraightNode(1.1f, Math.PI * 3 / 2, new Point3f(-7.5f, 0, 0), 0);
		nodes[2] = new CurveNode(-0.52f, Math.PI * 3 / 2, -CHANGELANE_ANGLE, new Point3f(-6.40f, 0, -0.52f), 0, 0);
		nodes[3] = new CurveNode(0.52f, Math.PI * 3 / 2 - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-6.00f, 0, 0.44f), 0, 0);
		nodes[4] = new StraightNode(2.07f, Math.PI * 3 / 2, new Point3f(-6.00f, 0, -0.08f), 0);
		nodes[5] = new CurveNode(-0.53f, Math.PI * 3 / 2, -Math.PI / 2, new Point3f(-3.93f, 0, -0.61f), 0, 0);
		nodes[6] = new CurveNode(-0.46f, Math.PI, -Math.PI, new Point3f(-3.86f, 0, -0.61f), 0.08f, 0);
		nodes[7] = new StraightNode(2.20f, 0, new Point3f(-4.32f, 0.08f, -0.61f), 0);
		nodes[8] = new CurveNode(-1.39f, 0, -Math.PI / 2, new Point3f(-2.93f, 0, 1.59f), -0.08f, 0.08f);
		nodes[9] = new StraightNode(1.00f, Math.PI * 3 / 2, new Point3f(-2.93f, 0,  2.98f), 0);
		nodes[10] = new CurveNode(-0.26f, Math.PI * 3 / 2, -CHANGELANE_ANGLE, new Point3f(-1.93f, 0, 2.72f), 0, 0);
		nodes[11] = new CurveNode(0.26f, Math.PI * 3 / 2 - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-1.73f, 0, 3.20f), 0, 0);
		nodes[12] = new StraightNode(0.30f, Math.PI * 3 / 2, new Point3f(-1.73f, 0,  2.94f), 0);
		nodes[13] = new CurveNode(-1.35f, Math.PI * 3 / 2, -Math.PI / 2, new Point3f(-1.43f, 0, 1.59f), 0.08f, 0);
		nodes[14] = new StraightNode(1.80f, Math.PI, new Point3f(-0.08f, 0.08f, 1.59f), 0);
		nodes[15] = new CurveNode(0.52f, Math.PI, CHANGELANE_ANGLE, new Point3f(0.44f, 0, -0.21f), 0, 0.08f);
		nodes[16] = new CurveNode(-0.52f, Math.PI  + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(-0.52f, 0, -0.61f), 0, 0.08f);
		nodes[17] = new StraightNode(2.5f - 0.61f, Math.PI, new Point3f(0, 0.08f, -0.61f), 0);
		nodes[18] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, -2.5f), 0);


		nodes[0].setNowOnCars(carLists.get(4));
		nodes[1].setNowOnCars(carLists.get(4));
		nodes[2].setNowOnCars(carLists.get(10));
		nodes[3].setNowOnCars(carLists.get(10));
		nodes[4].setNowOnCars(carLists.get(10));
		nodes[5].setNowOnCars(carLists.get(10));
		nodes[6].setNowOnCars(carLists.get(10));
		nodes[7].setNowOnCars(carLists.get(10));
		nodes[8].setNowOnCars(carLists.get(16));
		nodes[9].setNowOnCars(carLists.get(16));
		nodes[10].setNowOnCars(carLists.get(16));
		nodes[11].setNowOnCars(carLists.get(16));
		nodes[12].setNowOnCars(carLists.get(16));
		nodes[13].setNowOnCars(carLists.get(12));
		nodes[14].setNowOnCars(carLists.get(12));
		nodes[15].setNowOnCars(carLists.get(12));
		nodes[16].setNowOnCars(carLists.get(12));
		nodes[17].setNowOnCars(carLists.get(0));
		nodes[18].setNowOnCars(carLists.get(0));

		nodes[1].setNextNodeCarListChange(true);
		nodes[1].setChangeType(NodeChangeType.BUNKI);
		nodes[7].setNextNodeCarListChange(true);
		nodes[7].setChangeType(NodeChangeType.BUNKI);
		nodes[12].setNextNodeCarListChange(true);
		nodes[12].setChangeType(NodeChangeType.BUNKI);
		nodes[16].setNextNodeCarListChange(true);
		nodes[16].setChangeType(NodeChangeType.GORYU);
		nodes[16].setNextListMarge(1061);

		nodeGroup.addAll(Arrays.asList(nodes));

		return nodeGroup;
	}

	private NodeList createNodeGroup11(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[20];

		nodes[0] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, -5.0f), 0);
		nodes[1] = new StraightNode((float) (2.1 + 0.52 - (1.38f + 0.65f + 0.54f) * Math.sin(Math.PI / 4)), 0, new Point3f(0.16f, 0.08f, -2.5f), 0);
		nodes[2] = new CurveNode(-0.52f, 0, -CHANGELANE_ANGLE, new Point3f(0.68f, 0, (float) (0.52 - 0.40 - (1.38f + 0.65f + 0.54f) * Math.sin(Math.PI / 4))), 0, 0.08f);
		double v1 = 0.52 - (1.38f + 0.65f + 0.54f) * Math.sin(Math.PI / 4);
		nodes[3] = new CurveNode(0.52f, -CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-0.28f, 0, (float) v1), 0, 0.08f);
		nodes[4] = new StraightNode(2.00f, 0, new Point3f(0.24f, 0.08f, (float) v1), 0);
		nodes[5] = new CurveNode(-1.38f, 0, -Math.PI / 4, new Point3f(1.62f, 0, (float) (2.52 - (1.38f + 0.65f + 0.54f) * Math.sin(Math.PI / 4))), 0, 0.08f);
		nodes[6] = new StraightNode(0.65f, Math.PI * 7 / 4,
				new Point3f((float) (0.24 + 1.38 * (1 - Math.cos(Math.PI / 4))), 0.08f, (float) (2.52 - (0.65 + 0.54) * Math.sin(Math.PI / 4))), -0.02f);
		nodes[7] = new CurveNode(0.54f, Math.PI * 7 / 4, Math.PI * 3 / 4, new Point3f(0.77f, 0, 2.52f), -0.06f, 0.06f);
		nodes[8] = new StraightNode(2.20f, Math.PI / 2, new Point3f(0.77f, 0, 3.06f), 0);
		nodes[9] = new StraightNode(1.50f, Math.PI / 2, new Point3f(-1.43f, 0, 3.06f), 0);
		nodes[10] = new CurveNode(1.47f, Math.PI / 2, Math.PI / 2, new Point3f(-2.93f, 0, 1.59f), 0.08f, 0);
		nodes[11] = new StraightNode(2.20f, Math.PI, new Point3f(-4.40f, 0.08f, 1.59f), 0);
		nodes[12] = new CurveNode(0.54f, Math.PI, Math.PI * 3 / 4, new Point3f(-3.86f, 0, -0.61f), -0.06f, 0.08f);
		nodes[13] = new StraightNode(0.65f, Math.PI * 7 / 4,
				new Point3f((float) (-3.86 + 0.54 * Math.sin(Math.PI / 4)), 0.02f, (float) (-1.15 + 0.54 * (1 - Math.cos(Math.PI / 4)))), -0.02f);
		double v = -3.86f + (1.38 + 0.65 + 0.54) * Math.sin(Math.PI / 4);
		nodes[14] = new CurveNode(-1.38f, Math.PI * 7 / 4, -Math.PI / 4,
				new Point3f((float) v, 0, -1.46f), 0, 0);
		nodes[15] = new StraightNode(2.00f, Math.PI * 3 / 2, new Point3f((float) (v), 0, -0.08f), 0);
		nodes[16] = new CurveNode(0.52f, Math.PI * 3 / 2, CHANGELANE_ANGLE, new Point3f((float) (v + 2.00), 0, 0.44f), 0, 0);
		nodes[17] = new CurveNode(-0.52f, Math.PI * 3 / 2 + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f((float) (v + 2.40), 0, -0.52f), 0, 0);
		nodes[18] = new StraightNode((float) (2.5 - (v + 2.40)), Math.PI * 3 / 2, new Point3f((float) (v + 2.40), 0, 0), 0);
		nodes[19] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(2.5f, 0, 0), 0);


		nodes[0].setNowOnCars(carLists.get(3));
		nodes[1].setNowOnCars(carLists.get(3));
		nodes[2].setNowOnCars(carLists.get(15));
		nodes[3].setNowOnCars(carLists.get(15));
		nodes[4].setNowOnCars(carLists.get(15));
		nodes[5].setNowOnCars(carLists.get(15));
		nodes[6].setNowOnCars(carLists.get(15));
		nodes[7].setNowOnCars(carLists.get(15));
		nodes[8].setNowOnCars(carLists.get(15));
		nodes[9].setNowOnCars(carLists.get(17));
		nodes[10].setNowOnCars(carLists.get(17));
		nodes[11].setNowOnCars(carLists.get(11));
		nodes[12].setNowOnCars(carLists.get(11));
		nodes[13].setNowOnCars(carLists.get(11));
		nodes[14].setNowOnCars(carLists.get(11));
		nodes[15].setNowOnCars(carLists.get(11));
		nodes[16].setNowOnCars(carLists.get(11));
		nodes[17].setNowOnCars(carLists.get(11));
		nodes[18].setNowOnCars(carLists.get(4));
		nodes[19].setNowOnCars(carLists.get(4));

		nodes[1].setNextNodeCarListChange(true);
		nodes[1].setChangeType(NodeChangeType.BUNKI);
		nodes[8].setNextNodeCarListChange(true);
		nodes[8].setChangeType(NodeChangeType.BUNKI);
		nodes[10].setNextNodeCarListChange(true);
		nodes[10].setChangeType(NodeChangeType.BUNKI);
		nodes[17].setNextNodeCarListChange(true);
		nodes[17].setChangeType(NodeChangeType.GORYU);
		nodes[17].setNextListMarge((float) (1000 + 100 * (v + 2.40)));

		nodeGroup.addAll(Arrays.asList(nodes));


		return nodeGroup;
	}

	private NodeList createNodeGroup12(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[20];

		nodes[0] = new StraightNode(2.5f, Math.PI / 2, new Point3f(5.0f, 0, 0.16f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI / 2, new Point3f(2.5f, 0, 0.16f), 0);
		nodes[2] = new StraightNode(0.73f, Math.PI / 2, new Point3f(0.0f, 0, 0.16f), 0);
		nodes[3] = new CurveNode(-0.52f, Math.PI / 2, -CHANGELANE_ANGLE, new Point3f(-0.73f, 0, 0.68f), 0, 0);
		nodes[4] = new CurveNode(0.52f, Math.PI / 2 - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-1.13f, 0, -0.28f), 0, 0);
		nodes[5] = new StraightNode(1.80f, Math.PI / 2, new Point3f(-1.13f, 0, 0.24f), 0);
		nodes[6] = new CurveNode(-1.35f, Math.PI / 2, -Math.PI / 2, new Point3f(-2.93f, 0, 1.59f), 0, 0);
		nodes[7] = new CurveNode(-1.35f, 0, -Math.PI / 2, new Point3f(-2.93f, 0, 1.59f), 0, 0);
		nodes[8] = new StraightNode(0.30f, Math.PI * 3 / 2, new Point3f(-2.93f, 0, 2.94f), 0);
		nodes[9] = new CurveNode(0.26f, Math.PI * 3 / 2, CHANGELANE_ANGLE, new Point3f(-2.63f, 0, 3.20f), 0, 0);
		nodes[10] = new CurveNode(-0.26f, Math.PI * 3 / 2 + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(-2.43f, 0, 2.72f), 0, 0);
		nodes[11] = new StraightNode(1.00f, Math.PI * 3 / 2, new Point3f(-2.43f, 0, 2.98f), 0);
		nodes[12] = new StraightNode(2.20f, Math.PI * 3 / 2, new Point3f(-1.43f, 0, 2.98f), 0);
		nodes[13] = new CurveNode(-0.46f, Math.PI * 3 / 2, -Math.PI, new Point3f(0.77f, 0, 2.52f), 0.08f, 0);
		nodes[14] = new CurveNode(-0.53f, Math.PI / 2, -Math.PI / 2, new Point3f(0.77f, 0, 2.59f), 0, 0.08f);
		nodes[15] = new StraightNode(2.07f, 0, new Point3f(0.24f, 0.08f, 2.59f), 0);
		nodes[16] = new CurveNode(0.52f, 0, CHANGELANE_ANGLE, new Point3f(-0.28f, 0, 4.66f), 0, 0.08f);
		nodes[17] = new CurveNode(-0.52f, CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(0.68f, 0, 5.06f), 0, 0.08f);
		nodes[18] = new StraightNode(2.44f, 0, new Point3f(0.16f, 0.08f, 5.06f), 0);
		nodes[19] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, 7.5f), 0);


		nodes[0].setNowOnCars(carLists.get(7));
		nodes[1].setNowOnCars(carLists.get(7));
		nodes[2].setNowOnCars(carLists.get(7));
		nodes[3].setNowOnCars(carLists.get(9));
		nodes[4].setNowOnCars(carLists.get(9));
		nodes[5].setNowOnCars(carLists.get(9));
		nodes[6].setNowOnCars(carLists.get(9));
		nodes[7].setNowOnCars(carLists.get(16));
		nodes[8].setNowOnCars(carLists.get(16));
		nodes[9].setNowOnCars(carLists.get(16));
		nodes[10].setNowOnCars(carLists.get(16));
		nodes[11].setNowOnCars(carLists.get(16));
		nodes[12].setNowOnCars(carLists.get(14));
		nodes[13].setNowOnCars(carLists.get(14));
		nodes[14].setNowOnCars(carLists.get(14));
		nodes[15].setNowOnCars(carLists.get(14));
		nodes[16].setNowOnCars(carLists.get(14));
		nodes[17].setNowOnCars(carLists.get(14));
		nodes[18].setNowOnCars(carLists.get(3));
		nodes[19].setNowOnCars(carLists.get(3));

		nodes[2].setNextNodeCarListChange(true);
		nodes[2].setChangeType(NodeChangeType.BUNKI);
		nodes[6].setNextNodeCarListChange(true);
		nodes[6].setChangeType(NodeChangeType.BUNKI);
		nodes[11].setNextNodeCarListChange(true);
		nodes[11].setChangeType(NodeChangeType.BUNKI);
		nodes[17].setNextNodeCarListChange(true);
		nodes[17].setChangeType(NodeChangeType.GORYU);
		nodes[17].setNextListMarge(1006);

		nodeGroup.addAll(Arrays.asList(nodes));

		return nodeGroup;
	}

	private NodeList createNodeGroup13(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[20];

		nodes[0] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, 10.0f), 0);
		nodes[1] = new StraightNode(0.85f, Math.PI, new Point3f(0, 0.08f, 7.5f), 0);
		nodes[2] = new CurveNode(-0.52f, Math.PI, -CHANGELANE_ANGLE, new Point3f(-0.52f, 0, 6.65f), 0, 0.08f);
		nodes[3] = new CurveNode(0.52f, Math.PI - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(0.44f, 0, 6.25f), 0, 0.08f);
		nodes[4] = new StraightNode(1.80f, Math.PI, new Point3f(-0.08f, 0.08f, 6.25f), 0);
		nodes[5] = new CurveNode(-1.35f, Math.PI, -Math.PI / 2, new Point3f(-1.43f, 0, 4.45f), -0.08f, 0.08f);
		nodes[6] = new StraightNode(0.30f, Math.PI / 2, new Point3f(-1.43f, 0, 3.10f), 0);
		nodes[7] = new CurveNode(0.26f, Math.PI / 2, CHANGELANE_ANGLE, new Point3f(-1.73f, 0, 2.84f), 0, 0);
		nodes[8] = new CurveNode(-0.26f, Math.PI / 2 + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(-1.93f, 0, 3.32f), 0, 0);
		nodes[9] = new StraightNode(1.00f, Math.PI / 2, new Point3f(-1.93f, 0, 3.06f), 0);
		nodes[10] = new CurveNode(1.47f, Math.PI / 2, Math.PI / 2, new Point3f(-2.93f, 0, 1.59f), 0.08f, 0);
		nodes[11] = new StraightNode(2.20f, Math.PI, new Point3f(-4.40f, 0.08f, 1.59f), 0);
		nodes[12] = new CurveNode(0.54f, Math.PI, Math.PI * 3 / 4, new Point3f(-3.86f, 0, -0.61f), -0.06f, 0.08f);
		nodes[13] = new StraightNode(0.65f, Math.PI * 7 / 4,
				new Point3f((float) (-3.86 + 0.54 * Math.sin(Math.PI / 4)), 0.02f, (float) (-1.15 + 0.54 * (1 - Math.cos(Math.PI / 4)))), -0.02f);
		double v = -3.86f + (1.38 + 0.65 + 0.54) * Math.sin(Math.PI / 4);
		nodes[14] = new CurveNode(-1.38f, Math.PI * 7 / 4, -Math.PI / 4,
				new Point3f((float) v, 0, -1.46f), 0, 0);
		nodes[15] = new StraightNode(2.00f, Math.PI * 3 / 2, new Point3f((float) (v), 0, -0.08f), 0);
		nodes[16] = new CurveNode(0.52f, Math.PI * 3 / 2, CHANGELANE_ANGLE, new Point3f((float) (v + 2.00), 0, 0.44f), 0, 0);
		nodes[17] = new CurveNode(-0.52f, Math.PI * 3 / 2 + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f((float) (v + 2.40), 0, -0.52f), 0, 0);
		nodes[18] = new StraightNode((float) (2.5 - (v + 2.40)), Math.PI * 3 / 2, new Point3f((float) (v + 2.40), 0, 0), 0);
		nodes[19] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(2.5f, 0, 0), 0);


		nodes[0].setNowOnCars(carLists.get(0));
		nodes[1].setNowOnCars(carLists.get(0));
		nodes[2].setNowOnCars(carLists.get(13));
		nodes[3].setNowOnCars(carLists.get(13));
		nodes[4].setNowOnCars(carLists.get(13));
		nodes[5].setNowOnCars(carLists.get(13));
		nodes[6].setNowOnCars(carLists.get(17));
		nodes[7].setNowOnCars(carLists.get(17));
		nodes[8].setNowOnCars(carLists.get(17));
		nodes[9].setNowOnCars(carLists.get(17));
		nodes[10].setNowOnCars(carLists.get(17));
		nodes[11].setNowOnCars(carLists.get(11));
		nodes[12].setNowOnCars(carLists.get(11));
		nodes[13].setNowOnCars(carLists.get(11));
		nodes[14].setNowOnCars(carLists.get(11));
		nodes[15].setNowOnCars(carLists.get(11));
		nodes[16].setNowOnCars(carLists.get(11));
		nodes[17].setNowOnCars(carLists.get(11));
		nodes[18].setNowOnCars(carLists.get(4));
		nodes[19].setNowOnCars(carLists.get(4));

		nodes[1].setNextNodeCarListChange(true);
		nodes[1].setChangeType(NodeChangeType.BUNKI);
		nodes[5].setNextNodeCarListChange(true);
		nodes[5].setChangeType(NodeChangeType.BUNKI);
		nodes[10].setNextNodeCarListChange(true);
		nodes[10].setChangeType(NodeChangeType.BUNKI);
		nodes[17].setNextNodeCarListChange(true);
		nodes[17].setChangeType(NodeChangeType.GORYU);
		nodes[17].setNextListMarge((float) (1000 + 100 * (v + 2.40)));

		nodeGroup.addAll(Arrays.asList(nodes));

		return nodeGroup;
	}

	private NodeList createNodeGroup14(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[18];

		nodes[0] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-10.0f, 0, 0), 0);
		nodes[1] = new StraightNode(1.1f, Math.PI * 3 / 2, new Point3f(-7.5f, 0, 0), 0);
		nodes[2] = new CurveNode(-0.52f, Math.PI * 3 / 2, -CHANGELANE_ANGLE, new Point3f(-6.40f, 0, -0.52f), 0, 0);
		nodes[3] = new CurveNode(0.52f, Math.PI * 3 / 2 - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-6.00f, 0, 0.44f), 0, 0);
		nodes[4] = new StraightNode(2.07f, Math.PI * 3 / 2, new Point3f(-6.00f, 0, -0.08f), 0);
		nodes[5] = new CurveNode(-0.53f, Math.PI * 3 / 2, -Math.PI / 2, new Point3f(-3.93f, 0, -0.61f), 0, 0);
		nodes[6] = new CurveNode(-0.46f, Math.PI, -Math.PI, new Point3f(-3.86f, 0, -0.61f), 0.08f, 0);
		nodes[7] = new StraightNode(2.20f, 0, new Point3f(-4.32f, 0.08f, -0.61f), 0);
		nodes[8] = new CurveNode(-1.39f, 0, -Math.PI / 2, new Point3f(-2.93f, 0, 1.59f), -0.08f, 0.08f);
		nodes[9] = new StraightNode(1.50f, Math.PI * 3 / 2, new Point3f(-2.93f, 0, 2.98f), 0);
		nodes[10] = new StraightNode(2.20f, Math.PI * 3 / 2, new Point3f(-1.43f, 0, 2.98f), 0);
		nodes[11] = new CurveNode(-0.46f, Math.PI * 3 / 2, -Math.PI, new Point3f(0.77f, 0, 2.52f), 0.08f, 0);
		nodes[12] = new CurveNode(-0.53f, Math.PI / 2, -Math.PI / 2, new Point3f(0.77f, 0, 2.59f), 0, 0.08f);
		nodes[13] = new StraightNode(2.07f, 0, new Point3f(0.24f, 0.08f, 2.59f), 0);
		nodes[14] = new CurveNode(0.52f, 0, CHANGELANE_ANGLE, new Point3f(-0.28f, 0, 4.66f), 0, 0.08f);
		nodes[15] = new CurveNode(-0.52f, CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(0.68f, 0, 5.06f), 0, 0.08f);
		nodes[16] = new StraightNode(2.44f, 0, new Point3f(0.16f, 0.08f, 5.06f), 0);
		nodes[17] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, 7.5f), 0);


		nodes[0].setNowOnCars(carLists.get(4));
		nodes[1].setNowOnCars(carLists.get(4));
		nodes[2].setNowOnCars(carLists.get(10));
		nodes[3].setNowOnCars(carLists.get(10));
		nodes[4].setNowOnCars(carLists.get(10));
		nodes[5].setNowOnCars(carLists.get(10));
		nodes[6].setNowOnCars(carLists.get(10));
		nodes[7].setNowOnCars(carLists.get(10));
		nodes[8].setNowOnCars(carLists.get(16));
		nodes[9].setNowOnCars(carLists.get(16));
		nodes[10].setNowOnCars(carLists.get(14));
		nodes[11].setNowOnCars(carLists.get(14));
		nodes[12].setNowOnCars(carLists.get(14));
		nodes[13].setNowOnCars(carLists.get(14));
		nodes[14].setNowOnCars(carLists.get(14));
		nodes[15].setNowOnCars(carLists.get(14));
		nodes[16].setNowOnCars(carLists.get(3));
		nodes[17].setNowOnCars(carLists.get(3));

		nodes[1].setNextNodeCarListChange(true);
		nodes[1].setChangeType(NodeChangeType.BUNKI);
		nodes[7].setNextNodeCarListChange(true);
		nodes[7].setChangeType(NodeChangeType.BUNKI);
		nodes[9].setNextNodeCarListChange(true);
		nodes[9].setChangeType(NodeChangeType.BUNKI);
		nodes[15].setNextNodeCarListChange(true);
		nodes[15].setChangeType(NodeChangeType.GORYU);
		nodes[15].setNextListMarge(1006);

		nodeGroup.addAll(Arrays.asList(nodes));

		return nodeGroup;
	}

	private NodeList createNodeGroup15(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[19];

		nodes[0] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, -5.0f), 0);
		nodes[1] = new StraightNode((float) (2.1 + 0.52 - (1.38f + 0.65f + 0.54f) * Math.sin(Math.PI / 4)), 0, new Point3f(0.16f, 0.08f, -2.5f), 0);
		nodes[2] = new CurveNode(-0.52f, 0, -CHANGELANE_ANGLE, new Point3f(0.68f, 0, (float) (0.52 - 0.40 - (1.38f + 0.65f + 0.54f) * Math.sin(Math.PI / 4))), 0, 0.08f);
		double v1 = 0.52 - (1.38f + 0.65f + 0.54f) * Math.sin(Math.PI / 4);
		nodes[3] = new CurveNode(0.52f, -CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-0.28f, 0, (float) v1), 0, 0.08f);
		nodes[4] = new StraightNode(2.00f, 0, new Point3f(0.24f, 0.08f, (float) v1), 0);
		nodes[5] = new CurveNode(-1.38f, 0, -Math.PI / 4, new Point3f(1.62f, 0, (float) (2.52 - (1.38f + 0.65f + 0.54f) * Math.sin(Math.PI / 4))), 0, 0.08f);
		nodes[6] = new StraightNode(0.65f, Math.PI * 7 / 4,
				new Point3f((float) (0.24 + 1.38 * (1 - Math.cos(Math.PI / 4))), 0.08f, (float) (2.52 - (0.65 + 0.54) * Math.sin(Math.PI / 4))), -0.02f);
		nodes[7] = new CurveNode(0.54f, Math.PI * 7 / 4, Math.PI * 3 / 4, new Point3f(0.77f, 0, 2.52f), -0.06f, 0.06f);
		nodes[8] = new StraightNode(2.20f, Math.PI / 2, new Point3f(0.77f, 0, 3.06f), 0);
		nodes[9] = new StraightNode(1.00f, Math.PI / 2, new Point3f(-1.43f, 0, 3.06f), 0);
		nodes[10] = new CurveNode(-0.26f, Math.PI / 2, -CHANGELANE_ANGLE, new Point3f(-2.43f, 0, 3.32f), 0, 0);
		nodes[11] = new CurveNode(0.26f, Math.PI / 2 - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-2.63f, 0, 2.84f), 0, 0);
		nodes[12] = new StraightNode(0.30f, Math.PI / 2, new Point3f(-2.63f, 0, 3.10f), 0);
		nodes[13] = new CurveNode(1.51f, Math.PI / 2, Math.PI / 2, new Point3f(-2.93f, 0, 1.59f), 0, 0);
		nodes[14] = new CurveNode(-1.35f, Math.PI, -Math.PI / 2, new Point3f(-5.79f, 0, 1.59f), 0, 0);
		nodes[15] = new StraightNode(1.80f, Math.PI / 2, new Point3f(-5.79f, 0, 0.24f), 0);
		nodes[16] = new CurveNode(0.52f, Math.PI / 2, CHANGELANE_ANGLE, new Point3f(-7.59f, 0, -0.28f), 0, 0);
		nodes[17] = new CurveNode(-0.52f, Math.PI / 2 + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(-7.99f, 0, 0.68f), 0, 0);
		nodes[18] = new StraightNode(2.5f - 0.49f, Math.PI / 2, new Point3f(-7.99f, 0, 0.16f), 0);


		nodes[0].setNowOnCars(carLists.get(3));
		nodes[1].setNowOnCars(carLists.get(3));
		nodes[2].setNowOnCars(carLists.get(15));
		nodes[3].setNowOnCars(carLists.get(15));
		nodes[4].setNowOnCars(carLists.get(15));
		nodes[5].setNowOnCars(carLists.get(15));
		nodes[6].setNowOnCars(carLists.get(15));
		nodes[7].setNowOnCars(carLists.get(15));
		nodes[8].setNowOnCars(carLists.get(15));
		nodes[9].setNowOnCars(carLists.get(17));
		nodes[10].setNowOnCars(carLists.get(17));
		nodes[11].setNowOnCars(carLists.get(17));
		nodes[12].setNowOnCars(carLists.get(17));
		nodes[13].setNowOnCars(carLists.get(17));
		nodes[14].setNowOnCars(carLists.get(8));
		nodes[15].setNowOnCars(carLists.get(8));
		nodes[16].setNowOnCars(carLists.get(8));
		nodes[17].setNowOnCars(carLists.get(8));
		nodes[18].setNowOnCars(carLists.get(7));

		nodes[1].setNextNodeCarListChange(true);
		nodes[1].setChangeType(NodeChangeType.BUNKI);
		nodes[8].setNextNodeCarListChange(true);
		nodes[8].setChangeType(NodeChangeType.BUNKI);
		nodes[13].setNextNodeCarListChange(true);
		nodes[13].setChangeType(NodeChangeType.BUNKI);
		nodes[17].setNextNodeCarListChange(true);
		nodes[17].setChangeType(NodeChangeType.GORYU);
		nodes[17].setNextListMarge(1299);

		nodeGroup.addAll(Arrays.asList(nodes));

		return nodeGroup;
	}

	private NodeList createNodeGroup16(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[15];

		nodes[0] = new StraightNode(2.5f, Math.PI / 2, new Point3f(5.0f, 0, 0.16f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI / 2, new Point3f(2.5f, 0, 0.16f), 0);
		nodes[2] = new StraightNode(0.73f, Math.PI / 2, new Point3f(0.0f, 0, 0.16f), 0);
		nodes[3] = new CurveNode(-0.52f, Math.PI / 2, -CHANGELANE_ANGLE, new Point3f(-0.73f, 0, 0.68f), 0, 0);
		nodes[4] = new CurveNode(0.52f, Math.PI / 2 - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-1.13f, 0, -0.28f), 0, 0);
		nodes[5] = new StraightNode(1.80f, Math.PI / 2, new Point3f(-1.13f, 0, 0.24f), 0);
		nodes[6] = new CurveNode(-1.35f, Math.PI / 2, -Math.PI / 2, new Point3f(-2.93f, 0, 1.59f), 0, 0);
		nodes[7] = new CurveNode(-1.35f, 0, -Math.PI / 2, new Point3f(-2.93f, 0, 1.59f), 0, 0);
		nodes[8] = new StraightNode(1.50f, Math.PI * 3 / 2, new Point3f(-2.93f, 0, 2.94f), 0);
		nodes[9] = new CurveNode(-1.35f, Math.PI * 3 / 2, -Math.PI / 2, new Point3f(-1.43f, 0, 1.59f), 0.08f, 0);
		nodes[10] = new StraightNode(1.80f, Math.PI, new Point3f(-0.08f, 0.08f, 1.59f), 0);
		nodes[11] = new CurveNode(0.52f, Math.PI, CHANGELANE_ANGLE, new Point3f(0.44f, 0, -0.21f), 0, 0.08f);
		nodes[12] = new CurveNode(-0.52f, Math.PI  + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(-0.52f, 0, -0.61f), 0, 0.08f);
		nodes[13] = new StraightNode(2.5f - 0.61f, Math.PI, new Point3f(0, 0.08f, -0.61f), 0);
		nodes[14] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, -2.5f), 0);


		nodes[0].setNowOnCars(carLists.get(7));
		nodes[1].setNowOnCars(carLists.get(7));
		nodes[2].setNowOnCars(carLists.get(7));
		nodes[3].setNowOnCars(carLists.get(9));
		nodes[4].setNowOnCars(carLists.get(9));
		nodes[5].setNowOnCars(carLists.get(9));
		nodes[6].setNowOnCars(carLists.get(9));
		nodes[7].setNowOnCars(carLists.get(16));
		nodes[8].setNowOnCars(carLists.get(16));
		nodes[9].setNowOnCars(carLists.get(12));
		nodes[10].setNowOnCars(carLists.get(12));
		nodes[11].setNowOnCars(carLists.get(12));
		nodes[12].setNowOnCars(carLists.get(12));
		nodes[13].setNowOnCars(carLists.get(0));
		nodes[14].setNowOnCars(carLists.get(0));

		nodes[2].setNextNodeCarListChange(true);
		nodes[2].setChangeType(NodeChangeType.BUNKI);
		nodes[6].setNextNodeCarListChange(true);
		nodes[6].setChangeType(NodeChangeType.BUNKI);
		nodes[8].setNextNodeCarListChange(true);
		nodes[8].setChangeType(NodeChangeType.BUNKI);
		nodes[12].setNextNodeCarListChange(true);
		nodes[12].setChangeType(NodeChangeType.GORYU);
		nodes[12].setNextListMarge(1061);

		nodeGroup.addAll(Arrays.asList(nodes));

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

	public void stopping(){
		mainTimer.cancel();
		lampTimer.cancel();
	}

	int rowIndex = 18;

	/**
	 * 車を生成するクラス mainlane
	 */
	class carTimer extends TimerTask{

		@Override
		public void run() {
			BranchGroup tempBranchGroup = new BranchGroup();
			Car car;

			int rand = (int) (Math.random() * 8);
			int carType = (int) (Math.random() * 4);
			int startIndex = carLists.get(rand).getCarSize() - 1;
			double startSpeed;

			if (startIndex >= 0) startSpeed = carLists.get(rand).getCar(startIndex).getSpeed();
			else startSpeed = 27.77778;

			NodeList nodeList = null;
			Point3f start = null;
			double direction = 0;

			if (rand == 0) {
				nodeList = createNodeGroup1();
				start = new Point3f(0, 0.08f, 10.0f);
				direction = Math.PI;
			} else if (rand == 1) {
				nodeList = createNodeGroup2();
				start = new Point3f(0.04f, 0.08f, 10.0f);
				direction = Math.PI;
			} else if (rand == 2) {
				nodeList = createNodeGroup3();
				start = new Point3f(0.12f, 0.08f, -5.0f);
				direction = 0;
			} else if (rand == 3) {
				nodeList = createNodeGroup4();
				start = new Point3f(0.16f, 0.08f, -5.0f);
				direction = 0;
			} else if (rand == 4) {
				nodeList = createNodeGroup5();
				start = new Point3f(-10.0f, 0, 0);
				direction = Math.PI * 3 / 2;
			} else if (rand == 5) {
				nodeList = createNodeGroup6();
				start = new Point3f(-10.0f, 0, 0.04f);
				direction = Math.PI * 3 / 2;
			} else if (rand == 6) {
				nodeList = createNodeGroup7();
				start = new Point3f(5.0f, 0, 0.12f);
				direction = Math.PI / 2;
			} else if (rand == 7) {
				nodeList = createNodeGroup8();
				start = new Point3f(5.0f, 0, 0.16f);
				direction = Math.PI / 2;
			}

			if (carType == 0){
				car = new Track(militime, nodeList, start, direction, startSpeed);
			}else {
				car = new PassengerCar(militime, nodeList, start, direction, startSpeed);
			}

			car.setNodegroupIndex(rand + 1);

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

			int rand = (int) (Math.random() * 8);
			int carType = (int) (Math.random() * 4);
			int startIndex = carLists.get(rand).getCarSize() - 1;
			double startSpeed;

			if (startIndex >= 0) startSpeed = carLists.get(rand).getCar(startIndex).getSpeed();
			else startSpeed = rand >= 2 && rand <= 5 ? 27.77778 : 11.11111;

			NodeList nodeList = null;
			Point3f start = null;
			double direction = 0;

			if (rand == 0) {
				nodeList = createNodeGroup9();
				start = new Point3f(0, 0.08f, 10.0f);
				direction = Math.PI;
			} else if (rand == 1) {
				nodeList = createNodeGroup10();
				start = new Point3f(-10.0f, 0, 0);
				direction = Math.PI * 3 / 2;
			} else if (rand == 2) {
				nodeList = createNodeGroup11();
				start = new Point3f(0.16f, 0.08f, -5.0f);
				direction = 0;
			} else if (rand == 3) {
				nodeList = createNodeGroup12();
				start = new Point3f(5.0f, 0, 0.16f);
				direction = Math.PI / 2;
			} else if (rand == 4) {
				nodeList = createNodeGroup13();
				start = new Point3f(0, 0.08f, 10.0f);
				direction = Math.PI / 2;
			} else if (rand == 5) {
				nodeList = createNodeGroup14();
				start = new Point3f(-10.0f, 0, 0);
				direction = Math.PI;
			} else if (rand == 6) {
				nodeList = createNodeGroup15();
				start = new Point3f(0.16f, 0.08f, -5.0f);
				direction = Math.PI * 3 / 2;
			} else if (rand == 7) {
				nodeList = createNodeGroup16();
				start = new Point3f(5.0f, 0, 0.16f);
				direction = 0;
			}

			if (carType == 0){
				car = new Track(militime, nodeList, start, direction, startSpeed);
			}else {
				car = new PassengerCar(militime, nodeList, start, direction, startSpeed);
			}

			car.setNodegroupIndex(rand + 9);

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
