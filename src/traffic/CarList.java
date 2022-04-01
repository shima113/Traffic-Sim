package traffic;

import java.util.ArrayList;

import cars.Car;

public class CarList {

	ArrayList<Car> cars;
	ArrayList<Float> carMovedList;

	public static final Car NOTHING_INFRONT = new Car(1000);
	
	public CarList() {
		cars = new ArrayList<>();
		carMovedList = new ArrayList<>();
	}
	
	public void addCar(Car car) {
		cars.add(car);
	}
	
	public Car getInFrontCar(Car myCar) {
		int index = cars.indexOf(myCar);
		Car inFrontCar = null;
		
		if (index != 0) {
			inFrontCar = cars.get(index - 1);			
		}else {
			inFrontCar = NOTHING_INFRONT;
		}
		
		return inFrontCar;
	}
}
