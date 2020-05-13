package pt.iscte.paddle.runtime.experiment;

import java.util.Arrays;

public class Example04Invert {
	
	public static int[] invert(int[] v) {
		int [] v2 = new int[v.length];
		int i = v.length - 1;
		while(i >= 0) {
			v2[v.length - i] = v[i];		//v.length - i - 1
			i--;
		}
		return v2;
	}
	
	public static void main(String[] args) {
		int [] v = {1, 2, 3, 4, 5};
		int [] array = invert(v);
		System.out.println("Original Array: " + Arrays.toString(v));
		System.out.println("Inverted Array: " + Arrays.toString(array));
	}

}
