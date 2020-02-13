package pt.iscte.paddle.runtime;

import java.io.File;

import pt.iscte.paddle.interpreter.ArrayIndexError;
import pt.iscte.paddle.interpreter.ExecutionError;
import pt.iscte.paddle.interpreter.IExecutionData;
import pt.iscte.paddle.interpreter.IMachine;
import pt.iscte.paddle.interpreter.IProgramState;
import pt.iscte.paddle.interpreter.IValue;
import pt.iscte.paddle.interpreter.IProgramState.IListener;
import pt.iscte.paddle.javali.translator.Translator;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.roles.IVariableRole;
import pt.iscte.paddle.runtime.roles.IStepper;

public class ExecutionErrorChecker {
	
	private Translator translator;
	private IModule module;
	private IProcedure procedure;
	private IProgramState state;
	
	public ExecutionErrorChecker() {
		//Initialize Environment
		translator = new Translator(new File("MeuFicheiro.javali").getAbsolutePath());
		module = translator.createProgram();
		procedure = module.getProcedures().iterator().next();	//Loads first procedure in class
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
		for(IVariable i : procedure.getVariables()) {
			if(IStepper.isStepper(i)) {
				IVariableRole vr = IStepper.createStepper(i);
				System.out.println(i + " : " + vr);	
			} else 
				System.out.println(i + " : not a Stepper");
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
		String variable = e.getIndexExpression().getId();
		String array = e.getTarget().getId();
		int arrayDimension = e.getIndexDimension();	//Dimensão da array que deu erro

		String tamanhoArray = "Não_implementado";

		StringBuilder sb = new StringBuilder("Tentativa de acesso à posição ");
		sb.append(invalidPos);
		sb.append(", que é inválida para o vetor ");
		sb.append(array);
		sb.append(" (comprimento " + arrayDimension + ", índices válidos [0, " + tamanhoArray + "]. ");
		sb.append("O acesso foi feito através da variável ");
		sb.append(variable);

		if(IStepper.isStepper(procedure.getVariable(variable))) {
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
