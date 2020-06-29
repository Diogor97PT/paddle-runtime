package pt.iscte.paddle.runtime.experiment.tests;

import static pt.iscte.paddle.model.IOperator.SUB;
import static pt.iscte.paddle.model.IOperator.GREATER_EQ;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.tests.Test;

public class Example04InvertTest extends Test {
	
	public Example04InvertTest() {
		module = IModule.create();
		module.setId("Example04InvertTest");
		
		IProcedure p2 = createProcedure();
		
		procedure = module.addProcedure(INT.array().reference());
		procedure.setId("main");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration v = body.addVariable(INT.array().reference());		//Array to search for number
		v.setId("v");
		
		body.addAssignment(v, INT.array().heapAllocation(INT.literal(5)));
		
		body.addArrayElementAssignment(v, INT.literal(1), INT.literal(0));
		body.addArrayElementAssignment(v, INT.literal(2), INT.literal(1));
		body.addArrayElementAssignment(v, INT.literal(3), INT.literal(2));
		body.addArrayElementAssignment(v, INT.literal(4), INT.literal(3));
		body.addArrayElementAssignment(v, INT.literal(5), INT.literal(4));
		
		body.addReturn(p2.expression(v));
	}

	private IProcedure createProcedure() {
		IProcedure procedure = module.addProcedure(INT.array().reference());
		procedure.setId("invert");
		
		IVariableDeclaration v = procedure.addParameter(INT.array().reference());
		v.setId("v");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration v2 = body.addVariable(INT.array().reference());
		v2.setId("v2");
		body.addAssignment(v2, INT.array().heapAllocation(v.length()));
		
		IVariableDeclaration i = body.addVariable(INT);
		i.setId("i");
		body.addAssignment(i, SUB.on(v.length(), INT.literal(1)));
		
		ILoop loop = body.addLoop(GREATER_EQ.on(i, INT.literal(0)));
		loop.addArrayElementAssignment(v2, v.element(i), SUB.on(v.length(), i));			//Switch with line below
//		loop.addArrayElementAssignment(v2, v.element(i), SUB.on(SUB.on(v.length(), i), INT.literal(1)));
		loop.addDecrement(i);
		
		body.addReturn(v2);
		
		return procedure;
	}
	
	
	
}
