package pt.iscte.paddle.runtime.demos;
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
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IStepper;
import pt.iscte.paddle.model.roles.IVariableRole;

public class DemoListener {

	public static void main(String[] args) throws ExecutionError {

		// instantiate model
		IModule module = IModule.create();
		module.setId("StepperTest");
		
		IProcedure procedure = module.addProcedure(INT.array().reference());
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
		
		//System.out.println(module);

		IProgramState state = IMachine.create(module);

		// Tracer ----
		state.addListener(new IListener() {
			public void step(IProgramElement statement) {
				if(statement instanceof IVariableAssignment) {
					IVariableAssignment a = (IVariableAssignment) statement;
					//if(a.getTarget().getId().equals("i")) {
					System.out.println(a.getTarget().getId() + state.getCallStack().getTopFrame().getVariableStore(a.getTarget()));
					//}
				}
			}
			
		});

		
//		for(IVariableDeclaration var : procedure.getVariables()) {
//			IVariableRole role = IVariableRole.match(var);
//			System.out.println(var.getId() + ": " + role);
//		}
		
		System.out.println("Modifications of variable i:");
		
		try {
			IExecutionData data = state.execute(procedure, 5);// naturals(5)
			
			IValue ret = data.getReturnValue();
			
			System.out.println();
			System.out.println("RESULT: " + ret);
		} catch (ArrayIndexError e) {
			int invalidPos = e.getInvalidIndex();
			String variable = ((IVariableExpression)e.getIndexExpression()).getVariable().getId();
			String arrayVariable = ((IVariableExpression)e.getTarget()).getVariable().getId();
			int arrayDimension = e.getIndexDimension();	//Dimensão da array que deu erro

			String tamanhoArray = "Não_implementado";

			StringBuilder sb = new StringBuilder("Tentativa de acesso à posição ");
			sb.append(invalidPos);
			sb.append(", que é inválida para o vetor ");
			sb.append(arrayVariable);
			sb.append(" (comprimento " + arrayDimension + ", índices válidos [0, " + tamanhoArray + "]. ");
			sb.append("O acesso foi feito através da variável ");
			sb.append(variable);

			System.out.println(procedure.getVariables());
			System.out.println(variable);
			
			IVariableRole role = IVariableRole.match(procedure.getVariable(variable));
			
			if(role instanceof IStepper) {
				sb.append(", que é um iterador para as posições do vetor " + arrayVariable);
			} else {
				sb.append(".");
			}

			System.out.println(sb.toString());
			
			/*System.out.println(e);
			System.out.println(invalidPos);
			System.out.println(variable);
			System.out.println(array);
			System.out.println(arrayDimension);*/
		} catch (ExecutionError e) {
			throw new ExecutionError(e.getType(), e.getSourceElement(), e.getMessage());
		}
	}
}