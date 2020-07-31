package pt.iscte.paddle.runtime.experiment;

public class Example00 {
	
	public static int example(int [] v) {
		int [] v2 = new int[v.length];
		v2[1] = 4;
		return v2[v2.length];
	}
	
	public static void main(String[] args) {
		int [] array = {1, 2};
		System.out.println("Array last value is " + example(array));
	}
}
