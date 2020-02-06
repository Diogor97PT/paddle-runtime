package pt.iscte.paddle.runtime;
import java.io.File;

import pt.iscte.paddle.interpreter.ArrayIndexError;
import pt.iscte.paddle.interpreter.ExecutionError;
import pt.iscte.paddle.interpreter.IExecutionData;
import pt.iscte.paddle.interpreter.IMachine;
import pt.iscte.paddle.interpreter.IProgramState;
import pt.iscte.paddle.interpreter.IProgramState.IListener;
import pt.iscte.paddle.interpreter.IValue;
import pt.iscte.paddle.javali.translator.Translator;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.roles.IVariableRole;
import pt.iscte.paddle.runtime.roles.IStepper;

public class DemoListener {

	public static void main(String[] args) throws ExecutionError {

		// instantiate model from file
		Translator translator = new Translator(new File("MeuFicheiro.javali").getAbsolutePath());
		IModule module = translator.createProgram();
		IProcedure nats = module.getProcedures().iterator().next(); // first
		
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

		
		for(IVariable i : nats.getVariables()) {
			if(IStepper.isStepper(i)) {
				IVariableRole vr = IStepper.createStepper(i);
				System.out.println(i + " : " + vr);	
			} else 
				System.out.println(i + " : not a Stepper");
		}
		
		System.out.println("Modifications of variable i:");
		
		try {
			IExecutionData data = state.execute(nats, 5);// naturals(5)
			
			IValue ret = data.getReturnValue();
			
			System.out.println();
			System.out.println("RESULT: " + ret);
		} catch (ArrayIndexError e) {
			int invalidPos = e.getInvalidIndex();
			String variable = e.getIndexExpression().getId();
			String array = e.getTarget().getId();
			//int arrayDimension = e.getIndexDimension();	//Dimensão da array que deu erro
			
			if(invalidPos < 0) {	//tentou aceder a uma posição da array menor que 0
				//System.err.println("Tentativa de acesso a uma posição inferior a 0.");
				System.err.println("A variável " + variable  + "=" + invalidPos + " refere-se a uma posição inferior à menor posição possível, 0.");
				//System.err.println("No Java, assim como em grande parte das linguagens de programação, o os vetores começam pela posição 0.");
				System.err.println("");
			} else {	//posição é maior do que o tamanho da array
				System.err.println("Tentativa de acesso a uma posição superior ao tamanho do vetor.");
				System.err.println("A variável " + variable  + "=" + invalidPos + " refere-se a uma posição superior ao tamanho da array " + array 
						+ " em " + array + "[" + variable + "].");
			}
			
			//prints
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