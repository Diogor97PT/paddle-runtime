package pt.iscte.paddle.runtime.experiment;

import java.util.Arrays;

public class Example06TranposeMatrix {

	//columns of the same length and rows of the same length
	public static int[][] tranposeMatrix(int [][] m){
		int [][] transposed = new int [m[0].length][m.length];
		for(int i = 0; i < m[0].length; i++) {
			for(int j = 0; j < m.length; j++) {
				transposed[j][i] = m[i][j];				//transposed[i][j] = m[j][i]
			}
		}
		return transposed;
	}
	
	public static void main(String[] args) {
		int [][] original = {{1, 2, 3},
				  			 {4, 5, 6}};
		
		int [][] transposed = tranposeMatrix(original);
		
		System.out.println("Original Matrix:");
		System.out.println(Arrays.deepToString(original));
		System.out.println("Transposed Matrix:");
		System.out.println(Arrays.deepToString(transposed));
	}

}
