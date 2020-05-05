package pt.iscte.paddle.runtime.messages;

import pt.iscte.paddle.interpreter.NullPointerError;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.Runtime;

public class NullPointerErrorMessage extends ErrorMessage {
	
	private NullPointerError error;

	public NullPointerErrorMessage(HyperlinkedText text, Runtime runtime, NullPointerError error) {
		super(text, runtime);
		
		this.error = error;
		
		text.words("");
	}
	
	@Override
	public String getShortText() {
		return "Variável null";
	}

	@Override
	public IProgramElement getErrorElement() {
		return error.getSourceElement();
	}

	@Override
	public IExpression getErrorExpression() {
		return null;
	}

	@Override
	public IVariableDeclaration getErrorTarget() {
		return null;
	}
}
