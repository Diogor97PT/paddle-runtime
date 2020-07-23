package pt.iscte.paddle.runtime.experiment.tests;

import static pt.iscte.paddle.model.IOperator.SUB;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IOperator.DIV;
import static pt.iscte.paddle.model.IType.INT;
import static pt.iscte.paddle.model.IType.VOID;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.tests.Test;

public class Example07InvertSameVectorTest extends Test {
	
	public Example07InvertSameVectorTest() {
		module = IModule.create();
		module.setId("Example07InvertSameVector");
		
		IProcedure p2 = createProcedure();
		
		procedure = module.addProcedure(VOID);
		procedure.setId("main");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration v = body.addVariable(INT.array().reference());
		v.setId("v");
		body.addAssignment(v, INT.array().heapAllocation(INT.literal(5)));
		
		body.addArrayElementAssignment(v, INT.literal(1), INT.literal(0));
		body.addArrayElementAssignment(v, INT.literal(2), INT.literal(1));
		body.addArrayElementAssignment(v, INT.literal(3), INT.literal(2));
		body.addArrayElementAssignment(v, INT.literal(4), INT.literal(3));
		body.addArrayElementAssignment(v, INT.literal(5), INT.literal(4));
		
		body.addCall(p2, v);
		
//		body.addReturn(v);
	}

	private IProcedure createProcedure() {
		IProcedure procedure = module.addProcedure(VOID);
		procedure.setId("invert");
		
		IProcedure swapElements = createProcedure2();
		
		IVariableDeclaration v = procedure.addParameter(INT.array().reference());
		v.setId("v");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER.on(i, DIV.on(v.length(), INT.literal(2))));
		
		loop.addCall(swapElements, v, i, SUB.on(v.length(), i));						//Swap with the line below
//		loop.addCall(swapElements, v, i, SUB.on(SUB.on(v.length(), i), INT.literal(1)));
		
		loop.addIncrement(i);
		
		return procedure;
	}
	
	private IProcedure createProcedure2() {
		IProcedure procedure = module.addProcedure(IType.VOID);
		procedure.setId("swapElements");
		
		IVariableDeclaration v = procedure.addParameter(INT.array().reference());
		v.setId("v");
		
		IVariableDeclaration i = procedure.addParameter(INT);
		i.setId("i");
		
		IVariableDeclaration j = procedure.addParameter(INT);
		j.setId("j");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration temp = body.addVariable(INT, v.element(i));
		temp.setId("temp");
		
		body.addArrayElementAssignment(v, v.element(j), i);
		body.addArrayElementAssignment(v, temp, j);
		
		return procedure;
	}
	
}
