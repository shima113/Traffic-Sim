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

	/**
	 * 道路にいる車がどこにいるのかを表すリスト
	 */
	ArrayList<Float> carMovedList;

	public static final Car NOTHING_INFRONT = new Car(100000);
	
	public CarList() {
		cars = new ArrayList<>();
		carMovedList = new ArrayList<>();
	}

	public void addCar(Car car) {
		cars.add(car);
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

	public Car getCar(int index){
		return cars.get(index);
	}

	public int getCarSize(){
		return cars.size();
	}

	public void removeCar(Car thiscar){
		cars.remove(thiscar);
	}
}
