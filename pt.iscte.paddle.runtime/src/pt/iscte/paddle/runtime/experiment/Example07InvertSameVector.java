package pt.iscte.paddle.runtime.experiment;

import java.util.Arrays;

public class Example07InvertSameVector {

	public static void invert(int[] v) {
		for(int i = 0; i < v.length / 2; i++) {
			swapElements(v, i, v.length - i);		//swapElements(v, i, v.length - i - 1);
		}
	}
	
	public static void swapElements(int[] nums, int i, int j) {
		int temp = nums[i];
		nums[i] = nums[j];
		nums[j] = temp;
	}
	
	public static void main(String[] args) {
		int [] v = {1, 2, 3, 4, 5};
		System.out.println("Original Array: " + Arrays.toString(v));
		invert(v);
		System.out.println("Inverted Array: " + Arrays.toString(v));
	}
}
