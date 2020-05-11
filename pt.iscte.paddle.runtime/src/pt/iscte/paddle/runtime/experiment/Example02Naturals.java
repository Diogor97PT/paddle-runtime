package pt.iscte.paddle.runtime.experiment;

import java.util.Arrays;

public class Example02Naturals {
	
	//Example 2
	public static int[] naturals(int n) {
		int [] array = new int [n];
		int i = 0;
		while(i < n) {
			i = i + 1;						//switch i = i + 1 with the line below
			array[i] = i + 1;
		}
		return array;
	}
	
	public static void main(String[] args) {
		int [] array = naturals(5);
		System.out.println("Array: " + Arrays.toString(array));
	}
}
