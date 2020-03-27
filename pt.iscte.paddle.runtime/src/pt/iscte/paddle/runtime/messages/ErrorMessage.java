package pt.iscte.paddle.runtime.messages;

import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.runtime.Runtime;

public abstract class ErrorMessage extends Message {

	public ErrorMessage(HyperlinkedText text, Runtime runtime) {
		super(text, runtime);
	}

	public abstract IProgramElement getErrorElement();
	
	public abstract String getShortText();
}
