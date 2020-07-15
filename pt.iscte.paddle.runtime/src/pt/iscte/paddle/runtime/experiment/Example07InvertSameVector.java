package pt.iscte.paddle.runtime.experiment;

import java.util.Arrays;

public class Example07InvertSameVector {

	public static void invert(int[] v) {
		int i = 0;
		while(i < v.length / 2) {
			swapElements(v, i, v.length - i);		//swapElements(v, i, v.length - i - 1);
			i = i + 1;
		}
	}
	
	public static void swapElements(int[] v, int i, int j) {
		int temp = v[i];
		v[i] = v[j];
		v[j] = temp;
	}
	
	public static void main(String[] args) {
		int [] v = {1, 2, 3, 4, 5};
		System.out.println("Original Array: " + Arrays.toString(v));
		invert(v);
		System.out.println("Inverted Array: " + Arrays.toString(v));
	}
}
