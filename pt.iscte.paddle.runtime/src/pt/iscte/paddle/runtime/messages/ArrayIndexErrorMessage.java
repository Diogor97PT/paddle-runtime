package pt.iscte.paddle.runtime.messages;

import java.util.HashMap;
import java.util.Map;

import pt.iscte.paddle.interpreter.ArrayIndexError;
import pt.iscte.paddle.interpreter.IArray;
import pt.iscte.paddle.interpreter.IReference;
import pt.iscte.paddle.javardise.Decoration;
import pt.iscte.paddle.javardise.MarkerService;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IArrayIndexIterator;
import pt.iscte.paddle.model.roles.IVariableRole;
import pt.iscte.paddle.runtime.Runtime;

public class ArrayIndexErrorMessage implements IMessage {
	
	private HyperlinkedText text;
	private ArrayIndexError e;
	private Map<IVariableDeclaration, String> varValues = new HashMap<>();
	
	public ArrayIndexErrorMessage(HyperlinkedText text, ArrayIndexError e, Runtime runtime) {
		this.text = text;
		this.e = e;
		
		Map<IVariableDeclaration, IReference> references = runtime.getReferences();
		
		int invalidPos = e.getInvalidIndex();
		IVariableDeclaration variable = ((IVariableExpression)e.getIndexExpression()).getVariable();
		IVariableDeclaration array = ((IVariableExpression)e.getTarget()).getVariable();
		//int arrayDimension = e.getIndexDimension();	//Dimensão da array que deu erro
		
		IArray array_ref = (IArray)references.get(array).getValue();
		
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
		
		text.newline().line("Valores das variáveis quando ocorreu a Exceção:");
		
		references.forEach((key, value) -> {
			text.link(key.toString(), key);
			text.line(" : " + value.getValue());
			
			varValues.put(key, value.getValue().toString());
		});
	}

	@Override
	public HyperlinkedText getText() {
		return text;
	}

	@Override
	public void generateShortText() {
		IProgramElement exceptionPlace = e.getSourceElement();
		Decoration d = MarkerService.addDecoration(exceptionPlace, "Tentativa de acesso a posição inválida", Decoration.Location.RIGHT);
		d.show();
	}

	@Override
	public Map<IVariableDeclaration, String> getVarValues() {
		return varValues;
	}
	
	
}
