package pt.iscte.paddle.runtime.messages;

import pt.iscte.paddle.interpreter.IValue;
import pt.iscte.paddle.javardise.Decoration;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.runtime.Runtime;

public class SuccessfulMessage extends Message {

	public SuccessfulMessage(HyperlinkedText text, Runtime runtime, IValue value) {
		super(text, runtime);
		
		text.line("Código executado corretamente.");
		text.line("Resultado da execução:");
		text.line(value.toString());
	}

	@Override
	public Decoration generateShortText() {
		return null;
	}
	
	
	
}
