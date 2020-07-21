package pt.iscte.paddle.runtime;

import pt.iscte.paddle.runtime.experiment.tests.Example00TestExplanation;
import pt.iscte.paddle.runtime.experiment.tests.Example00TestStackTrace;
import pt.iscte.paddle.runtime.experiment.tests.Example01SumTest;
import pt.iscte.paddle.runtime.experiment.tests.Example02NaturalsTest;
import pt.iscte.paddle.runtime.experiment.tests.Example03LastOccurrenceTest;
import pt.iscte.paddle.runtime.experiment.tests.Example04InvertTest;
import pt.iscte.paddle.runtime.experiment.tests.Example05ScaleMatrixTest;
import pt.iscte.paddle.runtime.experiment.tests.Example06TranposeMatrixTest;
import pt.iscte.paddle.runtime.experiment.tests.Example07InvertSameVectorTest;
import pt.iscte.paddle.runtime.experiment.tests.Example08BubbleSortTest;
import pt.iscte.paddle.runtime.tests.Test;

public enum Profile {
	
	A, B;
	
	public boolean isJavaStackTrace() {
		Test test = RuntimeWindow.test;
		
		if(test instanceof Example00TestStackTrace)
			return true;
		else if (test instanceof Example00TestExplanation)
			return false;
		
		if(test instanceof Example01SumTest || test instanceof Example03LastOccurrenceTest || 
				test instanceof Example05ScaleMatrixTest || test instanceof Example07InvertSameVectorTest) {
			if (this == A)
				return true;
			else
				return false;
		} else if (test instanceof Example02NaturalsTest || test instanceof Example04InvertTest || 
				test instanceof Example06TranposeMatrixTest || test instanceof Example08BubbleSortTest) {
			if (this == A)
				return false;
			else
				return true;
		}
		return false;
	}
	
	public String getJavaStackTraceExplanation() {
		Test test = RuntimeWindow.test;
		if(test instanceof Example00TestStackTrace) {
			return "Exception in thread \"main\" java.lang.ArrayIndexOutOfBoundsException: Index 2 out of bounds for length 2\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example00.example(Example00.java:4)\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example00.main(Example00.java:12)";
		} else if(test instanceof Example01SumTest) {
			return "Exception in thread \"main\" java.lang.ArrayIndexOutOfBoundsException: Index 4 out of bounds for length 4\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example01Sum.sum(Example01Sum.java:8)\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example01Sum.main(Example01Sum.java:20)";
		} else if(test instanceof Example02NaturalsTest) {
			return "Exception in thread \"main\" java.lang.ArrayIndexOutOfBoundsException: Index 5 out of bounds for length 5\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example02Naturals.naturals(Example02Naturals.java:9)\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example02Naturals.main(Example02Naturals.java:16)";
		} else if(test instanceof Example03LastOccurrenceTest) {
			return "Exception in thread \"main\" java.lang.ArrayIndexOutOfBoundsException: Index -1 out of bounds for length 8\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example03LastOccurrence.lastOccurrence(Example03LastOccurrence.java:6)\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example03LastOccurrence.main(Example03LastOccurrence.java:26)\r\n";
		} else if(test instanceof Example04InvertTest) {
			return "Exception in thread \"main\" java.lang.ArrayIndexOutOfBoundsException: Index 5 out of bounds for length 5\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example04Invert.invert(Example04Invert.java:8)\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example04Invert.main(Example04Invert.java:21)";
		} else if(test instanceof Example05ScaleMatrixTest) {
			return "Exception in thread \"main\" java.lang.ArrayIndexOutOfBoundsException: Index 3 out of bounds for length 3\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example05MultiplyMatrix.multiplyMatrix(Example05MultiplyMatrix.java:9)\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example05MultiplyMatrix.main(Example05MultiplyMatrix.java:38)\r\n";
		} else if(test instanceof Example06TranposeMatrixTest) {
			return "Exception in thread \"main\" java.lang.ArrayIndexOutOfBoundsException: Index 2 out of bounds for length 2\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example06TranposeMatrix.transposeMatrix(Example06TranposeMatrix.java:11)\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example06TranposeMatrix.main(Example06TranposeMatrix.java:27)";
		} else if(test instanceof Example07InvertSameVectorTest) {
			return "Exception in thread \"main\" java.lang.ArrayIndexOutOfBoundsException: Index 5 out of bounds for length 5\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example07InvertSameVector.swapElements(Example07InvertSameVector.java:13)\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example07InvertSameVector.invert(Example07InvertSameVector.java:6)\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example07InvertSameVector.main(Example07InvertSameVector.java:24)\r\n";
		} else if(test instanceof Example08BubbleSortTest) {
			return "Exception in thread \"main\" java.lang.ArrayIndexOutOfBoundsException: Index 12 out of bounds for length 12\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example08BubbleSort.bubbleSort(Example08BubbleSort.java:9)\r\n" + 
					"              at pt.iscte.paddle.runtime.experiment.Example08BubbleSort.main(Example08BubbleSort.java:31)\r\n";
		}
		
		return "";
	}

}
