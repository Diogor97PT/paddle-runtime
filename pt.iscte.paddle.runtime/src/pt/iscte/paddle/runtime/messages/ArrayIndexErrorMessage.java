package pt.iscte.paddle.runtime.messages;

import pt.iscte.paddle.interpreter.ArrayIndexError;
import pt.iscte.paddle.interpreter.IArray;
import pt.iscte.paddle.interpreter.IReference;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IArrayIndexIterator;
import pt.iscte.paddle.model.roles.IVariableRole;
import pt.iscte.paddle.runtime.Runtime;
import pt.iscte.paddle.runtime.variableInfo.ArrayVariableInfo;

public class ArrayIndexErrorMessage extends ErrorMessage {
	
	private ArrayIndexError error;
	private IArray array_ref;
	private int [] errorCoordinates;

	public ArrayIndexErrorMessage(HyperlinkedText text, Runtime runtime, ArrayIndexError error) {
		super(text, runtime);
		
		this.error = error;
		
		int invalidPos = error.getInvalidIndex();
		IExpression errorExpression = error.getIndexExpression();
		IVariableDeclaration variable = getVariableFromExpression(errorExpression).getVariable();
		
		IVariableDeclaration array;									//Error array or matrix
		if(error.getTarget() instanceof IArrayElement)
			array = getVariableFromExpression(((IArrayElement)error.getTarget()).getTarget()).getVariable();
		else
			array = ((IVariableExpression)error.getTarget()).getVariable();
		
		ArrayVariableInfo arrayInfo = (ArrayVariableInfo) runtime.getVarValues().get(array);
		
		Object obj = arrayInfo.getReference().getValue();
		if(obj instanceof IArray)
			array_ref = (IArray) obj;
		else
			array_ref = (IArray)((IReference)obj).getValue();
		
		if(arrayInfo.getNumberOfDimensions() == 1) {
			setCoordinates(error.getInvalidIndex());
			
			text.words("Tentativa de acesso à posição ")
				.words(Integer.toString(invalidPos))
				.words(", que é inválida para o ")
				.link("vetor " + array.getId(), array)
				.words(" (comprimento " + array_ref.getLength() + ", índices válidos [0, " + (array_ref.getLength() - 1) + "]). ")
				.newline()
				.words("O acesso foi feito através da ")
				.link("variável " + variable.toString(), errorExpression);

			IVariableRole role = IVariableRole.match(variable);
			if(role instanceof IArrayIndexIterator && ((IArrayIndexIterator) role).getArrayVariables().contains(array)) {
				text.words(", que é um iterador para as posições do vetor " + array);
			}
		} else {
			text.words("Tentativa de acesso à posição ")
				.words(Integer.toString(invalidPos))
				.words(" de dimensão " + error.getIndexDimension())
				.words(", que é inválida para a ")
				.link("matriz " + array.getId(), array)
				.newline();
			
			if(error.getIndexDimension() == 1) {		//Dimensão da array que deu erro = 1
				IArrayElement arrayElement = (IArrayElement) error.getTarget();
				int index = runtime.getIntValueFromExpression(arrayElement.getIndexes().get(0));
				
				setCoordinates(index, error.getInvalidIndex());
			} else {
				setCoordinates(error.getInvalidIndex());
			}
			
			text.words(" (comprimento da dimensão " + error.getIndexDimension() + ": " + array_ref.getLength() + ", índices válidos [0, " + (array_ref.getLength() - 1) + "]). ")			
				.newline()
				.words("O acesso inválido foi feito através da ")
				.link("variável " + variable.toString(), errorExpression);
			
			IVariableRole role = IVariableRole.match(variable);
			if(role instanceof IArrayIndexIterator && ((IArrayIndexIterator) role).getArrayVariables().contains(array)) {
				text.words(", que é um iterador para as posições da dimensão " + error.getIndexDimension() + " da matriz " + array);
			}
		}
	}
	
	private void setCoordinates(int ... coordinates) {
		errorCoordinates = coordinates;
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
		if(error.getTarget() instanceof IVariableExpression)
			return ((IVariableExpression)error.getTarget()).getVariable();
		else
			return getVariableFromExpression(((IArrayElement)error.getTarget()).getTarget()).getVariable();
	}
	
	public int [] getErrorCoordinates() {
		return errorCoordinates;
	}
	
	public int getArraySize() {
		return array_ref.getLength();
	}
}
