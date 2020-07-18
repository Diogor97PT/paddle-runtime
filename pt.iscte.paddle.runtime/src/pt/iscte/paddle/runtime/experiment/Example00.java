package pt.iscte.paddle.runtime.experiment;

public class Example00 {
	
	public static int example(int [] v) {
		return v[v.length];
	}
	
	public static void main(String[] args) {
		int [] array = {1, 2};
		System.out.println("Array last value is " + example(array));
	}
}
