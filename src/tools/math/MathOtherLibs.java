package tools.math;

public class MathOtherLibs {

	//最大公約数(ユークリッドの互除法を使用)
	public static int gcd(int a, int b) {
		while(a != b) {
			if(a < b) {
				b -= a;
			}else if(a > b) {
				a -= b;
			}
		}
			
		return a;
	}
		
	//最小公倍数
	public static int lcm(int a, int b) {
		int result = a * b / gcd(a,b);
		
		return result;
	}
}
