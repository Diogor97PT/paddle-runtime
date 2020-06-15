package pt.iscte.paddle.runtime.experiment.tests;

import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.tests.Test;

public class Example02NaturalsTest extends Test {

	//Example 2 Test
	public Example02NaturalsTest() {
		module = IModule.create();
		module.setId("Example02NaturalsTest");
		
		IProcedure p2 = createProcedure();
		
		procedure = module.addProcedure(INT.array().reference());
		procedure.setId("main");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(5));
		i.setId("i");
		
		body.addReturn(p2.expression(i));
	}

	private IProcedure createProcedure() {
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
		
		ILoop loop = body.addLoop(SMALLER.on(i, n));
		
		loop.addIncrement(i);										//Swap this line with the one below
		loop.addArrayElementAssignment(array, ADD.on(i, INT.literal(1)), i);
		
		body.addReturn(array);
		
		return procedure;
	}
	
}
