package pt.iscte.paddle.runtime.messages;

import pt.iscte.paddle.interpreter.ArrayIndexError;
import pt.iscte.paddle.interpreter.IArray;
import pt.iscte.paddle.javardise.Decoration;
import pt.iscte.paddle.javardise.MarkerService;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IArrayIndexIterator;
import pt.iscte.paddle.model.roles.IVariableRole;
import pt.iscte.paddle.runtime.Runtime;

public class ArrayIndexErrorMessage extends Message {
	
	private ArrayIndexError e;
	
	public ArrayIndexErrorMessage(HyperlinkedText text, ArrayIndexError e, Runtime runtime) {
		super(text, runtime);
		
		this.e = e;
		
		int invalidPos = e.getInvalidIndex();
		IVariableDeclaration variable = ((IVariableExpression)e.getIndexExpression()).getVariable();
		IVariableDeclaration array = ((IVariableExpression)e.getTarget()).getVariable();
		//int arrayDimension = e.getIndexDimension();	//Dimensão da array que deu erro
		
		IArray array_ref = (IArray)runtime.getReferences().get(array).getValue();
		
		text.words("Tentativa de acesso à posição ")
			.words(Integer.toString(invalidPos))
			.words(", que é inválida para o ")
			.link("vetor " + array.getId(), array)
			.words(" (comprimento " + array_ref.getLength() + ", índices válidos [0, " + (array_ref.getLength() - 1) + "]. ")
			.newline()
			.words("O acesso foi feito através da ")
			.link("variável i", variable);
		
		IVariableRole role = IVariableRole.match(variable);
		if(role instanceof IArrayIndexIterator && ((IArrayIndexIterator) role).getArrayVariables().contains(array)) {
			text.words(", que é um iterador para as posições do vetor " + array);
		}
	}

	@Override
	public Decoration generateShortText() {
		return MarkerService.addDecoration(e.getSourceElement(), "Tentativa de acesso a posição inválida", Decoration.Location.RIGHT);
	}
}
