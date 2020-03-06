package pt.iscte.paddle.runtime;

import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.SMALLER_EQ;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.interpreter.ArrayIndexError;
import pt.iscte.paddle.interpreter.ExecutionError;
import pt.iscte.paddle.interpreter.IExecutionData;
import pt.iscte.paddle.interpreter.IMachine;
import pt.iscte.paddle.interpreter.IProgramState;
import pt.iscte.paddle.interpreter.IProgramState.IListener;
import pt.iscte.paddle.interpreter.IValue;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IStepper;
import pt.iscte.paddle.model.roles.IVariableRole;

public class ExecutionErrorChecker {
	
	private IModule module;
	private IProcedure procedure;
	private IProgramState state;
	
	public ExecutionErrorChecker() {
		//Initialize Environment
		
		module = IModule.create();
		module.setId("StepperTest");
		
		procedure = module.addProcedure(INT.array().reference());
		procedure.setId("naturals");
		
		IVariableDeclaration n = procedure.addParameter(INT);
		n.setId("n");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration array = body.addVariable(INT.array().reference());
		array.setId("array");
		body.addAssignment(array, INT.array().heapAllocation(n));
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER_EQ.on(i, n));
		loop.addArrayElementAssignment(array, ADD.on(i, INT.literal(1)), i);
		loop.addAssignment(i, ADD.on(i, INT.literal(1)));
		
		body.addReturn(array);
		
		state = IMachine.create(module);
	}
	
	public void addListener() {
		
		state.addListener(new IListener() {
			@Override
			public void step(IProgramElement currentInstruction) {
				IListener.super.step(currentInstruction);
			}
			
			/*@Override
			public void executionError(ExecutionError e) {
				switch (e.getType()) {
				case ARRAY_INDEX_BOUNDS:
					System.out.println(e);
					break;
				default:
					e.printStackTrace();
					break;
				}
			}*/
			
			/*@Override
			public void infiniteLoop() {	
				//IListener.super.infiniteLoop();
				System.out.println("Nice loop mate");
			}*/
		});
		
	}
	
	public void printDebugStuff() {
		for(IVariableDeclaration var : procedure.getVariables()) {
			IVariableRole role = IVariableRole.match(var);
			System.out.println(var.getId() + ": " + role);
		}
	}
	
	public void execute() {
		
		try {
			IExecutionData data = state.execute(procedure, 5);	//naturals(5)
			
			IValue value = data.getReturnValue();
			
			System.out.println("\n" + "RESULT: " + value);
		} catch (ArrayIndexError e) {
			System.out.println(generateArrayErrorString(e));
		} catch (ExecutionError e) {
			System.err.println("EXCEPTION NOT HANDLED YET");
			e.printStackTrace();
		}
	}
	
	public String generateArrayErrorString(ArrayIndexError e) {
		int invalidPos = e.getInvalidIndex();
		String variable = ((IVariableExpression)e.getIndexExpression()).getVariable().getId();
		String array = ((IVariableExpression)e.getTarget()).getVariable().getId();
		int arrayDimension = e.getIndexDimension();	//Dimensão da array que deu erro

		String tamanhoArray = "Não_implementado";

		StringBuilder sb = new StringBuilder("Tentativa de acesso à posição ");
		sb.append(invalidPos);
		sb.append(", que é inválida para o vetor ");
		sb.append(array);
		sb.append(" (comprimento " + arrayDimension + ", índices válidos [0, " + tamanhoArray + "]. ");
		sb.append("O acesso foi feito através da variável ");
		sb.append(variable);

		System.out.println(procedure.getVariables());
		System.out.println(variable);
		
		IVariableRole role = IVariableRole.match(procedure.getVariable(variable));
		
		if(role instanceof IStepper) {
			sb.append(", que é um iterador para as posições do vetor " + array);
		} else {
			sb.append(".");
		}

		return sb.toString();
	}

	public static void main(String[] args) throws ExecutionError {
		ExecutionErrorChecker ec = new ExecutionErrorChecker();
		ec.addListener();
		ec.printDebugStuff();
		ec.execute();
	}
}
