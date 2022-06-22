package traffic;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cars.Car;

/**
 * 車間距離を確認するためにある、道路にどの車がいるのかのリストを実装する
 */
public class CarList {

	int nodegroupIndex;

	/**
	 * 道路にいる車のリスト
	 */
	ArrayList<Car> cars;

	Road road;
	CarList bunkiCarList, gouryuCarList;
	NodeList toChangeLane;

	public static final Car NOTHING_INFRONT = new Car(100000);
	
	public CarList() {
		cars = new ArrayList<>();

		Timer timer = new Timer();
		/*timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (nodegroupIndex == 12) System.out.println(CarList.this);
			}
		}, 0, 40);*/


	}

	public void addCar(Car car) {
		cars.add(car);
	}
	
	public void addCarChanged(Car myCar){
		addCarChanged(myCar, myCar.totalDistance);
	}

	public void addCarChanged(Car myCar, float distance){
		int index = searchDistance(distance);

		if (index == 0){
			cars.add(myCar);
		}else {
			cars.add(index, myCar);
		}

		sort();
	}

	/**
	 * 自分の車の前にいる車の情報を取得するメソッド
	 * @param myCar :自分の車
	 * @return 前の車
	 */
	public Car getInFrontCar(Car myCar) {
		int index = cars.indexOf(myCar);
		Car inFrontCar;
		
		if (index != 0 && index != -1) {
			inFrontCar = cars.get(index - 1);			
		}else {
			inFrontCar = NOTHING_INFRONT;
		}
		
		return inFrontCar;
	}

	public int searchDistance(float distance){
		int ret = cars.size() - 1;
		for (int i = 0; i < cars.size(); i++) {
			if (cars.get(i).getTotalDistance() < distance){
				ret = i;
				break;
			}
		}

		if (cars.size() == 0){
			ret = 0;
		}

		return ret;
	}

	public float getInFrontDistance(float distance){
		float retDistance;
		Car infrontCar = new Car(10000000);

		for (int i = 0; i < cars.size(); i++) {
			if (cars.get(i).getTotalDistance() < distance){
				if (i != 0){
					infrontCar = cars.get(i - 1);
				}
				break;
			}
		}

		retDistance = infrontCar.getTotalDistance() - distance;
		return  retDistance;
	}

	private void sort(){
		int length = cars.size();

		int num;
		int pos;
		Car temp;

		for (num = 0; num < length; num++){
			for (pos = length - 1; pos >= num + 1; pos--){
				if (cars.get(pos).getTotalDistance() > cars.get(pos - 1).getTotalDistance()){
					temp = cars.get(pos);
					cars.set(pos, cars.get(pos - 1));
					cars.set(pos - 1, temp);
				}
			}
		}
	}

	public Car getCar(int index){
		return cars.get(index);
	}

	public int getCarSize(){
		return cars.size();
	}

	public void removeCar(Car thiscar){
		cars.remove(thiscar);
	}

	public Road getRoad() {
		return road;
	}

	public void setRoad(Road road) {
		this.road = road;
	}

	public NodeList getToChangeLane() {
		return toChangeLane;
	}

	public void setToChangeLane(NodeList toChangeLane) {
		this.toChangeLane = toChangeLane;
	}

	public CarList getBunkiCarList() {
		return bunkiCarList;
	}

	public void setBunkiCarList(CarList bunkiCarList) {
		this.bunkiCarList = bunkiCarList;
	}

	public CarList getGouryuCarList() {
		return gouryuCarList;
	}

	public void setGouryuCarList(CarList gouryuCarList) {
		this.gouryuCarList = gouryuCarList;
	}

	public int getNodegroupIndex() {
		return nodegroupIndex;
	}

	public void setNodegroupIndex(int nodegroupIndex) {
		this.nodegroupIndex = nodegroupIndex;
	}

	@Override
	public String toString() {
		StringBuilder stringBuffer = new StringBuilder();
		stringBuffer.append(cars.size());
		stringBuffer.append(", ");
		for (Car car : cars) {
			stringBuffer.append(car.totalDistance);
			stringBuffer.append(", ");
			stringBuffer.append(car.getSpeed());
			stringBuffer.append(", ");
			stringBuffer.append(car.getAcceralation());
			stringBuffer.append(", ");
			stringBuffer.append(car.getNowNode().getLimitSpeed());
			stringBuffer.append(";  ");
		}

		return stringBuffer.toString();
	}
}
