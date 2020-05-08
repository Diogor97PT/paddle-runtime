package pt.iscte.paddle.runtime.variableInfo;

import java.util.ArrayList;

import pt.iscte.paddle.interpreter.IReference;
import pt.iscte.paddle.model.IVariableDeclaration;

public class VariableInfo {
	
	private IVariableDeclaration variableDeclaration;
	private ArrayList<String> varValues = new ArrayList<>();
	private VariableType variableType;
	private IReference reference;
	
	public VariableInfo(IVariableDeclaration variableDeclaration, VariableType variableType, IReference reference, String value) {
		this.variableDeclaration = variableDeclaration;
		this.variableType = variableType;
		this.reference = reference;
		varValues.add(value);
	}
	
	public void addVarValue(String value) {
		varValues.add(value);
	}
	
	public IVariableDeclaration getVariableDeclaration() {
		return variableDeclaration;
	}
	
	public VariableType getVariableType() {
		return variableType;
	}
	
	public IReference getReference() {
		return reference;
	}
	
	public ArrayList<String> getVarValues() {
		return varValues;
	}
	
	public enum VariableType {
		PARAMETER, LOCAL_VARIABLE;
	}
}
