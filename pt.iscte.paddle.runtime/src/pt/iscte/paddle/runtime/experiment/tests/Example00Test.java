package pt.iscte.paddle.runtime.experiment.tests;

import static pt.iscte.paddle.model.IOperator.EQUAL;
import static pt.iscte.paddle.model.IOperator.SMALLER_EQ;
import static pt.iscte.paddle.model.IType.BOOLEAN;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.tests.Test;

public class Example00Test extends Test {

	public Example00Test() {
		module = IModule.create();
		module.setId("Example00");
		
		IProcedure p2 = createProcedure();
		
		procedure = module.addProcedure(BOOLEAN);
		procedure.setId("main");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration v = body.addVariable(INT.array().reference());
		v.setId("v");
		
		body.addAssignment(v, INT.array().heapAllocation(INT.literal(2)));
		
		body.addArrayElementAssignment(v, INT.literal(1), INT.literal(0));
		body.addArrayElementAssignment(v, INT.literal(2), INT.literal(1));
		
		IVariableDeclaration n = body.addVariable(INT, INT.literal(3));
		n.setId("n");
		
		body.addReturn(p2.expression(v, n));
	}

	private IProcedure createProcedure() {
		IProcedure procedure = module.addProcedure(BOOLEAN);
		procedure.setId("example");
		
		IVariableDeclaration v = procedure.addParameter(INT.array().reference());
		v.setId("v");
		
		IVariableDeclaration n = procedure.addParameter(INT);
		n.setId("n");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER_EQ.on(i, v.length()));		//switch SMALLER_EQ(<=) with SMALLER(<)

		ISelection ifSelection = loop.addSelection(EQUAL.on(n, v.element(i)));
		ifSelection.addReturn(BOOLEAN.literal(true));
		loop.addIncrement(i);
		
		body.addReturn(BOOLEAN.literal(false));
		
		return procedure;
	}
	
}
