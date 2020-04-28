package pt.iscte.paddle.runtime.messages;

import java.util.Map;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import pt.iscte.paddle.interpreter.ArrayIndexError;
import pt.iscte.paddle.interpreter.ExecutionError;
import pt.iscte.paddle.interpreter.IReference;
import pt.iscte.paddle.interpreter.IValue;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.Runtime;

public abstract class Message {
	
	private HyperlinkedText text;
	private Map<IVariableDeclaration, IReference> varReferences;
	private ListMultimap<IVariableDeclaration, String> varValues = MultimapBuilder.hashKeys().arrayListValues().build();
	private Map<IVariableDeclaration, IReference> parameterReferences;
	
	public Message(HyperlinkedText text, Runtime runtime) {
		this.text = text;
		varReferences = runtime.getVarReferences();
		varValues = runtime.getVarValues();
		parameterReferences = runtime.getParameterReferences();
	}

	public static Message getErrorMessage(HyperlinkedText text, Runtime runtime, ExecutionError e) {
		
		if(e instanceof ArrayIndexError) {
			return new ArrayIndexErrorMessage(text, runtime, (ArrayIndexError)e);
		}
		
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
	
	public Map<IVariableDeclaration, IReference> getVarReferences() {
		return varReferences;
	}
	
	public ListMultimap<IVariableDeclaration, String> getVarValues() {
		return varValues;
	}
	
	public Map<IVariableDeclaration, IReference> getParameterReferences() {
		return parameterReferences;
	}
}
