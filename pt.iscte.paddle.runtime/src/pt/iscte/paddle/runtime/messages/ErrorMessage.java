package pt.iscte.paddle.runtime.messages;

import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.runtime.Runtime;

public abstract class ErrorMessage extends Message {
	
	public ErrorMessage(HyperlinkedText text, Runtime runtime) {
		super(text, runtime);
	}

	public abstract IProgramElement getErrorElement();
	
	public abstract IExpression getErrorExpression();
	
	public abstract IVariableDeclaration getErrorTarget();
	
	public abstract String getShortText();
	
	public static IVariableExpression getVariableFromExpression(IExpression exp) {	//TODO transformar a função para mais do que uma variável?
		if(exp.getNumberOfParts() == 0)
			return (IVariableExpression) exp;
		
		for(IExpression possibleVar : exp.getParts()) {								
			if(possibleVar instanceof IVariableExpression) {
				return (IVariableExpression) possibleVar;
			}
		}
		return null;																//null se não existir variáveis
	}
}
