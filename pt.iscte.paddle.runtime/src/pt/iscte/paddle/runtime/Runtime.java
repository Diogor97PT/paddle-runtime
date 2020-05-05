package pt.iscte.paddle.runtime;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import pt.iscte.paddle.interpreter.ExecutionError;
import pt.iscte.paddle.interpreter.IExecutionData;
import pt.iscte.paddle.interpreter.IMachine;
import pt.iscte.paddle.interpreter.IProgramState;
import pt.iscte.paddle.interpreter.IProgramState.IListener;
import pt.iscte.paddle.interpreter.IReference;
import pt.iscte.paddle.interpreter.IValue;
import pt.iscte.paddle.javardise.service.IJavardiseService;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlockElement;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IStatement;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.cfg.IControlFlowGraph;
import pt.iscte.paddle.runtime.messages.ErrorMessage;
import pt.iscte.paddle.runtime.messages.Message;
import pt.iscte.paddle.runtime.tests.ArrayIndexFunctionTest;
import pt.iscte.paddle.runtime.tests.Test;

public class Runtime {
	
	private IModule module;
	private IProcedure procedure;
	private IProgramState state;
	private IControlFlowGraph icfg;
	
	private Map<IVariableDeclaration, IReference> varReferences = new HashMap<>();
	private ListMultimap<IVariableDeclaration, String> varValues = MultimapBuilder.hashKeys().arrayListValues().build();
	private Map<IVariableDeclaration, IReference> parameterReferences = new HashMap<>();
	
	//-------------------------------------tests-------------------------------------//
//	Test test = new ArrayIndexErrorTest();
//	Test test = new ArrayIndexErrorExpressionTest();
//	Test test = new ArrayIndexErrorBackwardTest();
	Test test = new ArrayIndexFunctionTest();
//	Test test = new SumAllTest();
//	Test test = new NullPointerErrorTest();
	//-------------------------------------tests-------------------------------------//
	
	public Runtime() {
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
			public void step(IProgramElement statement) {
				if(statement instanceof IVariableAssignment) {					//Store variable state when it's assigned
					IVariableAssignment a = (IVariableAssignment) statement;
					IReference r = state.getCallStack().getTopFrame().getVariableStore(a.getTarget());
					varValues.put(a.getTarget(), r.getValue().toString());
					varReferences.putIfAbsent(a.getTarget(), r);
				} else if(statement instanceof IArrayElementAssignment) {		//Store arrays state when one of it's elements is changed
					IArrayElementAssignment a = (IArrayElementAssignment) statement;
					IVariableDeclaration var = ErrorMessage.getVariableFromExpression(a.getTarget()).getVariable();
					IReference r = state.getCallStack().getTopFrame().getVariableStore(var);
					varValues.put(var, r.getValue().toString());
					varReferences.putIfAbsent(var, r);
				}
				getReferences((IStatement)statement);
			}
		});
	}
	
	private void getReferences(IStatement statement) {
		IProgramElement block = statement.getParent();
		while(!(block instanceof IProcedure)) {
			block = ((IBlockElement)block).getParent();
		}
		IProcedure procedure = (IProcedure)block;
		
		procedure.getParameters().forEach(var -> {						//Store Parameter references
			IReference r = state.getCallStack().getTopFrame().getVariableStore(var);
			parameterReferences.putIfAbsent(var, r);
		});

		procedure.getVariables().forEach(var -> {						//Store Variable references
			IReference r = state.getCallStack().getTopFrame().getVariableStore(var);
			varReferences.putIfAbsent(var, r);
		});
	}
	
	public Message execute() {
		HyperlinkedText text = new HyperlinkedText(e -> e.forEach(e2 -> IJavardiseService.getWidget(e2).addMark(InterfaceColors.BLUE.getColor()).show()));
		
		Message message = null;
		
		try {
			IExecutionData data = state.execute(procedure, 20);
			IValue value = data.getReturnValue();
			message = Message.getSuccessfulMessage(text, this, value);
		} catch (ExecutionError e) {
			message = Message.getErrorMessage(text, this, e);
			text.newline();
//			message.addVarValuesToText();
		}
		return message;
	}
	
	public IModule getModule() {
		return module;
	}
	
	public IProcedure getProcedure() {
		return procedure;
	}
	
	public Map<IVariableDeclaration, IReference> getVarReferences() {
		return varReferences;
	}
	
	public ListMultimap<IVariableDeclaration, String> getVarValues() {
		return varValues;
	}
	
	public Map<IVariableDeclaration, IReference> getParameterReferences() {
		return parameterReferences;
	}
	
	public IControlFlowGraph getIcfg() {
		return icfg;
	}
	
	public static void main(String[] args) {
		Runtime runtime = new Runtime();
		runtime.addListener();
		new RuntimeWindow(runtime);
	}
}
