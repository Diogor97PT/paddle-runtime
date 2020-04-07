package pt.iscte.paddle.runtime.messages;

import pt.iscte.paddle.interpreter.ArrayIndexError;
import pt.iscte.paddle.interpreter.IArray;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IArrayIndexIterator;
import pt.iscte.paddle.model.roles.IVariableRole;
import pt.iscte.paddle.runtime.Runtime;

class ArrayIndexErrorMessage extends ErrorMessage {
	
	private ArrayIndexError error;

	public ArrayIndexErrorMessage(HyperlinkedText text, Runtime runtime, ArrayIndexError error) {
		super(text, runtime);
		
		this.error = error;
		
		int invalidPos = error.getInvalidIndex();
		IExpression errorExpression = error.getIndexExpression();
		IVariableDeclaration variable = getVariableFromExpression(errorExpression).getVariable();				//TODO Testar se rebenta
		IVariableDeclaration array = ((IVariableExpression)error.getTarget()).getVariable();
		//int arrayDimension = e.getIndexDimension();	//Dimensão da array que deu erro
		
		IArray array_ref = (IArray)runtime.getReferences().get(array).getValue();
		
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
		return "Tentativa de acesso a posição inválida";		//adicionar codigo que deu erro por exemplo neste caso: array[5] (meter mesmo o valor)
	}
	
	@Override
	public IProgramElement getErrorElement() {
//		System.out.println(error.getSourceElement());
		return error.getSourceElement();
	}
	
	public IExpression getErrorExpression() {
//		System.out.println(((IVariableExpression)error.getIndexExpression()).getVariable());
		return error.getIndexExpression();
	}
}
