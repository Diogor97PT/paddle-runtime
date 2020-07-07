package pt.iscte.paddle.runtime.experiment;

public class Example00 {
	
	public static boolean example(int [] v, int n) {
		int i = 0;
		while(i <= v.length) {			//switch <= with <
			if (n == v[i]) {
				return true;
			}
			i = i + 1;
		}
		return false;
	}
	
	public static void main(String[] args) {
		int [] array = {1, 2};
		int n = 3;
		boolean contains = example(array, n);
		if(contains)
			System.out.println(n + " is in the array");
		else {
			System.out.println(n + " is not in the array");
		}
	}
}
