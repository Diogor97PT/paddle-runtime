package pt.iscte.paddle.runtime.messages;

import pt.iscte.paddle.interpreter.IValue;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.runtime.Runtime;

class SuccessfulMessage extends Message {
	
	public SuccessfulMessage(HyperlinkedText text, Runtime runtime, IValue value) {
		super(text, runtime);
		
		text.line("Código executado corretamente.");
		text.line("Resultado da execução:");
		text.line(value.toString());
	}

	@Override
	public String getShortText() {
		return "Código executado corretamente.";
	}

	@Override
	public IProgramElement getProgramElement() {
		return null;
	}
	
	
}
