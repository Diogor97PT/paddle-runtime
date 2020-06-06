package pt.iscte.paddle.runtime.experiment.tests;

import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.tests.Test;

public class Example06TranposeMatrixTest extends Test {
	
	//TODO Example06 Não funciona ainda
	public Example06TranposeMatrixTest() {
		module = IModule.create();
		module.setId("Example06TranposeMatrixTest");
		
		IProcedure p2 = createProcedure();
		
		procedure = module.addProcedure(INT.array2D().reference());
		procedure.setId("main");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration m = body.addVariable(INT.array2D().reference());
		m.setId("m");
		body.addAssignment(m, INT.array2D().heapAllocation(INT.literal(2), INT.literal(3)));
		
		body.addArrayElementAssignment(m, INT.literal(1), INT.literal(0), INT.literal(0));
		body.addArrayElementAssignment(m, INT.literal(2), INT.literal(0), INT.literal(1));
		body.addArrayElementAssignment(m, INT.literal(3), INT.literal(0), INT.literal(2));
		
		body.addArrayElementAssignment(m, INT.literal(4), INT.literal(1), INT.literal(0));
		body.addArrayElementAssignment(m, INT.literal(5), INT.literal(1), INT.literal(1));
		body.addArrayElementAssignment(m, INT.literal(6), INT.literal(1), INT.literal(2));
		
//		int [][] original = {{1, 2, 3},
//	  			 {4, 5, 6}};
		
		body.addReturn(p2.expression(m));
	}

	private IProcedure createProcedure() {
		IProcedure procedure = module.addProcedure(INT.array2D().reference());
		procedure.setId("tranposeMatrix");
		
		IVariableDeclaration m = procedure.addParameter(INT.array2D().reference());
		m.setId("m");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration transposed = body.addVariable(INT.array2D().reference());
		transposed.setId("transposed");
		body.addAssignment(transposed, INT.array2D().heapAllocation(m.length(INT.literal(0)), m.length()));
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER.on(i, m.length(INT.literal(0))));
		
		IVariableDeclaration j = body.addVariable(INT, INT.literal(0));
		j.setId("j");
		
		ILoop loop2 = body.addLoop(SMALLER.on(j, m.length()));
		
		loop2.addArrayElementAssignment(transposed, m.element(i, j), j, i);
//		loop2.addArrayElementAssignment(transposed, m.element(j, i), i, j);
		loop2.addIncrement(j);
		loop.addIncrement(i);
		
		body.addReturn(transposed);
		
		return procedure;
	}
	
}
