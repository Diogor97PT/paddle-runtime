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
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IArrayType;
import pt.iscte.paddle.model.IBlockElement;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILiteral;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IStatement;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.cfg.IControlFlowGraph;
import pt.iscte.paddle.runtime.messages.ErrorMessage;
import pt.iscte.paddle.runtime.messages.Message;
import pt.iscte.paddle.runtime.tests.ArrayIndexErrorTest;
import pt.iscte.paddle.runtime.tests.Test;
import pt.iscte.paddle.runtime.variableInfo.ArrayVariableInfo;
import pt.iscte.paddle.runtime.variableInfo.VariableInfo;
import pt.iscte.paddle.runtime.variableInfo.VariableInfo.VariableType;

public class Runtime {
	
	private IModule module;
	private IProcedure procedure;
	private IProgramState state;
	private IControlFlowGraph icfg;
	
	private Map<IVariableDeclaration, VariableInfo> varValues = new HashMap<>();
	
	//-------------------------------------tests-------------------------------------//
	Test test = new ArrayIndexErrorTest();
//	Test test = new ArrayIndexErrorExpressionTest();
//	Test test = new ArrayIndexErrorBackwardTest();
//	Test test = new ArrayIndexFunctionTest();
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
	
	public void addListener() {					//TODO resolver caso sum = sum + array[i] em que nao sei as posições acedidas do array
		state.addListener(new IListener() {
			@Override
			public void step(IProgramElement statement) {
				IProcedure procedure = getProcedureFromStatement((IStatement)statement);
				
				procedure.getParameters().forEach(var -> {						//TODO Otimizar de maneira a verificar um procedure apenas uma vez
					IReference r = state.getCallStack().getTopFrame().getVariableStore(var);
					if(r.getType() instanceof IArrayType)
						varValues.putIfAbsent(var, new ArrayVariableInfo(var, VariableType.PARAMETER, r, r.getValue().toString()));
					else
						varValues.putIfAbsent(var, new VariableInfo(var, VariableType.PARAMETER, r, r.getValue().toString()));
				});
				
				if(statement instanceof IVariableAssignment ) {
					IVariableAssignment a = (IVariableAssignment) statement;
					IVariableDeclaration var = a.getTarget();
					IReference r = state.getCallStack().getTopFrame().getVariableStore(var);
					
					VariableType variableType = null;
					for(IVariableDeclaration parVar : procedure.getParameters()) {
						if(parVar.equals(var)) {
							variableType = VariableType.PARAMETER;
							break;
						}
					}
					if(variableType == null)
						variableType = VariableType.LOCAL_VARIABLE;
					
					if(r.getType() instanceof IArrayType) {
						varValues.putIfAbsent(var, new ArrayVariableInfo(var, variableType, r, r.getValue().toString()));
					} else {
						VariableInfo varInfo = varValues.putIfAbsent(var, new VariableInfo(var, variableType, r, r.getValue().toString()));
						if(varInfo != null)
							varInfo.addVarValue(r.getValue().toString());
					}
				} else if (statement instanceof IArrayElementAssignment) {
					IArrayElementAssignment a = (IArrayElementAssignment) statement;
					IVariableDeclaration var = ErrorMessage.getVariableFromExpression(a.getTarget()).getVariable();
					IReference r = state.getCallStack().getTopFrame().getVariableStore(var);
					
					int position = getIntValueFromExpression(a.getIndexes().get(0));  //TODO tornar multidimensional
					
					ArrayVariableInfo info = (ArrayVariableInfo) varValues.get(var);
					info.addArrayAccessInformation(r.getValue().toString(), position);
				}
			}
		});
	}
	
	private IProcedure getProcedureFromStatement(IStatement statement) {
		IProgramElement block = statement.getParent();
		while(!(block instanceof IProcedure)) {
			block = ((IBlockElement)block).getParent();
		}
		return (IProcedure)block;
	}
	
	private int getIntValueFromExpression(IExpression exp) {
		
		if(exp instanceof IVariableExpression) {
			return getIntValueFromIVariableExpression((IVariableExpression)exp);
		}
		
		int sum = 0;
		
		for (IExpression part : exp.getParts()) {
			if(part instanceof ILiteral)
				sum += Integer.parseInt(((ILiteral)part).getStringValue());
			else if (part instanceof IVariableExpression)
				sum += getIntValueFromIVariableExpression((IVariableExpression)part);
		}
		return sum;
	}
	
	private int getIntValueFromIVariableExpression(IVariableExpression exp) {
		VariableInfo info = varValues.get(((IVariableExpression) exp).getVariable());
		return Integer.parseInt(info.getReference().getValue().toString());
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
	
	public Map<IVariableDeclaration, VariableInfo> getVarValues() {
		return varValues;
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
