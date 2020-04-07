package pt.iscte.paddle.runtime;

import java.util.HashMap;
import java.util.Map;

import pt.iscte.paddle.interpreter.ExecutionError;
import pt.iscte.paddle.interpreter.IExecutionData;
import pt.iscte.paddle.interpreter.IMachine;
import pt.iscte.paddle.interpreter.IProgramState;
import pt.iscte.paddle.interpreter.IProgramState.IListener;
import pt.iscte.paddle.interpreter.IReference;
import pt.iscte.paddle.interpreter.IValue;
import pt.iscte.paddle.javardise.service.IJavardiseService;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.cfg.IControlFlowGraph;
import pt.iscte.paddle.runtime.messages.Message;
import pt.iscte.paddle.runtime.tests.ArrayIndexErrorBackwardTest;
import pt.iscte.paddle.runtime.tests.Test;

public class Runtime {
	
	private IModule module;
	private IProcedure procedure;
	private IProgramState state;
	private IControlFlowGraph icfg;
	
	private Map<IVariableDeclaration, IReference> varReferences = new HashMap<>();
	private Map<IVariableDeclaration, IReference> parameterReferences = new HashMap<>();
	
	public Runtime(Test test) {
		module = test.getModule();
		procedure = test.getProcedure();
		icfg = procedure.generateCFG();
		
//		String code = module.translate(new IModel2CodeTranslator.Java());
//		System.out.println(code);
		
		state = IMachine.create(module);
	}
	
	public void addListener() {
		state.addListener(new IListener() {
			
			@Override
			public void programStarted() {
				procedure.getParameters().forEach(var -> {
					IReference r = state.getCallStack().getTopFrame().getVariableStore(var);
					parameterReferences.put(var, r);
				});
			}
			
			@Override
			public void step(IProgramElement statement) {
				if(statement instanceof IVariableAssignment) {
					IVariableAssignment a = (IVariableAssignment) statement;
					IReference r = state.getCallStack().getTopFrame().getVariableStore(a.getTarget());
					varReferences.putIfAbsent(a.getTarget(), r);
				}
			}
		});
	}
	
	public Message execute() {
		HyperlinkedText text = new HyperlinkedText(e -> e.forEach(e2 -> IJavardiseService.getWidget(e2).addMark(InterfaceColors.BLUE.getColor()).show()));
		
		Message message = null;
		
		try {
			IExecutionData data = state.execute(procedure, 5);
			IValue value = data.getReturnValue();
			message = Message.getSuccessfulMessage(text, this, value);
		} catch (ExecutionError e) {
			message = Message.getErrorMessage(text, this, e);
			text.newline();
			message.addVarValuesToText();
		}
		
		return message;
	}
	
	public IModule getModule() {
		return module;
	}
	
	public IProcedure getProcedure() {
		return procedure;
	}
	
	public Map<IVariableDeclaration, IReference> getReferences() {
		return varReferences;
	}
	
	public Map<IVariableDeclaration, IReference> getParameterReferences() {
		return parameterReferences;
	}
	
	public IControlFlowGraph getIcfg() {
		return icfg;
	}
	
	public static void main(String[] args) {
//		ArrayIndexErrorTest test = new ArrayIndexErrorTest();
//		ArrayIndexErrorExpressionTest test = new ArrayIndexErrorExpressionTest();
		ArrayIndexErrorBackwardTest test = new ArrayIndexErrorBackwardTest();
		Runtime runtime = new Runtime(test);
		runtime.addListener();
		new RuntimeWindow(runtime);
	}
}
