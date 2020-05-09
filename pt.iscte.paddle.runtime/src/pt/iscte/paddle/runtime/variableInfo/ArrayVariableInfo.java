package pt.iscte.paddle.runtime.variableInfo;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.interpreter.IReference;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IVariableDeclaration;

public class ArrayVariableInfo extends VariableInfo {

	private List<Integer> accessedPositions = new ArrayList<>();
	private IExpression lengthExpression;
	
	public ArrayVariableInfo(IVariableDeclaration variableDeclaration, VariableType variableType, IReference reference, IExpression lengthExpression, String value) {
		super(variableDeclaration, variableType, reference, value);
		this.lengthExpression = lengthExpression;
	}
	
	public void addArrayAccessInformation(String value, int i) {
		accessedPositions.add(i);
		super.addVarValue(value);
	}

	public List<Integer> getAccessedPositions() {
		return accessedPositions;
	}
	
	public IExpression getLengthExpression() {
		return lengthExpression;
	}
}
