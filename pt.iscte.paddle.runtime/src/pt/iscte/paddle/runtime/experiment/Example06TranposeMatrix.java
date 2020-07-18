package pt.iscte.paddle.runtime.experiment;

import java.util.Arrays;

public class Example06TranposeMatrix {

	//columns of the same length and rows of the same length
	public static int[][] transposeMatrix(int [][] m){
		int [][] transposed = new int [m[0].length][m.length];
		int i = 0;
		while(i < m[0].length) {
			int j = 0;
			while(j < m.length) {
				transposed[j][i] = m[i][j];				//transposed[i][j] = m[j][i]
				j = j + 1;
			}
			i = i + 1;
		}
		return transposed;
	}
	
	public static void main(String[] args) {
		int [][] original = {{1, 2, 3},
				  			 {4, 5, 6}};
		
		int [][] transposed = transposeMatrix(original);
		
		System.out.println("Original Matrix:");
		System.out.println(Arrays.deepToString(original));
		System.out.println("Transposed Matrix:");
		System.out.println(Arrays.deepToString(transposed));
	}

}
