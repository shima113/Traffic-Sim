package cars;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.openxmlformats.schemas.presentationml.x2006.main.impl.OleObjDocumentImpl;
import traffic.CurveNode;
import traffic.Node;
import traffic.StraightNode;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 車クラス<br>
 * 車の加減速、座標移動などを行います
 */
public class Car implements ActionListener {

	/**
	 * いまnodeGroupの何番目のNodeにいるのかを表す
	 */
	int nowNodeIndex = 0;
	/**
	 * ボタンで加減速していた時に使っていた
	 */
	int accel = 0;

	/**
	 * Excel出力用　開始時刻
	 */
	long startTime = 0;

	/**
	 * 1フレーム（デフォルトは0.04秒）に動いた距離
	 */
	float movedDistance;
	/**
	 * 今いるNodeに入ってから動いた距離の総量
	 */
	float movedDistanceForCheckNode = 0;
	/**
	 * 1フレーム経過するあいだに今のNodeが終わってしまったため、このフレームから次のNodeに入るときにいろいろつじつまを合わせるやつ
	 */
	float movedDistanceRimainder = 0;
	/**
	 * これまでに動いた距離の合計　1=1m
	 */
	float totalDistance, totalDistanceDisplay = 0;

	/**
	 * 現在地の座標{x, y, z}　1=100m
	 */
	float[] movedVector = new float[3];
	/**
	 * 1フレームの間隔
	 */
	double time;
	/**
	 * 速度（m/s）秒速！！！！！！！！
	 */
	double speed = 20; //秒速！！！！！！（m/s）
	/**
	 * 加速度（たぶんm/s^2）
	 */
	double acceralation = 0;
	double nowDirection = 0;

	/**
	 * 車のオブジェクト　車の種類ごと（PassengerCar, Busなど）に生成される
	 */
	public TransformGroup carObjectGroup;
	/**
	 * 車の方向のTransform3D
	 */
	Transform3D angleTransform3d = new Transform3D();

	/**
	 * これからこの車がたどるであろうNodeのリスト
	 */
	ArrayList<Node> nodeGroup = new ArrayList<>();
	Node nowNode;
	/**
	 * 移動計算用　1フレームごとに計算する
	 */
	Timer timer;

	Sheet expSheet;

	Node atoNode;
	ArrayList<Node> atnodegroupArrayList;
	int carnum;

	final Node STOPNODE = new StraightNode(100, 0, new Point3f(), 0);

	public Car(int loadingTime, ArrayList<Node> nodes, Point3f startPoint, double direction, double speed) {
		time = loadingTime / 1000.0; //ミリ秒 → 秒
		nodeGroup = nodes;
		nowNode = nodeGroup.get(nowNodeIndex);
		movedVector[0] = startPoint.x;
		movedVector[1] = startPoint.y;
		movedVector[2] = startPoint.z;
		
		nowNode.getNowOnCars().addCar(this);
		
		this.speed = speed;

		timer = new Timer();
		timer.schedule(new TimerMove(), 1000, loadingTime);

		startTime = System.currentTimeMillis() + 1000;
	}

	public Car() {}
	public Car(float totalDistance) {this.totalDistance = totalDistance;} //NOTHING_INFRONT用

	public void move() {
		movedDistance = movedDistanceRimainder;
		movedDistanceRimainder = 0;

		movedDistance = (float)(speed * time + acceralation * time * time / 2); //x = V0t + 1/2at^2
		speed = speed + acceralation * time; //V = V0 + at

		if(speed < 0) {
			stop();
		}

		if (nowNode.equals(STOPNODE)) {
			stop();
			return;
		}

		movedVector = nowNode.move(movedDistance, movedVector);

		movedDistanceForCheckNode += movedDistance;

	}

	public void changeLane(Node targetNode, ArrayList<Node> targetNodeGroup) {
		float distance = (float) nowNode.getEquationStraight().getDistanceLine(targetNode.getEquationStraight());
		distance /= 2;

		System.out.println(distance);
		System.out.println(movedVector[0]);
		System.out.println(movedVector[2]);


		final float CHANGELANE_INTERVAL = 0.10f;

		float radius = (distance * distance + CHANGELANE_INTERVAL * CHANGELANE_INTERVAL) / (2 * distance);
		double declination = nowNode.getNowDirection();
		double angle = Math.acos((radius - distance) / radius);

		Point3f centrePoint3f1 =
				new Point3f((float)(Math.cos(declination) * radius) + movedVector[0], movedVector[1], (float) Math.sin(declination) * -radius + movedVector[2]);
		Point3f centrePoint3f2 =
				new Point3f((float)(-distance * 2 * Math.cos(declination) - CHANGELANE_INTERVAL * 2 * Math.cos(Math.PI / 2 - declination)) + movedVector[0], movedVector[1],
						(float) (distance * 2 * Math.sin(declination) + CHANGELANE_INTERVAL * 2 * Math.sin(Math.PI / 2 - declination) + movedVector[2]));

		CurveNode changeLaneNode1 = new CurveNode(radius, declination, angle, centrePoint3f1, 0, movedVector[1]);
		CurveNode changeLaneNode2 = new CurveNode(-radius, declination + angle, -angle, centrePoint3f2, 0, movedVector[1]);
		//StraightNode straightNode = new StraightNode(10.0f, declination, new Point3f(movedVector[0] + CHANGELANE_INTERVAL * 2, movedVector[1], movedVector[2] + distance * 2), 0);

		/*System.out.println("radius" + radius);//atteru
		System.out.println("declin" + declination);
		System.out.println("angle " + angle);//atteru
		System.out.println("centr1" + centrePoint3f1);
		System.out.println("centr2" + centrePoint3f2);*/

		nodeGroup.add(changeLaneNode1);
		nodeGroup.add(changeLaneNode2);
		//nodeGroup.add(straightNode);
	}

	public void setAtoNode(Node atoNode) {
		this.atoNode = atoNode;
	}

	public void setAtnodegroupArrayList(ArrayList<Node> atnodegroupArrayList) {
		this.atnodegroupArrayList = atnodegroupArrayList;
	}

	public Car checkInFrontCar() {
		return nowNode.getNowOnCars().getInFrontCar(this);
	}

	public void accelByDistance(Car inFrontCar, int time) {
		float distanceInFront = inFrontCar.getTotalDistance() - this.totalDistance;
		double speedPerHour = speed * 3.6;

		if (time == 25) {
			if (distanceInFront > (speedPerHour * 2) && speed < nowNode.getLimitSpeed()) {
				acceralation = 4;
			}else if (distanceInFront < (speedPerHour * 0.8)) {
				acceralation = -6;
			}else {
				acceralation = 0;
			}
			
			if (distanceInFront < speedPerHour) {
				if (inFrontCar.getSpeed() < (speed - 11.11111)) {
					acceralation = -6;
				}else if (inFrontCar.getSpeed() < (speed - 5.55556)) {
					acceralation = -3;
				}
			}else {
				if (inFrontCar.getSpeed() > (speed + 5.55556)) {
					acceralation = 3;
				}else if (inFrontCar.getSpeed() < (speed + 11.11111)) {
					acceralation = 6;
				}
			}
		}
		
		//emergency stop
		if (inFrontCar.getAcceralation() <= -20) {
			if (distanceInFront < speedPerHour) {
				acceralation = -10;
			}
		}
		
		if (distanceInFront < (speedPerHour / 4)) {
			acceralation = -20;
		}else if (distanceInFront < (speedPerHour / 3)) {
			acceralation = -12;
		}else if (distanceInFront < (speedPerHour / 2)) {
			acceralation = -8;
		}
		
		//crash
		if (distanceInFront <= 5) {
			speed = 0;
		}
	}
	
	public void checkLimitSpeed() {
		if (speed > nowNode.getLimitSpeed() + 20) {
			acceralation = -10;
		}else if (speed > nowNode.getLimitSpeed() + 5) {
			acceralation = -4;
		}
	}

	long endTime = 0;

	public void updateNode() {
		if(movedDistanceForCheckNode > nowNode.getLength() * 100) {
			nowNodeIndex++;
			
			if (nowNodeIndex >= nodeGroup.size()) {
				endTime = System.currentTimeMillis();
				nowNode.getNowOnCars().removeCar(this);
				nowNode = STOPNODE;
				nowNodeIndex--;
				nodeGroup = null;
				timer.cancel();
				timer = null;
				//changeLane(atoNode, atnodegroupArrayList);

				exportExcel();
				return;
			}

			movedDistanceRimainder = movedDistanceForCheckNode - nowNode.getLength() * 100;
			movedDistanceForCheckNode -= nowNode.getLength() * 100;

			//nowNode.getNowOnCars().remove(nowNode.getNowOnCars().indexOf(this));

			nowNode = nodeGroup.get(nowNodeIndex);

			//nowNode.getNowOnCars().add(this);
		}
	}

	class TimerMove extends TimerTask{
		Vector3f movedVector3f = new Vector3f(0.0f, 0.0f, 0.0f);
		Transform3D movedTransform3d = new Transform3D();

		//field for curve
		double angle;
		double movedAngle;
		double declination = 0;

		double[] printed;
		
		int temp;

		@Override
		public void run() {
			moveCulculation();

			totalDistance += movedDistance;
			carObjectGroup.setTransform(movedTransform3d);

			speed = Math.ceil(speed * 10) / 10;
			totalDistanceDisplay = (float) (Math.ceil(totalDistance * 10) / 10);
			//System.out.println(movedVector[0] + ",  " +  movedVector[1] + ",  " + movedVector[2]);

			//System.out.println(carnum + ": " + nowNode.getNowDirection());
		}

		private void moveCulculation() {
			move();
			movedVector3f.x = movedVector[0];
			movedVector3f.y = movedVector[1];
			movedVector3f.z = movedVector[2];

			movedTransform3d.setIdentity();
			movedTransform3d.setTranslation(movedVector3f);
			//printtransform();
			angleTransform3d.rotY(-nowNode.getNowDirection());
			movedTransform3d.mul(angleTransform3d);
			updateNode();
			temp++;
			if (!nowNode.equals(STOPNODE)) {
				accelByDistance(checkInFrontCar(), temp);
				checkLimitSpeed();
			}
			
			if (temp == 25) {
				temp = 0;
			}
		}

		@SuppressWarnings("unused")
		private void printtransform() {
			System.out.println(movedTransform3d.toString());
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		switch(command) {
		case "accel":
			accel++;
			if(accel > 6) {
				accel--;
			}
			setAcceralation(accel * 4);
			break;
		case "brake":
			accel--;
			if(accel < -6) {
				accel++;
			}
			setAcceralation(accel * 6);
			break;
		}
	}

	int sheetIndex = 0;
	int nodegroupIndex = 1000;

	private void exportExcel(){
		Row row = expSheet.createRow(sheetIndex);
		Cell time = row.createCell(0);
		Cell length = row.createCell(1);
		Cell speed = row.createCell(2);
		Cell nodegrop = row.createCell(3);

		String rowIndex = String.valueOf(sheetIndex + 1);

		time.setCellValue(endTime - startTime);
		length.setCellValue(totalDistance);
		speed.setCellFormula("B" + rowIndex + "/" + "A" + rowIndex + "*3600");
		nodegrop.setCellValue(nodegroupIndex);

	}

	public void stop() {
		speed = 0;
		acceralation = 0;
	}

	public TransformGroup getCarObjectGroup() {
		return carObjectGroup;
	}

	public double getSpeed() {
		return speed;
	}

	public int getAccel() {
		return accel;
	}

	public float getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(float totalDistance) {
		this.totalDistance = totalDistance;
	}

	public float getTotalDistanceDisplay() {
		return totalDistanceDisplay;
	}

	public float getMovedDistance() {
		return movedDistance;
	}

	public float getMovedDistanceForCheckNode() {
		return movedDistanceForCheckNode;
	}

	public void setAcceralation(double accel) {
		acceralation = accel;
	}

	public double getAcceralation() {
		return acceralation;
	}

	public void setCarnum(int carnum) {
		this.carnum = carnum;
	}

	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}

	public void setExpSheet(Sheet expSheet) {
		this.expSheet = expSheet;
	}

	public void setNodegroupIndex(int nodegroupIndex) {
		this.nodegroupIndex = nodegroupIndex;
	}
}
