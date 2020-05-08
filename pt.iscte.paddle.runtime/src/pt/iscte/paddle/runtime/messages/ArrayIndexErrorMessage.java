package pt.iscte.paddle.runtime.messages;

import pt.iscte.paddle.interpreter.ArrayIndexError;
import pt.iscte.paddle.interpreter.IArray;
import pt.iscte.paddle.interpreter.IReference;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IArrayIndexIterator;
import pt.iscte.paddle.model.roles.IVariableRole;
import pt.iscte.paddle.runtime.Runtime;

public class ArrayIndexErrorMessage extends ErrorMessage {
	
	private ArrayIndexError error;
	private IArray array_ref;

	public ArrayIndexErrorMessage(HyperlinkedText text, Runtime runtime, ArrayIndexError error) {
		super(text, runtime);
		
		this.error = error;
		
		int invalidPos = error.getInvalidIndex();
		IExpression errorExpression = error.getIndexExpression();
		IVariableDeclaration variable = getVariableFromExpression(errorExpression).getVariable();				//TODO Testar se rebenta
		IVariableDeclaration array = ((IVariableExpression)error.getTarget()).getVariable();
		//int arrayDimension = e.getIndexDimension();	//Dimensão da array que deu erro
		
		Object obj = runtime.getVarValues().get(array).getReference().getValue();
		if(obj instanceof IArray)
			array_ref = (IArray) obj;
		else
			array_ref = (IArray)((IReference)obj).getValue();
		
		text.words("Tentativa de acesso à posição ")
			.words(Integer.toString(invalidPos))
			.words(", que é inválida para o ")
			.link("vetor " + array.getId(), array)
			.words(" (comprimento " + array_ref.getLength() + ", índices válidos [0, " + (array_ref.getLength() - 1) + "]. ")
			.newline()
			.words("O acesso foi feito através da ")
			.link("variável " + variable.toString(), errorExpression);
		
		IVariableRole role = IVariableRole.match(variable);
		if(role instanceof IArrayIndexIterator && ((IArrayIndexIterator) role).getArrayVariables().contains(array)) {
			text.words(", que é um iterador para as posições do vetor " + array);
		}
	}

	@Override
	public String getShortText() {
		return "Posição inválida";
	}
	
	@Override
	public IProgramElement getErrorElement() {
		return error.getSourceElement();
	}
	
	@Override
	public IExpression getErrorExpression() {
		return error.getIndexExpression();
	}
	
	@Override
	public IVariableDeclaration getErrorTarget() {
		return ((IVariableExpression)error.getTarget()).getVariable();
	}
	
	public int getErrorIndex() {
		return error.getInvalidIndex();
	} 
	
	public int getArraySize() {
		return array_ref.getLength();
	}
}
