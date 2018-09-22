import java.util.Random;

public class Test {

	public static void test1() {
		Object a = new Object();
		Object b = new Object();
		b = a;
		System.out.print(b);
	}
	
	public static void test2() {
		Object a = new Object();
		Object b = new Object();
		Object c = a;
		if(new Random().nextBoolean()) {
			c = b;
		}
		System.out.print(c);
	}
	
}
