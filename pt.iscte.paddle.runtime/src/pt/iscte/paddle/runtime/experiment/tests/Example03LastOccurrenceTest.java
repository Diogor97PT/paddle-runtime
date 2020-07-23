package pt.iscte.paddle.runtime.experiment.tests;

import static pt.iscte.paddle.model.IOperator.EQUAL;
import static pt.iscte.paddle.model.IOperator.GREATER_EQ;
import static pt.iscte.paddle.model.IOperator.SUB;
import static pt.iscte.paddle.model.IType.INT;
import static pt.iscte.paddle.model.IType.VOID;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.tests.Test;

public class Example03LastOccurrenceTest extends Test {
	
	public Example03LastOccurrenceTest() {
		module = IModule.create();
		module.setId("Example03LastOccurrence");
		
		IProcedure p2 = createProcedure();
		
		procedure = module.addProcedure(VOID);
		procedure.setId("main");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration v = body.addVariable(INT.array().reference());		//Array to search for number
		v.setId("v");
		
		body.addAssignment(v, INT.array().heapAllocation(INT.literal(8)));
		
		body.addArrayElementAssignment(v, INT.literal(1), INT.literal(0));
		body.addArrayElementAssignment(v, INT.literal(2), INT.literal(1));
		body.addArrayElementAssignment(v, INT.literal(8), INT.literal(2));
		body.addArrayElementAssignment(v, INT.literal(2), INT.literal(3));
		body.addArrayElementAssignment(v, INT.literal(5), INT.literal(4));
		body.addArrayElementAssignment(v, INT.literal(2), INT.literal(5));
		body.addArrayElementAssignment(v, INT.literal(9), INT.literal(6));
		body.addArrayElementAssignment(v, INT.literal(1), INT.literal(7));
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(3));			//Number to find
		i.setId("i");
		
//		body.addReturn(p2.expression(v, i));
		body.addCall(p2, v, i);
	}

	private IProcedure createProcedure() {
		IProcedure procedure = module.addProcedure(INT);
		procedure.setId("lastOccurrence");
		
		IVariableDeclaration v = procedure.addParameter(INT.array().reference());
		v.setId("v");
		
		IVariableDeclaration n = procedure.addParameter(INT);
		n.setId("n");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration i = body.addVariable(INT);
		i.setId("i");
		body.addAssignment(i, SUB.on(v.length(), INT.literal(1)));
		
		ILoop loop = body.addLoop(GREATER_EQ.on(i, INT.literal(-1)));				//GREATER_EQ.on(i, INT.literal(0)) or GREATER.on(i, INT.literal(-1))
		
		ISelection ifselection = loop.addSelection(EQUAL.on(v.element(i), n));
		ifselection.addReturn(i);
		
		loop.addDecrement(i);
		
		body.addReturn(INT.literal(-1));
		
		return procedure;
	}
	
}
