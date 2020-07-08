package pt.iscte.paddle.runtime.experiment.tests;

import static pt.iscte.paddle.model.IOperator.MUL;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.tests.Test;

public class Example05MultiplyMatrixTest extends Test {

	public Example05MultiplyMatrixTest() {
		module = IModule.create();
		module.setId("Example05MultiplyMatrix");
		
		IProcedure p2 = createProcedure();
		
		procedure = module.addProcedure(IType.VOID);
		procedure.setId("main");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration m = body.addVariable(INT.array2D().reference());
		m.setId("m");
		body.addAssignment(m, INT.array2D().heapAllocation(INT.literal(4), INT.literal(4)));
		body.addArrayElementAssignment(m, INT.array().heapAllocation(INT.literal(4)), INT.literal(0));
		body.addArrayElementAssignment(m, INT.array().heapAllocation(INT.literal(3)), INT.literal(1));
		body.addArrayElementAssignment(m, INT.array().heapAllocation(INT.literal(5)), INT.literal(2));
		body.addArrayElementAssignment(m, INT.array().heapAllocation(INT.literal(3)), INT.literal(3));
		
		body.addArrayElementAssignment(m, INT.literal(1), INT.literal(0), INT.literal(0));
		body.addArrayElementAssignment(m, INT.literal(2), INT.literal(0), INT.literal(1));
		body.addArrayElementAssignment(m, INT.literal(3), INT.literal(0), INT.literal(2));
		body.addArrayElementAssignment(m, INT.literal(4), INT.literal(0), INT.literal(3));
		
		body.addArrayElementAssignment(m, INT.literal(3), INT.literal(1), INT.literal(0));
		body.addArrayElementAssignment(m, INT.literal(2), INT.literal(1), INT.literal(1));
		body.addArrayElementAssignment(m, INT.literal(1), INT.literal(1), INT.literal(2));
		
		body.addArrayElementAssignment(m, INT.literal(4), INT.literal(2), INT.literal(0));
		body.addArrayElementAssignment(m, INT.literal(1), INT.literal(2), INT.literal(1));
		body.addArrayElementAssignment(m, INT.literal(7), INT.literal(2), INT.literal(2));
		body.addArrayElementAssignment(m, INT.literal(3), INT.literal(2), INT.literal(3));
		
		body.addArrayElementAssignment(m, INT.literal(3), INT.literal(3), INT.literal(0));
		body.addArrayElementAssignment(m, INT.literal(8), INT.literal(3), INT.literal(1));
		body.addArrayElementAssignment(m, INT.literal(4), INT.literal(3), INT.literal(2));
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(2));
		i.setId("i");
		
		body.addCall(p2, m, i);
	}
	
	private IProcedure createProcedure() {
		IProcedure p2 = module.addProcedure(IType.VOID);
		p2.setId("multiplyMatrix");
		
		IVariableDeclaration m = p2.addParameter(INT.array2D().reference());
		m.setId("m");
		
		IVariableDeclaration n = p2.addParameter(INT);
		n.setId("n");
		
		IBlock body = p2.getBody();
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER.on(i, m.length()));
		
		IVariableDeclaration j = loop.addVariable(INT, INT.literal(0));
		j.setId("j");
		
		ILoop loop2 = loop.addLoop(SMALLER.on(j, m.length()));					//swap with line below
//		ILoop loop2 = loop.addLoop(SMALLER.on(j, m.element(i).length()));
		
		loop2.addArrayElementAssignment(m, MUL.on(m.element(i, j), n), i, j);
		
		loop2.addIncrement(j);
		
		loop.addIncrement(i);
		
		return p2;
	}
	
}
