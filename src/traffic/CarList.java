package traffic;

import java.util.ArrayList;

import cars.Car;

/**
 * 車間距離を確認するためにある、道路にどの車がいるのかのリストを実装する
 */
public class CarList {

	/**
	 * 道路にいる車のリスト
	 */
	ArrayList<Car> cars;

	Road road;
	NodeList toChangeLane;

	public static final Car NOTHING_INFRONT = new Car(100000);
	
	public CarList() {
		cars = new ArrayList<>();
	}

	public void addCar(Car car) {
		cars.add(car);
	}
	
	public void addCarChanged(Car myCar){
		int index = searchDistance(myCar.totalDistance);
		cars.add(index, myCar);
	}

	/**
	 * 自分の車の前にいる車の情報を取得するメソッド
	 * @param myCar :自分の車
	 * @return 前の車
	 */
	public Car getInFrontCar(Car myCar) {
		int index = cars.indexOf(myCar);
		Car inFrontCar;
		
		if (index != 0) {
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
				ret = i + 1;
				break;
			}
		}

		if (cars.size() == 0){
			ret = 0;
		}

		return ret;
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
}
