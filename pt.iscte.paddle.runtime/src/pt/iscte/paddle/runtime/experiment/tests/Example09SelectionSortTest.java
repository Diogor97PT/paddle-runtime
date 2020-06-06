package pt.iscte.paddle.runtime.experiment.tests;

import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IOperator.SUB;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.tests.Test;

public class Example09SelectionSortTest extends Test {
	
	//TODO Example09 corrigir nao consegui testar porque ocupa demasiado espaço no ecrã
	//TODO add error to code
	public Example09SelectionSortTest() {
		module = IModule.create();
		module.setId("Example09SelectionSortTest");
		
		IProcedure p2 = createProcedure();
		
		procedure = module.addProcedure(INT.array().reference());
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
		
		body.addReturn(v);
	}

	private IProcedure createProcedure() {
		IProcedure procedure = module.addProcedure(IType.VOID);
		procedure.setId("selectionSort");
		
		IProcedure swapElements = createProcedure2();
		
		IVariableDeclaration v = procedure.addParameter(INT.array().reference());
		v.setId("v");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER.on(i, SUB.on(v.length(), INT.literal(1))));
		
		IVariableDeclaration minimumPos = loop.addVariable(INT, i);
		minimumPos.setId("minimumPos");
		
		IVariableDeclaration j = loop.addVariable(INT, ADD.on(i, INT.literal(1)));
		j.setId("j");
		
		ILoop loop2 = loop.addLoop(SMALLER.on(j, v.length()));
		
		ISelection ifSelection = loop2.addSelection(SMALLER.on(v.element(j), v.element(minimumPos)));
		ifSelection.addAssignment(minimumPos, j);
		
		loop.addCall(swapElements, v, minimumPos, i);
		
		loop2.addIncrement(j);
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
