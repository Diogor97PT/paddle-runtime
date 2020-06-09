package pt.iscte.paddle.runtime.experiment;

import java.util.Arrays;

public class Example09SelectionSort {

	//TODO Example09 No error yet
	public static void selectionSort(int [] v) {
		for(int i = 0; i < v.length - 1; i++) {
			int minimumPos = i;
			for(int j = i + 1; j < v.length; j++) {
				if(v[j] < v[minimumPos])
					minimumPos = j;
			}
			swapElements(v, minimumPos, i);
		}
	}
	
	public static void swapElements(int[] v, int i, int j) {
		int temp = v[i];
		v[i] = v[j];
		v[j] = temp;
	}
	
	public static void main(String[] args) {
		int [] v = {9, 5, 10, 99, 52, 23, 88, 88, 1, 1, 6, 7};
		System.out.println("Original Array: " + Arrays.toString(v));
		selectionSort(v);
		System.out.println("Sorted Array: " + Arrays.toString(v));
	}

}
