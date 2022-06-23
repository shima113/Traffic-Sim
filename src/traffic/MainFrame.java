/* Trafic Simulator ver1.0.0
 * 単位：0.01f = 1m
 */
package traffic;

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
		mainTimer.schedule(new  carTimer(), 2000, 2400);

		Timer lampTimer = new Timer();
		lampTimer.schedule(new LampTimer(), 2700, 2400);

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

				cars[0] = new PassengerCar(militime, createNodeGroup1(), new Point3f(0, 0.08f, 7.5f), Math.PI, 27.77778);
				cars[1] = new PassengerCar(militime, createNodeGroup2(), new Point3f(0.04f, 0.08f, 7.5f), Math.PI, 27.77778);
				cars[2] = new PassengerCar(militime, createNodeGroup3(), new Point3f(0.12f, 0.08f, -7.5f), Math.PI, 27.77778);
				cars[3] = new PassengerCar(militime, createNodeGroup4(), new Point3f(0.16f, 0.08f, -7.5f), Math.PI, 27.77778);
				cars[4] = new PassengerCar(militime, createNodeGroup5(), new Point3f(-7.5f, 0, 0), Math.PI, 27.77778);
				cars[5] = new PassengerCar(militime, createNodeGroup6(), new Point3f(-7.5f, 0, 0.04f), Math.PI, 27.77778);
				cars[6] = new PassengerCar(militime, createNodeGroup7(), new Point3f(7.5f, 0, 0.12f), Math.PI, 27.77778);
				cars[7] = new PassengerCar(militime, createNodeGroup8(), new Point3f(7.5f, 0, 0.16f), Math.PI, 27.77778);
				cars[8] = new PassengerCar(militime, createNodeGroup9(), new Point3f(0, 0.08f, 7.5f), Math.PI, 27.77778);
				cars[9] = new PassengerCar(militime, createNodeGroup10(), new Point3f(-7.5f, 0, 0), Math.PI, 27.77778);
				cars[10] = new PassengerCar(militime, createNodeGroup11(), new Point3f(0.16f, 0.08f, -7.5f), Math.PI, 27.77778);
				cars[11] = new PassengerCar(militime, createNodeGroup12(), new Point3f(7.5f, 0, 0.16f), Math.PI, 27.77778);
				cars[12] = new PassengerCar(militime, createNodeGroup13(), new Point3f(7.5f, 0, 0.16f), Math.PI, 27.77778);
				cars[13] = new PassengerCar(militime, createNodeGroup14(), new Point3f(0, 0.08f, 7.5f), Math.PI, 27.77778);
				cars[14] = new PassengerCar(militime, createNodeGroup15(), new Point3f(-7.5f, 0, 0), Math.PI, 27.77778);
				cars[15] = new PassengerCar(militime, createNodeGroup16(), new Point3f(0.16f, 0.08f, -7.5f), Math.PI, 27.77778);

				for (int i = 0; i < cars.length; i++) {
					cars[i].setNodegroupIndex(i + 1);
					tempBranchGroup.addChild(cars[i].carObjectGroup);
				}

				carBranchGroup.addChild(tempBranchGroup);
			}
		}, 1000, 1000);*/

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
			ave.setCellFormula("AVERAGEIF($D$10:$D$5000,A" + rowIndex + ",$C$10:$C$5000)");
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

		cars = new PassengerCar[16];

		cars[0] = new PassengerCar(militime, createNodeGroup1(), new Point3f(0, 0.08f, 7.5f), Math.PI, 27.77778);
		cars[1] = new PassengerCar(militime, createNodeGroup2(), new Point3f(0.04f, 0.08f, 7.5f), Math.PI, 27.77778);
		cars[2] = new PassengerCar(militime, createNodeGroup3(), new Point3f(0.12f, 0.08f, -7.5f), Math.PI, 27.77778);
		cars[3] = new PassengerCar(militime, createNodeGroup4(), new Point3f(0.16f, 0.08f, -7.5f), Math.PI, 27.77778);
		cars[4] = new PassengerCar(militime, createNodeGroup5(), new Point3f(-7.5f, 0, 0), Math.PI, 27.77778);
		cars[5] = new PassengerCar(militime, createNodeGroup6(), new Point3f(-7.5f, 0, 0.04f), Math.PI, 27.77778);
		cars[6] = new PassengerCar(militime, createNodeGroup7(), new Point3f(7.5f, 0, 0.12f), Math.PI, 27.77778);
		cars[7] = new PassengerCar(militime, createNodeGroup8(), new Point3f(7.5f, 0, 0.16f), Math.PI, 27.77778);
		try {//重ならないため
			Thread.sleep(500);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		cars[8] = new PassengerCar(militime, createNodeGroup9(), new Point3f(0, 0.08f, 7.5f), Math.PI, 27.77778);
		cars[9] = new PassengerCar(militime, createNodeGroup10(), new Point3f(-7.5f, 0, 0), Math.PI, 27.77778);
		cars[10] = new PassengerCar(militime, createNodeGroup11(), new Point3f(0.16f, 0.08f, -7.5f), Math.PI, 27.77778);
		cars[11] = new PassengerCar(militime, createNodeGroup12(), new Point3f(7.5f, 0, 0.16f), Math.PI, 27.77778);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		cars[12] = new PassengerCar(militime, createNodeGroup13(), new Point3f(7.5f, 0, 0.16f), Math.PI, 27.77778);
		cars[13] = new PassengerCar(militime, createNodeGroup14(), new Point3f(0, 0.08f, 7.5f), Math.PI, 27.77778);
		cars[14] = new PassengerCar(militime, createNodeGroup15(), new Point3f(-7.5f, 0, 0), Math.PI, 27.77778);
		cars[15] = new PassengerCar(militime, createNodeGroup16(), new Point3f(0.16f, 0.08f, -7.5f), Math.PI, 27.77778);

		for (int i = 0; i < cars.length; i++) {
			cars[i].setNodegroupIndex(i + 1);
			root.addChild(cars[i].carObjectGroup);
		}

		return root;
	}

	private void createCarList() {
		for(int i = 0; i < 20; i++) {
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

		nodeGroup.setCarList(carLists.get(0));

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
			node.setNowOnCars(carLists.get(1));
		}

		nodeGroup.setCarList(carLists.get(1));

		return nodeGroup;
	}

	private NodeList createNodeGroup3(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[6];

		nodes[0] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, -7.5f), 0);
		nodes[1] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, -5.0f), 0);
		nodes[2] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, -2.5f), 0);
		nodes[3] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, 0.0f), 0);
		nodes[4] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, 2.5f), 0);
		nodes[5] = new StraightNode(2.5f, 0, new Point3f(0.12f, 0.08f, 5.0f), 0);

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

		nodes[0] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, -7.5f), 0);
		nodes[1] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, -5.0f), 0);
		nodes[2] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, -2.5f), 0);
		nodes[3] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, 0.0f), 0);
		nodes[4] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, 2.5f), 0);
		nodes[5] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, 5.0f), 0);

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

		nodes[0] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-7.5f, 0, 0), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-5.0f, 0, 0), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-2.5f, 0, 0), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(0.0f, 0, 0), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(2.5f, 0, 0), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(5.0f, 0, 0), 0);

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

		nodes[0] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-7.5f, 0, 0.04f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-5.0f, 0, 0.04f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-2.5f, 0, 0.04f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(0.0f, 0, 0.044f), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(2.5f, 0, 0.04f), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(5.0f, 0, 0.04f), 0);

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

		nodes[0] = new StraightNode(2.5f, Math.PI / 2, new Point3f(7.5f, 0, 0.12f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI / 2, new Point3f(5.0f, 0, 0.12f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI / 2, new Point3f(2.5f, 0, 0.12f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI / 2, new Point3f(0.0f, 0, 0.12f), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-2.5f, 0, 0.12f), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-5.0f, 0, 0.12f), 0);

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

		nodes[0] = new StraightNode(2.5f, Math.PI / 2, new Point3f(7.5f, 0, 0.16f), 0);
		nodes[1] = new StraightNode(2.5f, Math.PI / 2, new Point3f(5.0f, 0, 0.16f), 0);
		nodes[2] = new StraightNode(2.5f, Math.PI / 2, new Point3f(2.5f, 0, 0.16f), 0);
		nodes[3] = new StraightNode(2.5f, Math.PI / 2, new Point3f(0.0f, 0, 0.16f), 0);
		nodes[4] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-2.5f, 0, 0.16f), 0);
		nodes[5] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-5.0f, 0, 0.16f), 0);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(7));
		}

		nodeGroup.setCarList(carLists.get(7));

		return nodeGroup;
	}

	private final float x = 2.71289321881345f;

	private NodeList createNodeGroup9(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[15];

		nodes[0] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, 7.5f), 0);
		nodes[1] = new StraightNode(0.70f, Math.PI, new Point3f(0, 0.08f, 5.0f), 0);
		nodes[2] = new CurveNode(-0.52f, Math.PI, -CHANGELANE_ANGLE, new Point3f(-0.52f, 0, 4.30f), 0, 0.08f);
		nodes[3] = new CurveNode(0.52f, Math.PI - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(0.44f, 0, 3.90f), 0, 0.08f);
		nodes[4] = new CurveNode(-0.26f, Math.PI, -CHANGELANE_ANGLE, new Point3f(-0.34f, 0, 3.90f), 0, 0.08f);
		nodes[5] = new CurveNode(0.26f, Math.PI - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(0.14f, 0, 3.70f), 0, 0.08f);
		nodes[6] = new CurveNode(-x, Math.PI, -Math.PI / 4, new Point3f(-0.12f - x, 0, 3.70f), 0, 0.08f);
		nodes[7] = new StraightNode(1.00f, Math.PI * 3 / 4,
				new Point3f((float) (-0.12 - x * (1 - Math.cos(Math.PI / 4))), 0.08f, (float) (3.70 - x * Math.sin(Math.PI / 4))), -0.08f);
		nodes[8] = new CurveNode(-x, Math.PI * 3 / 4, -Math.PI / 4, new Point3f(-3.54f, 0, 0.28f + x), 0, 0);
		nodes[9] = new CurveNode(0.26f, Math.PI / 2, CHANGELANE_ANGLE, new Point3f(-3.54f, 0, 0.02f), 0, 0);
		nodes[10] = new CurveNode(-0.26f, Math.PI / 2 + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(-3.74f, 0, 0.50f), 0, 0);
		nodes[11] = new CurveNode(0.52f, Math.PI / 2, CHANGELANE_ANGLE, new Point3f(-3.74f, 0, -0.28f), 0, 0);
		nodes[12] = new CurveNode(-0.52f, Math.PI / 2 + CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-4.14f, 0, 0.68f), 0, 0);
		nodes[13] = new StraightNode(0.86f, Math.PI / 2, new Point3f(-4.14f, 0, 0.16f), 0);
		nodes[14] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-5.0f, 0, 0.16f), 0);

		nodes[2].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[3].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[4].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[5].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[9].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[10].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[11].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[12].setType(NodeType.CHANGE_LANE_SECOND);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(8));
		}

		nodes[0].setNowOnCars(carLists.get(0));
		nodes[1].setNowOnCars(carLists.get(0));
		nodes[13].setNowOnCars(carLists.get(7));
		nodes[14].setNowOnCars(carLists.get(7));

		nodes[1].setNextNodeCarListChange(true);
		nodes[1].setChangeType(NodeChangeType.BUNKI);
		nodes[12].setNextNodeCarListChange(true);
		nodes[12].setChangeType(NodeChangeType.GORYU);
		nodes[12].setNextListMarge(1164);

		nodes[7].setLimitSpeed(16.66667);

		return nodeGroup;
	}

	private NodeList createNodeGroup10(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[15];

		nodes[0] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-7.5f, 0, 0), 0);
		nodes[1] = new StraightNode(0.86f, Math.PI * 3 / 2, new Point3f(-5.0f, 0, 0), 0);
		nodes[2] = new CurveNode(-0.52f, Math.PI * 3 / 2, -CHANGELANE_ANGLE, new Point3f(-4.14f, 0, -0.52f), 0, 0);
		nodes[3] = new CurveNode(0.52f, Math.PI * 3 / 2 - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-3.74f, 0, 0.44f), 0, 0);
		nodes[4] = new CurveNode(-0.26f, Math.PI * 3 / 2, -CHANGELANE_ANGLE, new Point3f(-3.74f, 0, -0.34f), 0, 0);
		nodes[5] = new CurveNode(0.26f, Math.PI * 3 / 2 - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-3.54f, 0, 0.14f), 0, 0);
		nodes[6] = new CurveNode(-x, Math.PI * 3 / 2, -Math.PI / 4, new Point3f(-3.54f, 0, -0.12f - x), 0, 0);
		nodes[7] = new StraightNode(1.00f, Math.PI * 5 / 4,
				new Point3f((float) (-3.54 + x * Math.sin(Math.PI / 4)), 0, (float) (-0.12 - x * (1 - Math.cos(Math.PI / 4)))), 0.08f);
		nodes[8] = new CurveNode(-x, Math.PI * 5 / 4, -Math.PI / 4, new Point3f(-0.12f - x, 0, -3.54f), 0, 0.08f);
		nodes[9] = new CurveNode(0.26f, Math.PI, CHANGELANE_ANGLE, new Point3f(0.14f, 0, -3.54f), 0, 0.08f);
		nodes[10] = new CurveNode(-0.26f, Math.PI + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(-0.34f, 0, -3.74f), 0, 0.08f);
		nodes[11] = new CurveNode(0.52f, Math.PI, CHANGELANE_ANGLE, new Point3f(0.44f, 0, -3.74f), 0, 0.08f);
		nodes[12] = new CurveNode(-0.52f, Math.PI + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(-0.52f, 0, -4.14f), 0, 0.08f);
		nodes[13] = new StraightNode(0.86f, Math.PI, new Point3f(0, 0.08f, -4.14f), 0);
		nodes[14] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, -5.0f), 0);

		nodes[2].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[3].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[4].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[5].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[9].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[10].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[11].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[12].setType(NodeType.CHANGE_LANE_SECOND);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(9));
		}

		nodes[0].setNowOnCars(carLists.get(4));
		nodes[1].setNowOnCars(carLists.get(4));
		nodes[13].setNowOnCars(carLists.get(0));
		nodes[14].setNowOnCars(carLists.get(0));

		nodes[1].setNextNodeCarListChange(true);
		nodes[1].setChangeType(NodeChangeType.BUNKI);
		nodes[12].setNextNodeCarListChange(true);
		nodes[12].setChangeType(NodeChangeType.GORYU);
		nodes[12].setNextListMarge(1164);

		nodes[7].setLimitSpeed(16.66667);

		return nodeGroup;
	}

	private NodeList createNodeGroup11(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[15];

		nodes[0] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, -7.5f), 0);
		nodes[1] = new StraightNode(0.86f, 0, new Point3f(0.16f, 0.08f, -5.0f), 0);
		nodes[2] = new CurveNode(-0.52f, 0, -CHANGELANE_ANGLE, new Point3f(0.68f, 0, -4.14f), 0, 0.08f);
		nodes[3] = new CurveNode(0.52f, -CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-0.28f, 0, -3.74f), 0, 0.08f);
		nodes[4] = new CurveNode(-0.26f, 0, -CHANGELANE_ANGLE, new Point3f(0.50f, 0, -3.74f), 0, 0.08f);
		nodes[5] = new CurveNode(0.26f, -CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(0.02f, 0, -3.54f), 0, 0.08f);
		nodes[6] = new CurveNode(-x, 0, -Math.PI / 4, new Point3f(0.28f + x, 0, -3.54f), 0, 0.08f);
		nodes[7] = new StraightNode(1.00f, Math.PI * 7 / 4,
				new Point3f((float) (0.28 + (x * (1 - Math.cos(Math.PI / 4)))), 0.08f, (float) (-3.54f + x * Math.sin(Math.PI / 4))), -0.08f);
		nodes[8] = new CurveNode(-x, Math.PI * 7 / 4, -Math.PI / 4, new Point3f(3.70f, 0, -0.12f - x), 0, 0);
		nodes[9] = new CurveNode(0.26f, Math.PI * 3 / 2, CHANGELANE_ANGLE, new Point3f(3.70f, 0, 0.14f), 0, 0);
		nodes[10] = new CurveNode(-0.26f, Math.PI * 3 / 2 + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(3.90f, 0, -0.34f), 0, 0);
		nodes[11] = new CurveNode(0.52f, Math.PI * 3 / 2, CHANGELANE_ANGLE, new Point3f(3.90f, 0, 0.44f), 0, 0);
		nodes[12] = new CurveNode(-0.52f, Math.PI * 3 / 2 + CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(4.30f, 0, -0.52f), 0, 0);
		nodes[13] = new StraightNode(0.7f, Math.PI * 3 / 2, new Point3f(4.30f, 0, 0), 0);
		nodes[14] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(5.0f, 0, 0), 0);

		nodes[2].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[3].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[4].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[5].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[9].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[10].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[11].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[12].setType(NodeType.CHANGE_LANE_SECOND);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(10));
		}

		nodes[0].setNowOnCars(carLists.get(3));
		nodes[1].setNowOnCars(carLists.get(3));
		nodes[13].setNowOnCars(carLists.get(4));
		nodes[14].setNowOnCars(carLists.get(4));

		nodes[1].setNextNodeCarListChange(true);
		nodes[1].setChangeType(NodeChangeType.BUNKI);
		nodes[12].setNextNodeCarListChange(true);
		nodes[12].setChangeType(NodeChangeType.GORYU);
		nodes[12].setNextListMarge(1180);

		nodes[7].setLimitSpeed(16.66667);

		return nodeGroup;
	}

	private NodeList createNodeGroup12(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[15];

		nodes[0] = new StraightNode(2.5f, Math.PI / 2, new Point3f(7.5f, 0, 0.16f), 0);
		nodes[1] = new StraightNode(0.7f, Math.PI / 2, new Point3f(5.0f, 0, 0.16f), 0);
		nodes[2] = new CurveNode(-0.52f, Math.PI / 2, -CHANGELANE_ANGLE, new Point3f(4.30f, 0, 0.68f), 0, 0);
		nodes[3] = new CurveNode(0.52f, Math.PI / 2 - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(3.90f, 0, -0.28f), 0, 0);
		nodes[4] = new CurveNode(-0.26f, Math.PI / 2, -CHANGELANE_ANGLE, new Point3f(3.90f, 0, 0.50f), 0, 0);
		nodes[5] = new CurveNode(0.26f, Math.PI / 2 - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(3.70f, 0, 0.02f), 0, 0);
		nodes[6] = new CurveNode(-x, Math.PI / 2, -Math.PI / 4, new Point3f(3.70f, 0, 0.28f + x), 0, 0);
		nodes[7] = new StraightNode(1.00f, Math.PI / 4,
				new Point3f((float) (3.70 - x * Math.sin(Math.PI / 4)), 0, (float) (0.28 + x * (1 - Math.cos(Math.PI / 4)))), 0.08f);
		nodes[8] = new CurveNode(-x, Math.PI / 4, -Math.PI / 4, new Point3f(0.28f + x, 0, 3.70f), 0, 0.08f);
		nodes[9] = new CurveNode(0.26f, 0, CHANGELANE_ANGLE, new Point3f(0.02f, 0, 3.70f), 0, 0.08f);
		nodes[10] = new CurveNode(-0.26f, CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(0.50f, 0, 3.90f), 0, 0.08f);
		nodes[11] = new CurveNode(0.52f, 0, CHANGELANE_ANGLE, new Point3f(-0.28f, 0, 3.90f), 0, 0.08f);
		nodes[12] = new CurveNode(-0.52f, CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(0.68f, 0, 4.30f), 0, 0.08f);
		nodes[13] = new StraightNode(0.7f, 0, new Point3f(0.16f, 0.08f, 4.30f), 0);
		nodes[14] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, 5.0f), 0);

		nodes[2].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[3].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[4].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[5].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[9].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[10].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[11].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[12].setType(NodeType.CHANGE_LANE_SECOND);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(11));
		}

		nodes[0].setNowOnCars(carLists.get(7));
		nodes[1].setNowOnCars(carLists.get(7));
		nodes[13].setNowOnCars(carLists.get(3));
		nodes[14].setNowOnCars(carLists.get(3));

		nodes[1].setNextNodeCarListChange(true);
		nodes[1].setChangeType(NodeChangeType.BUNKI);
		nodes[12].setNextNodeCarListChange(true);
		nodes[12].setChangeType(NodeChangeType.GORYU);
		nodes[12].setNextListMarge(1180);

		nodes[7].setLimitSpeed(16.66667);

		return nodeGroup;
	}

	private NodeList createNodeGroup13(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[21];

		nodes[0] = new StraightNode(2.5f, Math.PI / 2, new Point3f(7.5f, 0, 0.16f), 0);
		nodes[1] = new StraightNode(0.7f, Math.PI / 2, new Point3f(5.0f, 0, 0.16f), 0);
		nodes[2] = new CurveNode(-0.52f, Math.PI / 2, -CHANGELANE_ANGLE, new Point3f(4.30f, 0, 0.68f), 0, 0);
		nodes[3] = new CurveNode(0.52f, Math.PI / 2 - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(3.90f, 0, -0.28f), 0, 0);
		nodes[4] = new StraightNode(0.20f, Math.PI / 2, new Point3f(3.90f, 0, 0.24f), 0);
;		nodes[5] = new StraightNode(2.89f, Math.PI / 2, new Point3f(3.70f, 0, 0.24f), 0);
		nodes[6] = new StraightNode(0.73f, Math.PI / 2, new Point3f(0.81f, 0, 0.24f), 0);
 		nodes[7] = new CurveNode(-0.26f, Math.PI / 2, -CHANGELANE_ANGLE, new Point3f(0.08f, 0, 0.50f), 0, 0);
		nodes[8] = new CurveNode(0.26f, Math.PI / 2 - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-0.12f, 0, 0.02f), 0, 0);
		nodes[9] = new StraightNode(0.53f, Math.PI / 2, new Point3f(-0.12f, 0, 0.28f), 0);
		nodes[10] = new CurveNode(-0.53f, Math.PI / 2, -Math.PI * 3 / 2, new Point3f(-0.65f, 0, 0.81f), 0.08f, 0);
		nodes[11] = new StraightNode(0.53f, Math.PI, new Point3f(-0.12f, 0.08f, 0.81f), 0);
		nodes[12] = new CurveNode(0.26f, Math.PI, CHANGELANE_ANGLE, new Point3f(0.14f, 0, 0.28f), 0, 0.08f);
		nodes[13] = new CurveNode(-0.26f, Math.PI + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(-0.34f, 0, 0.08f), 0, 0.08f);
		nodes[14] = new StraightNode(0.73f, Math.PI, new Point3f(-0.08f, 0.08f, 0.08f), 0);
		nodes[15] = new StraightNode(2.89f, Math.PI, new Point3f(-0.08f, 0.08f, -0.65f), 0);
		nodes[16] = new StraightNode(0.20f, Math.PI, new Point3f(-0.08f, 0.08f, -3.54f), 0);
		nodes[17] = new CurveNode(0.52f, Math.PI, CHANGELANE_ANGLE, new Point3f(0.44f, 0, -3.74f), 0, 0.08f);
		nodes[18] = new CurveNode(-0.52f, Math.PI + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(-0.52f, 0, -4.14f), 0, 0.08f);
		nodes[19] = new StraightNode(0.86f, Math.PI, new Point3f(0, 0.08f, -4.14f), 0);
		nodes[20] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, -5.0f), 0);

		nodes[2].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[3].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[7].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[8].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[12].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[13].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[17].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[18].setType(NodeType.CHANGE_LANE_SECOND);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(12));
		}

		nodes[0].setNowOnCars(carLists.get(7));
		nodes[1].setNowOnCars(carLists.get(7));
		nodes[6].setNowOnCars(carLists.get(19));
		nodes[14].setNowOnCars(carLists.get(16));
		nodes[19].setNowOnCars(carLists.get(0));
		nodes[20].setNowOnCars(carLists.get(0));

		nodes[1].setNextNodeCarListChange(true);
		nodes[1].setChangeType(NodeChangeType.BUNKI);
		nodes[5].setNextNodeCarListChange(true);
		nodes[5].setChangeType(NodeChangeType.DONT_REMOVE);
		nodes[13].setNextNodeCarListChange(true);
		nodes[13].setChangeType(NodeChangeType.REMOVE_ONRY);
		nodes[18].setNextNodeCarListChange(true);
		nodes[18].setChangeType(NodeChangeType.GORYU);
		nodes[18].setNextListMarge(1164);

		nodes[9].setLimitSpeed(11.11111);
		nodes[11].setLimitSpeed(11.11111);

		return nodeGroup;
	}

	private NodeList createNodeGroup14(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[21];

		nodes[0] = new StraightNode(2.5f, Math.PI, new Point3f(0, 0.08f, 7.5f), 0);
		nodes[1] = new StraightNode(0.70f, Math.PI, new Point3f(0, 0.08f, 5.0f), 0);
		nodes[2] = new CurveNode(-0.52f, Math.PI, -CHANGELANE_ANGLE, new Point3f(-0.52f, 0, 4.30f), 0, 0.08f);
		nodes[3] = new CurveNode(0.52f, Math.PI - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(0.44f, 0, 3.90f), 0, 0.08f);
		nodes[4] = new StraightNode(0.20f, Math.PI, new Point3f(-0.08f, 0.08f, 3.90f), 0);
		nodes[5] = new StraightNode(2.89f, Math.PI, new Point3f(-0.08f, 0.08f, 3.70f), 0);
		nodes[6] = new StraightNode(0.73f, Math.PI, new Point3f(-0.08f, 0.08f, 0.81f), 0);
		nodes[7] = new CurveNode(-0.26f, Math.PI, -CHANGELANE_ANGLE, new Point3f(-0.34f, 0, 0.08f), 0, 0.08f);
		nodes[8] = new CurveNode(0.26f, Math.PI - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(0.14f, 0, -0.12f), 0, 0.08f);
		nodes[9] = new StraightNode(0.53f, Math.PI, new Point3f(-0.12f, 0.08f, -0.12f), 0);
		nodes[10] = new CurveNode(-0.53f, Math.PI, -Math.PI * 3 / 2, new Point3f(-0.65f, 0, -0.65f), -0.08f, 0.08f);
		nodes[11] = new StraightNode(0.53f, Math.PI * 3 / 2, new Point3f(-0.65f, 0, -0.12f), 0);
		nodes[12] = new CurveNode(0.26f, Math.PI * 3 / 2, CHANGELANE_ANGLE, new Point3f(-0.12f, 0, 0.14f), 0, 0);
		nodes[13] = new CurveNode(-0.26f, Math.PI * 3 / 2 + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(0.08f, 0, -0.34f), 0, 0);
		nodes[14] = new StraightNode(0.73f, Math.PI * 3 / 2, new Point3f(0.08f, 0, -0.08f), 0);
		nodes[15] = new StraightNode(2.89f, Math.PI * 3 / 2, new Point3f(0.81f, 0, -0.08f), 0);
		nodes[16] = new StraightNode(0.20f, Math.PI * 3 / 2, new Point3f(3.70f, 0, -0.08f), 0);
		nodes[17] = new CurveNode(0.52f, Math.PI * 3 / 2, CHANGELANE_ANGLE, new Point3f(3.90f, 0, 0.44f), 0, 0);
		nodes[18] = new CurveNode(-0.52f, Math.PI * 3 / 2 + CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(4.30f, 0, -0.52f), 0, 0);
		nodes[19] = new StraightNode(0.7f, Math.PI * 3 / 2, new Point3f(4.3f, 0, 0), 0);
		nodes[20] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(5.0f, 0, 0), 0);

		nodes[2].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[3].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[7].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[8].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[12].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[13].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[17].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[18].setType(NodeType.CHANGE_LANE_SECOND);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(13));
		}

		nodes[0].setNowOnCars(carLists.get(0));
		nodes[1].setNowOnCars(carLists.get(0));
		nodes[6].setNowOnCars(carLists.get(16));
		nodes[14].setNowOnCars(carLists.get(17));
		nodes[19].setNowOnCars(carLists.get(4));
		nodes[20].setNowOnCars(carLists.get(4));

		nodes[1].setNextNodeCarListChange(true);
		nodes[1].setChangeType(NodeChangeType.BUNKI);
		nodes[5].setNextNodeCarListChange(true);
		nodes[5].setChangeType(NodeChangeType.DONT_REMOVE);
		nodes[13].setNextNodeCarListChange(true);
		nodes[13].setChangeType(NodeChangeType.REMOVE_ONRY);
		nodes[18].setNextNodeCarListChange(true);
		nodes[18].setChangeType(NodeChangeType.GORYU);
		nodes[18].setNextListMarge(1180);

		nodes[9].setLimitSpeed(11.11111);
		nodes[11].setLimitSpeed(11.11111);

		return nodeGroup;
	}

	private NodeList createNodeGroup15(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[21];

		nodes[0] = new StraightNode(2.5f, Math.PI * 3 / 2, new Point3f(-7.5f, 0, 0), 0);
		nodes[1] = new StraightNode(0.86f, Math.PI * 3 / 2, new Point3f(-5.0f, 0, 0), 0);
		nodes[2] = new CurveNode(-0.52f, Math.PI * 3 / 2, -CHANGELANE_ANGLE, new Point3f(-4.14f, 0, -0.52f), 0, 0);
		nodes[3] = new CurveNode(0.52f, Math.PI * 3 / 2 - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-3.74f, 0, 0.44f), 0, 0);
		nodes[4] = new StraightNode(0.20f, Math.PI * 3 / 2, new Point3f(-3.74f, 0, -0.08f), 0);
		nodes[5] = new StraightNode(2.89f, Math.PI * 3 / 2, new Point3f(-3.54f, 0, -0.08f), 0);
		nodes[6] = new StraightNode(0.73f, Math.PI * 3 / 2, new Point3f(-0.65f, 0, -0.08f), 0);
		nodes[7] = new CurveNode(-0.26f, Math.PI * 3 / 2, -CHANGELANE_ANGLE, new Point3f(0.08f, 0, -0.34f), 0, 0);
		nodes[8] = new CurveNode(0.26f, Math.PI * 3 / 2 - CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(0.28f, 0, 0.14f), 0, 0);
		nodes[9] = new StraightNode(0.53f, Math.PI * 3 / 2, new Point3f(0.28f, 0, -0.12f), 0);
		nodes[10] = new CurveNode(-0.53f, Math.PI * 3 / 2, -Math.PI * 3 / 2, new Point3f(0.81f, 0, -0.65f), 0.08f, 0);
		nodes[11] = new StraightNode(0.53f, 0, new Point3f(0.28f, 0.08f, -0.65f), 0);
		nodes[12] = new CurveNode(0.26f, 0, CHANGELANE_ANGLE, new Point3f(0.02f, 0, -0.12f), 0, 0.08f);
		nodes[13] = new CurveNode(-0.26f, CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(0.50f, 0, 0.08f), 0, 0.08f);
		nodes[14] = new StraightNode(0.73f, 0, new Point3f(0.24f, 0.08f, 0.08f), 0);
		nodes[15] = new StraightNode(2.89f, 0, new Point3f(0.24f, 0.08f, 0.81f), 0);
		nodes[16] = new StraightNode(0.20f, 0, new Point3f(0.24f, 0.08f, 3.70f), 0);
		nodes[17] = new CurveNode(0.52f, 0, CHANGELANE_ANGLE, new Point3f(-0.28f, 0, 3.90f), 0, 0.08f);
		nodes[18] = new CurveNode(-0.52f, CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(0.68f, 0, 4.30f), 0, 0.08f);
		nodes[19] = new StraightNode(0.7f, 0, new Point3f(0.16f, 0.08f, 4.3f), 0);
		nodes[20] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, 5.0f), 0);

		nodes[2].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[3].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[7].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[8].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[12].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[13].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[17].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[18].setType(NodeType.CHANGE_LANE_SECOND);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(14));
		}

		nodes[0].setNowOnCars(carLists.get(4));
		nodes[1].setNowOnCars(carLists.get(4));
		nodes[6].setNowOnCars(carLists.get(17));
		nodes[14].setNowOnCars(carLists.get(18));
		nodes[19].setNowOnCars(carLists.get(3));
		nodes[20].setNowOnCars(carLists.get(3));

		nodes[1].setNextNodeCarListChange(true);
		nodes[1].setChangeType(NodeChangeType.BUNKI);
		nodes[5].setNextNodeCarListChange(true);
		nodes[5].setChangeType(NodeChangeType.DONT_REMOVE);
		nodes[13].setNextNodeCarListChange(true);
		nodes[13].setChangeType(NodeChangeType.REMOVE_ONRY);
		nodes[18].setNextNodeCarListChange(true);
		nodes[18].setChangeType(NodeChangeType.GORYU);
		nodes[18].setNextListMarge(1180);

		nodes[9].setLimitSpeed(11.11111);
		nodes[11].setLimitSpeed(11.11111);

		return nodeGroup;
	}

	private NodeList createNodeGroup16(){
		NodeList nodeGroup = new NodeList();

		Node[] nodes = new Node[21];

		nodes[0] = new StraightNode(2.5f, 0, new Point3f(0.16f, 0.08f, -7.5f), 0);
		nodes[1] = new StraightNode(0.86f, 0, new Point3f(0.16f, 0.08f, -5.0f), 0);
		nodes[2] = new CurveNode(-0.52f, 0, -CHANGELANE_ANGLE, new Point3f(0.68f, 0, -4.14f), 0, 0.08f);
		nodes[3] = new CurveNode(0.52f, -CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-0.28f, 0, -3.74f), 0, 0.08f);
		nodes[4] = new StraightNode(0.20f, 0, new Point3f(0.24f, 0.08f, -3.74f), 0);
		nodes[5] = new StraightNode(2.89f, 0, new Point3f(0.24f, 0.08f, -3.54f), 0);
		nodes[6] = new StraightNode(0.73f, 0, new Point3f(0.24f, 0.08f, -0.65f), 0);
		nodes[7] = new CurveNode(-0.26f, 0, -CHANGELANE_ANGLE, new Point3f(0.50f, 0, 0.08f), 0, 0.08f);
		nodes[8] = new CurveNode(0.26f, -CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(0.02f, 0, 0.28f), 0, 0.08f);
		nodes[9] = new StraightNode(0.53f, 0, new Point3f(0.28f, 0.08f, 0.28f), 0);
		nodes[10] = new CurveNode(-0.53f, 0, -Math.PI * 3 / 2, new Point3f(0.81f, 0, 0.81f), -0.08f, 0.08f);
		nodes[11] = new StraightNode(0.53f, Math.PI / 2, new Point3f(0.81f, 0, 0.28f), 0);
		nodes[12] = new CurveNode(0.26f, Math.PI / 2, CHANGELANE_ANGLE, new Point3f(0.28f, 0, 0.02f), 0, 0);
		nodes[13] = new CurveNode(-0.26f, Math.PI / 2 + CHANGELANE_ANGLE, -CHANGELANE_ANGLE, new Point3f(0.08f, 0, 0.50f), 0, 0);
		nodes[14] = new StraightNode(0.73f, Math.PI / 2, new Point3f(0.08f, 0, 0.24f), 0);
		nodes[15] = new StraightNode(2.89f, Math.PI / 2, new Point3f(0.81f, 0, 0.24f), 0);
		nodes[16] = new StraightNode(0.20f, Math.PI / 2, new Point3f(3.70f, 0, 0.24f), 0);
		nodes[17] = new CurveNode(0.52f, Math.PI / 2, CHANGELANE_ANGLE, new Point3f(-3.74f, 0, -0.28f), 0, 0);
		nodes[18] = new CurveNode(-0.52f, Math.PI / 2 + CHANGELANE_ANGLE, CHANGELANE_ANGLE, new Point3f(-4.14f, 0, 0.68f), 0, 0);
		nodes[19] = new StraightNode(0.86f, Math.PI / 2, new Point3f(-4.14f, 0, 0.16f), 0);
		nodes[20] = new StraightNode(2.5f, Math.PI / 2, new Point3f(-5.0f, 0, 0.16f), 0);

		nodes[2].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[3].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[7].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[8].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[12].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[13].setType(NodeType.CHANGE_LANE_SECOND);
		nodes[17].setType(NodeType.CHANGE_LANE_FIRST);
		nodes[18].setType(NodeType.CHANGE_LANE_SECOND);

		for (Node node : nodes) {
			nodeGroup.add(node);
			node.setNowOnCars(carLists.get(15));
		}

		nodes[0].setNowOnCars(carLists.get(3));
		nodes[1].setNowOnCars(carLists.get(3));
		nodes[6].setNowOnCars(carLists.get(18));
		nodes[14].setNowOnCars(carLists.get(19));
		nodes[19].setNowOnCars(carLists.get(7));
		nodes[20].setNowOnCars(carLists.get(7));

		nodes[1].setNextNodeCarListChange(true);
		nodes[1].setChangeType(NodeChangeType.BUNKI);
		nodes[5].setNextNodeCarListChange(true);
		nodes[5].setChangeType(NodeChangeType.DONT_REMOVE);
		nodes[13].setNextNodeCarListChange(true);
		nodes[13].setChangeType(NodeChangeType.REMOVE_ONRY);
		nodes[18].setNextNodeCarListChange(true);
		nodes[18].setChangeType(NodeChangeType.GORYU);
		nodes[18].setNextListMarge(1164);

		nodes[9].setLimitSpeed(11.11111);
		nodes[11].setLimitSpeed(11.11111);

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
				start = new Point3f(0, 0.08f, 7.5f);
				direction = Math.PI;
			} else if (rand == 1) {
				nodeList = createNodeGroup2();
				start = new Point3f(0.04f, 0.08f, 7.5f);
				direction = Math.PI;
			} else if (rand == 2) {
				nodeList = createNodeGroup3();
				start = new Point3f(0.12f, 0.08f, -7.5f);
				direction = 0;
			} else if (rand == 3) {
				nodeList = createNodeGroup4();
				start = new Point3f(0.16f, 0.08f, -7.5f);
				direction = 0;
			} else if (rand == 4) {
				nodeList = createNodeGroup5();
				start = new Point3f(-7.5f, 0, 0);
				direction = Math.PI * 3 / 2;
			} else if (rand == 5) {
				nodeList = createNodeGroup6();
				start = new Point3f(-7.5f, 0, 0.04f);
				direction = Math.PI * 3 / 2;
			} else if (rand == 6) {
				nodeList = createNodeGroup7();
				start = new Point3f(7.5f, 0, 0.12f);
				direction = Math.PI / 2;
			} else if (rand == 7) {
				nodeList = createNodeGroup8();
				start = new Point3f(7.5f, 0, 0.16f);
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
				start = new Point3f(0, 0.08f, 7.5f);
				direction = Math.PI;
			} else if (rand == 1) {
				nodeList = createNodeGroup10();
				start = new Point3f(-7.5f, 0, 0);
				direction = Math.PI * 3 / 2;
			} else if (rand == 2) {
				nodeList = createNodeGroup11();
				start = new Point3f(0.16f, 0.08f, -7.5f);
				direction = 0;
			} else if (rand == 3) {
				nodeList = createNodeGroup12();
				start = new Point3f(7.5f, 0, 0.16f);
				direction = Math.PI / 2;
			} else if (rand == 4) {
				nodeList = createNodeGroup13();
				start = new Point3f(7.5f, 0, 0.16f);
				direction = Math.PI / 2;
			} else if (rand == 5) {
				nodeList = createNodeGroup14();
				start = new Point3f(0, 0.08f, 7.5f);
				direction = Math.PI;
			} else if (rand == 6) {
				nodeList = createNodeGroup15();
				start = new Point3f(-7.5f, 0, 0);
				direction = Math.PI * 3 / 2;
			} else if (rand == 7) {
				nodeList = createNodeGroup16();
				start = new Point3f(0.16f, 0.08f, -7.5f);
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
