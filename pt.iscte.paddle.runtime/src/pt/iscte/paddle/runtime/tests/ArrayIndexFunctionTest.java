package pt.iscte.paddle.runtime.tests;

import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IOperator.SMALLER_EQ;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariableDeclaration;

public class ArrayIndexFunctionTest extends Test {
	
	public ArrayIndexFunctionTest() {
		module = IModule.create();
		module.setId("ArrayIndexFunctionTest");
		
		procedure = module.addProcedure(INT);
		procedure.setId("sumAllNaturals");
		
		IVariableDeclaration n = procedure.addParameter(INT);
		n.setId("n");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration i1 = body.addVariable(INT, INT.literal(0));
		i1.setId("i1");
		
		IVariableDeclaration array = body.addVariable(INT.array().reference());
		array.setId("array");
		body.addAssignment(array, INT.array().heapAllocation(n));
		
		ILoop loop1 = body.addLoop(SMALLER.on(i1, n));
		loop1.addArrayElementAssignment(array, ADD.on(i1, INT.literal(1)), i1);
		loop1.addIncrement(i1);
		
		//Procedure 2
		IProcedure procedure2 = module.addProcedure(INT);
		IVariableDeclaration array2 = procedure2.addParameter(INT.array());
		array2.setId("array");
		IVariableDeclaration n2 = procedure2.addParameter(INT);
		n2.setId("n");
		procedure2.setId("sumAll");
		
		IBlock body2 = procedure2.getBody();
		
		IVariableDeclaration i2 = body2.addVariable(INT, INT.literal(0));
		i2.setId("i2");
		
		IVariableDeclaration sum2 = body2.addVariable(INT, INT.literal(0));
		sum2.setId("sum");
		
		ILoop loop = body2.addLoop(SMALLER_EQ.on(i2, n2));
		loop.addAssignment(sum2, ADD.on(sum2, array2.element(i2)));
		loop.addIncrement(i2);
		
		body2.addReturn(sum2);
		
		//Procedure 1 again
		IVariableDeclaration sum = body.addVariable(INT);
		sum.setId("sum");
		
		body.addAssignment(sum, procedure2.expression(array, n));
		
		body.addReturn(sum);
	}
}
