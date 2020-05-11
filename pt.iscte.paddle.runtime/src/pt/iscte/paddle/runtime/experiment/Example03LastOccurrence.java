package pt.iscte.paddle.runtime.experiment;

public class Example03LastOccurrence {
	
	//Returns the position of the last occurrence of n or -1 if it does not exist 
	//Example 3
	public static int lastOccurrence(int [] v, int n) {
		int i = v.length - 1;
		while(i >= -1) {					//i >= 0 or i > -1
			if(v[i] == n) {
				return i;
			}
			i--;
		}
		return -1;
	}
	
	public static void main(String[] args) {
		int [] v = {1, 2, 8, 2, 5, 2, 9, 1};
		int n = 2;
		System.out.println("The last occurrence of " + n + " was in position " + lastOccurrence(v, n) + "");
		
		int n2 = 3;
		System.out.println("The last occurrence of " + n2 + " was in position " + lastOccurrence(v, n2) + "");
	}
}
