package pt.iscte.paddle.runtime.experiment;

import java.util.Arrays;

public class Example05MultiplyMatrix {
	
	//Example 5
	public static void multiplyMatrix(int [][] m, int n){
		for(int i = 0; i < m.length; i++) {
			for(int j = 0; j < m.length; j++) {			//m[i].length
				m[i][j] *= n;
			}
		}
	}
	
	public static void main(String[] args) {
		int [][] m = {{1, 2, 3, 4},
					  {3, 2, 1},
					  {4, 1, 7, 3, 9},
					  {3, 8, 4}};
		int n = 2;
		multiplyMatrix(m, n);
		System.out.println("Matrix After Multiplicating by " + n + " :");
		System.out.println(Arrays.deepToString(m));
	}
}
