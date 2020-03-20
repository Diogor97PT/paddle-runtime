package pt.iscte.paddle.runtime.messages;

import java.util.HashMap;
import java.util.Map;

import pt.iscte.paddle.interpreter.ArrayIndexError;
import pt.iscte.paddle.interpreter.ExecutionError;
import pt.iscte.paddle.javardise.Decoration;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.Runtime;

public abstract class Message {
	
	private HyperlinkedText text;
	private Map<IVariableDeclaration, String> varValues = new HashMap<>();
	
	public Message(HyperlinkedText text, Runtime runtime) {
		this.text = text;
		
		runtime.getReferences().forEach((key, value) -> {
			varValues.put(key, value.getValue().toString());
		});
	}

	public static Message getMessage(HyperlinkedText text, ExecutionError e, Runtime runtime) {
		
		if(e instanceof ArrayIndexError) {
			return new ArrayIndexErrorMessage(text, (ArrayIndexError)e, runtime);
		}
		
		return null;
	}
	
	public void addVarValuesToText() {
		text.line("Valores das variáveis quando ocorreu a Exceção:");
		
		varValues.forEach((key, value) -> {
			text.link(key.toString(), key);
			text.line(" : " + value);
		});
	}
	
	public HyperlinkedText getText() {
		return text;
	}
	
	public Map<IVariableDeclaration, String> getVarValues() {
		return varValues;
	}
	
	public abstract Decoration generateShortText();
	
//	IMessage EMPTY = new IMessage() {
//		
//		@Override
//		public HyperlinkedText getText() {
//			HyperlinkedText text = new HyperlinkedText(e1 -> MarkerService.mark(InterfaceColor.BLUE.getColor(), e1));
//			return text.line("Empty Message");
//		}
//		
//		@Override
//		public void generateShortText() {}
//
//		@Override
//		public Map<IVariableDeclaration, String> getVarValues() {
//			return null;
//		}
//	};
	
}
