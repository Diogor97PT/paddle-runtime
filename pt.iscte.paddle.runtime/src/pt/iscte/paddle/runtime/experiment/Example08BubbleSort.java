package pt.iscte.paddle.runtime.experiment;

import java.util.Arrays;

public class Example08BubbleSort {

	public static void bubbleSort(int [] v) {
		int i = 0;
		while(i < v.length) {
			int j = 0;
			while(j < v.length - i) {						//j < v.length - i - 1
				if(v[j] > v[j + 1]) {
					int temp = v[j];
					v[j] = v[j + 1]; 
					v[j + 1] = temp;  
				}
				j = j + 1;
			}
			i = i + 1;
		}
	}
	
	public static void main(String[] args) {
		int [] v = {9, 5, 10, 99, 52, 23, 88, 88, 1, 1, 6, 7};
		System.out.println("Original Array: " + Arrays.toString(v));
		bubbleSort(v);
		System.out.println("Sorted Array: " + Arrays.toString(v));
	}
}
