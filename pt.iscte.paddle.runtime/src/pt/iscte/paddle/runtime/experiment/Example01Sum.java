package pt.iscte.paddle.runtime.experiment;

public class Example01Sum {
	
	//Example 1
	public static int sum(int [] v) {
		int i = 0;
		int sum = 0;
		while(i <= v.length) {			//switch <= with <
			sum = sum + v[i];
			i = i + 1;
		}
		return sum;
	}
	
	public static void main(String[] args) {
		int [] array = {1, 3, 5, 10};
		System.out.println("The sum is " + sum(array));
	}
}
