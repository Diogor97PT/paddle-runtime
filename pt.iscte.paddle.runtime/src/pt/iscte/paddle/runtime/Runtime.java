package pt.iscte.paddle.runtime;

import pt.iscte.paddle.interpreter.ArrayIndexError;
import pt.iscte.paddle.interpreter.ExecutionError;
import pt.iscte.paddle.interpreter.IExecutionData;
import pt.iscte.paddle.interpreter.IMachine;
import pt.iscte.paddle.interpreter.IProgramState;
import pt.iscte.paddle.interpreter.IProgramState.IListener;
import pt.iscte.paddle.interpreter.IValue;
import pt.iscte.paddle.javardise.MarkerService;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock.IVisitor;
import pt.iscte.paddle.model.IModel2CodeTranslator;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IArrayIndexIterator;
import pt.iscte.paddle.model.roles.IVariableRole;
import pt.iscte.paddle.runtime.RuntimeWindow.InterfaceColor;
import pt.iscte.paddle.runtime.tests.Test;

public class Runtime {
	
	private IModule module;
	private IProcedure procedure;
	private IProgramState state;
	
	public Runtime(Test test) {
		module = test.getModule();
		procedure = test.getProcedure();
		
		String code = module.translate(new IModel2CodeTranslator.Java());
		System.out.println(code);
		
		state = IMachine.create(module);
	}
	
	public void addListener() {
		state.addListener(new IListener() {
			@Override
			public void step(IProgramElement statement) {
				if(statement instanceof IVariableAssignment) {
					IVariableAssignment a = (IVariableAssignment) statement;
					System.out.println(state.getCallStack().getTopFrame().getVariableStore(a.getTarget()));
				}
			}
		});
		
	}
	
	public HyperlinkedText execute() {
		HyperlinkedText text = new HyperlinkedText(e1 -> MarkerService.mark(InterfaceColor.BLUE.getColor(), e1));
		
		try {
			IExecutionData data = state.execute(procedure, 5);	//naturals(5)
			IValue value = data.getReturnValue();
			System.out.println("\n" + "RESULT: " + value);
			text.line("RESULT: " + value);
		} catch (ArrayIndexError e) {
			//System.out.println(generateArrayIndexError(e));
			
			generateArrayIndexError(e, text);
		} catch (ExecutionError e) {
			System.err.println("EXCEPTION NOT HANDLED YET");
			e.printStackTrace();
		}
		
		return text;
	}
	
	public HyperlinkedText generateArrayIndexError(ArrayIndexError e, HyperlinkedText text) {
		int invalidPos = e.getInvalidIndex();
		IVariableDeclaration variable = ((IVariableExpression)e.getIndexExpression()).getVariable();
		IVariableDeclaration array = ((IVariableExpression)e.getTarget()).getVariable();
		int arrayDimension = e.getIndexDimension();	//Dimensão da array que deu erro
		
		array.getOwnerProcedure().accept(new IVisitor() {
			@Override
			public boolean visit(IArrayElementAssignment assignment) {
				return IVisitor.super.visit(assignment);
			}
		});
		
		IProgramElement exceptionPlace = e.getSourceElement();
		System.out.println(e.getSourceElement());
		
		String tamanhoArray = "Não_implementado";
		
		text.words("Tentativa de acesso à posição ")
			.words(Integer.toString(invalidPos))
			.words(", que é inválida para o ")
			.link("vetor " + array.getId(), array)
			.words(" (comprimento " + arrayDimension + ", índices válidos [0, " + tamanhoArray + "]. ")	//retirar que o comprimento é a dimensão da array
			.newline()
			.words("O acesso foi feito através da ")
			.link("variável i", variable);
		
		IVariableRole role = IVariableRole.match(variable);
		if(role instanceof IArrayIndexIterator) {
			text.words(", que é um iterador para as posições do vetor " + array);
		}
		
		text.words(" | Teste ").link("TTTTTT", exceptionPlace);
		return text;
	}
	
	public IModule getModule() {
		return module;
	}
	
	public static void main(String[] args) {
		Test test = new Test();
		Runtime runtime = new Runtime(test);
		runtime.addListener();
		new RuntimeWindow(runtime);
	}
}
