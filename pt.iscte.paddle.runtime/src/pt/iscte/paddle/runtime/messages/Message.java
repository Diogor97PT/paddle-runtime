package pt.iscte.paddle.runtime.messages;

import java.util.Map;

import pt.iscte.paddle.interpreter.ArrayIndexError;
import pt.iscte.paddle.interpreter.ExecutionError;
import pt.iscte.paddle.interpreter.IValue;
import pt.iscte.paddle.interpreter.NullPointerError;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.Runtime;
import pt.iscte.paddle.runtime.variableInfo.VariableInfo;

public abstract class Message {
	
	private HyperlinkedText text;
	private Map<IVariableDeclaration, VariableInfo> varValues;
	
	public Message(HyperlinkedText text, Runtime runtime) {
		this.text = text;
		varValues = runtime.getVarValues();
	}

	public static Message getErrorMessage(HyperlinkedText text, Runtime runtime, ExecutionError e) {
		
		if(e instanceof ArrayIndexError)
			return new ArrayIndexErrorMessage(text, runtime, (ArrayIndexError)e);
		else if(e instanceof NullPointerError)
			return new NullPointerErrorMessage(text, runtime, (NullPointerError)e);
		
		return null;
	}
	
	public static Message getSuccessfulMessage(HyperlinkedText text, Runtime runtime, IValue value) {
		return new SuccessfulMessage(text, runtime, value);
	}
	
//	public void addVarValuesToText() {
//		text.line("Valores das variáveis quando ocorreu a Exceção:");
//		
//		for(Map.Entry<IVariableDeclaration, Collection<String>> entry : varValues.asMap().entrySet()) {
//			String varValue = Iterables.getLast(entry.getValue());
//			text.link(entry.getKey().toString(), entry.getKey());
//			text.line(" : " + varValue);
//		}
//	}
	
	public HyperlinkedText getText() {
		return text;
	}
	
	public Map<IVariableDeclaration, VariableInfo> getVarValues() {
		return varValues;
	}
}
