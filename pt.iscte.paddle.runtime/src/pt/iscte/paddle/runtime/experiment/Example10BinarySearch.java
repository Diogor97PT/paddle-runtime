package pt.iscte.paddle.runtime.experiment;

public class Example10BinarySearch {
	
	//Returns the position of n or -1 if it does not exist 
	public static int binarySearch(int [] v, int n) {
		int i = 0;
		int r = v.length - 1;
		
		while(i <= r) {
			int j = i + (i / 2);				//Swap with the line below
//			int j = i + (r - i) / 2;
			
			if(v[j] == n)
				return j;
			
			if(v[j] < n)
				i = j + 1;
			else
				r = j - 1;
		}
		
		return -1;
	}
	
	
	public static void main(String[] args) {
		int [] v = {1, 5, 6, 7, 9, 10, 23, 52, 88, 99};
		int n = 88;
		int position = binarySearch(v, n);
		if(position == -1)
			System.out.println("Number " + n + " not found.");
		else
			System.out.println("Number " + n + " can be found in position " + position);
	}
}
