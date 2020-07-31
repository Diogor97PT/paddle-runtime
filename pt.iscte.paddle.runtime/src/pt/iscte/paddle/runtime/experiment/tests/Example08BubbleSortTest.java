package pt.iscte.paddle.runtime.experiment.tests;

import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.GREATER;
import static pt.iscte.paddle.model.IOperator.SMALLER;
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

public class Example08BubbleSortTest extends Test {
	
	public Example08BubbleSortTest() {
		module = IModule.create();
		module.setId("Example08BubbleSort");
		
		IProcedure p2 = createProcedure();
		
		procedure = module.addProcedure(VOID);
		procedure.setId("main");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration v = body.addVariable(INT.array().reference());
		v.setId("v");
		body.addAssignment(v, INT.array().heapAllocation(INT.literal(8)));

		body.addArrayElementAssignment(v, INT.literal(9), INT.literal(0));
		body.addArrayElementAssignment(v, INT.literal(10), INT.literal(1));
		body.addArrayElementAssignment(v, INT.literal(99), INT.literal(2));
		body.addArrayElementAssignment(v, INT.literal(52), INT.literal(3));
		body.addArrayElementAssignment(v, INT.literal(23), INT.literal(4));
		body.addArrayElementAssignment(v, INT.literal(1), INT.literal(5));
		body.addArrayElementAssignment(v, INT.literal(88), INT.literal(6));
		body.addArrayElementAssignment(v, INT.literal(1), INT.literal(7));
		
		body.addCall(p2, v);
//		body.addReturn(v);
	}

	private IProcedure createProcedure() {
		IProcedure procedure2 = module.addProcedure(VOID);
		procedure2.setId("bubbleSort");
		
		IVariableDeclaration v = procedure2.addParameter(INT.array().reference());
		v.setId("v");
		
		IBlock body = procedure2.getBody();
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER.on(i, v.length()));
		
		IVariableDeclaration j = loop.addVariable(INT, INT.literal(0));
		j.setId("j");
		
		ILoop loop2 = loop.addLoop(SMALLER.on(j, SUB.on(v.length(), i)));		//swap with line below
//		ILoop loop2 = loop.addLoop(SMALLER.on(j, SUB.on(SUB.on(v.length(), i), INT.literal(1))));
		
		ISelection ifSelection = loop2.addSelection(GREATER.on(v.element(j), v.element(ADD.on(j, INT.literal(1)))));
		IVariableDeclaration temp = ifSelection.addVariable(INT, v.element(j));
		temp.setId("temp");
		ifSelection.addArrayElementAssignment(v, v.element(ADD.on(j, INT.literal(1))), j);
		ifSelection.addArrayElementAssignment(v, temp, ADD.on(j, INT.literal(1)));
		
		loop2.addIncrement(j);
		loop.addIncrement(i);
		
		return procedure2;
	}
	
}
