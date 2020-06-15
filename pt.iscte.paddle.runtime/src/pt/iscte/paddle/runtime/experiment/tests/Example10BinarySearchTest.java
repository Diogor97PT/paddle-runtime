package pt.iscte.paddle.runtime.experiment.tests;

import static pt.iscte.paddle.model.IOperator.SUB;
import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.DIV;
import static pt.iscte.paddle.model.IOperator.SMALLER_EQ;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IOperator.EQUAL;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.ISelection;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.tests.Test;

public class Example10BinarySearchTest extends Test {
	
	//TODO Example10 corrigir nao consegui testar porque ocupa demasiado espaço no ecrã
	public Example10BinarySearchTest() {
		module = IModule.create();
		module.setId("Example10BinarySearchTest");
		
		IProcedure p2 = createProcedure();
		
		procedure = module.addProcedure(INT);
		procedure.setId("main");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration v = body.addVariable(INT.array().reference());
		v.setId("v");
		body.addAssignment(v, INT.array().heapAllocation(INT.literal(10)));
		
		body.addArrayElementAssignment(v, INT.literal(1), INT.literal(0));
		body.addArrayElementAssignment(v, INT.literal(5), INT.literal(1));
		body.addArrayElementAssignment(v, INT.literal(6), INT.literal(2));
		body.addArrayElementAssignment(v, INT.literal(7), INT.literal(3));
		body.addArrayElementAssignment(v, INT.literal(9), INT.literal(4));
		body.addArrayElementAssignment(v, INT.literal(10), INT.literal(5));
		body.addArrayElementAssignment(v, INT.literal(23), INT.literal(6));
		body.addArrayElementAssignment(v, INT.literal(52), INT.literal(7));
		body.addArrayElementAssignment(v, INT.literal(88), INT.literal(8));
		body.addArrayElementAssignment(v, INT.literal(99), INT.literal(9));
		
		IVariableDeclaration n = body.addVariable(INT, INT.literal(88));
		n.setId("n");
		
		body.addReturn(p2.expression(v, n));
	}

	private IProcedure createProcedure() {
		IProcedure procedure = module.addProcedure(INT);
		procedure.setId("binarySearch");
		
		IVariableDeclaration v = procedure.addParameter(INT.array().reference());
		v.setId("v");
		
		IVariableDeclaration n = procedure.addParameter(INT);
		n.setId("n");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		IVariableDeclaration r = body.addVariable(INT, SUB.on(v.length(), INT.literal(1)));
		r.setId("r");
		
		ILoop loop = body.addLoop(SMALLER_EQ.on(i, r));
		
		IVariableDeclaration j = loop.addVariable(INT, ADD.on(i, DIV.on(i, INT.literal(2))));			//swap with the line below
//		IVariableDeclaration j = loop.addVariable(INT, ADD.on(i, DIV.on(SUB.on(r, i), INT.literal(2))));
		j.setId("j");
		
		ISelection firstIf = loop.addSelection(EQUAL.on(v.element(j), n));
		firstIf.addReturn(j);
		
		ISelection secondIf = loop.addSelectionWithAlternative(SMALLER.on(v.element(j), n));
		
		secondIf.addAssignment(i, ADD.on(j, INT.literal(1)));
		secondIf.getAlternativeBlock().addAssignment(r, SUB.on(j, INT.literal(1)));
		
		body.addReturn(INT.literal(-1));
		
		return procedure;
	}
	
}
