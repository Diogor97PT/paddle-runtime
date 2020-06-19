package pt.iscte.paddle.runtime.tests.arrayIndex;

import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.tests.Test;

public class MatrixErrorTest extends Test {
	
	public MatrixErrorTest() {
		module = IModule.create();
		module.setId("MatrixErrorTest");
		
		procedure = module.addProcedure(INT.array2D().reference());
		procedure.setId("multiplyMatrix");
		
		IVariableDeclaration n = procedure.addParameter(INT);
		n.setId("n");
		
		IBlock body = procedure.getBody();
		IVariableDeclaration m = body.addVariable(INT.array2D().reference(), INT.array2D().heapAllocation(INT.literal(20), INT.literal(20)));
		m.setId("m");

		IVariableDeclaration q = body.addVariable(INT, INT.literal(0));
		q.setId("q");
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER.on(i, INT.literal(21)));
		IVariableDeclaration j = loop.addVariable(INT, INT.literal(0));
		j.setId("j");
		
		ILoop loop2 = loop.addLoop(SMALLER.on(j, INT.literal(20)));
		loop2.addArrayElementAssignment(m, q, i, j);
		loop2.addIncrement(j);
		
		loop.addIncrement(q);
		loop.addIncrement(i);
		
		body.addReturn(m);
	}

}
