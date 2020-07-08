package pt.iscte.paddle.runtime.experiment.tests;

import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.SMALLER_EQ;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.tests.Test;

public class Example01SumTest extends Test {
	
	public Example01SumTest() {
		module = IModule.create();
		module.setId("Example01Sum");
		
		IProcedure p2 = createProcedure();
		
		procedure = module.addProcedure(INT);
		procedure.setId("main");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration v = body.addVariable(INT.array().reference());
		v.setId("v");
		
		body.addAssignment(v, INT.array().heapAllocation(INT.literal(4)));
		
		body.addArrayElementAssignment(v, INT.literal(1), INT.literal(0));
		body.addArrayElementAssignment(v, INT.literal(3), INT.literal(1));
		body.addArrayElementAssignment(v, INT.literal(5), INT.literal(2));
		body.addArrayElementAssignment(v, INT.literal(10), INT.literal(3));
		
		body.addReturn(p2.expression(v));
	}
	
	private IProcedure createProcedure() {
		IProcedure procedure = module.addProcedure(INT);
		procedure.setId("sum");
		
		IVariableDeclaration v = procedure.addParameter(INT.array().reference());
		v.setId("v");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		IVariableDeclaration sum = body.addVariable((INT), INT.literal(0));
		sum.setId("sum");
		
		ILoop loop = body.addLoop(SMALLER_EQ.on(i, v.length()));		//switch SMALLER_EQ(<=) with SMALLER(<)
		loop.addAssignment(sum, ADD.on(sum, v.element(i)));
		loop.addIncrement(i);
		
		body.addReturn(sum);
		
		return procedure;
	}

}
