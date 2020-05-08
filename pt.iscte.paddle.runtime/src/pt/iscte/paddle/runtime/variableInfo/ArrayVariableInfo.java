package pt.iscte.paddle.runtime.variableInfo;

import java.util.ArrayList;

import pt.iscte.paddle.interpreter.IReference;
import pt.iscte.paddle.model.IVariableDeclaration;

public class ArrayVariableInfo extends VariableInfo {

	private ArrayList<Integer> accessedPositions = new ArrayList<>();
	
	public ArrayVariableInfo(IVariableDeclaration variableDeclaration, VariableType variableType, IReference reference, String value) {
		super(variableDeclaration, variableType, reference, value);
	}
	
	public void addArrayAccessInformation(String value, int i) {
		accessedPositions.add(i);
		super.addVarValue(value);
	}

	public ArrayList<Integer> getAccessedPositions() {
		return accessedPositions;
	}
}
