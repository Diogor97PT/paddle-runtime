package pt.iscte.paddle.runtime.experiment;

import java.util.Arrays;

public class Example05ScaleMatrix {
	
	//Example 5
	public static void scaleMatrix(int [][] m, int n){
		int i = 0;
		while(i < m.length) {
			int j = 0;
			while( j < m.length) {				//m[i].length
				m[i][j] = m[i][j] * n;
				j = j + 1;
			}
			i = i + 1;
		}
	}
	
	public static void main(String[] args) {
		int [][] m = {{1, 2, 3, 4},
					  {3, 2, 1},
					  {4, 1, 7, 3},
					  {3, 8, 4}};
		int n = 2;
		scaleMatrix(m, n);
		System.out.println("Matrix After Multiplicating by " + n + " :");
		System.out.println(Arrays.deepToString(m));
	}
}
