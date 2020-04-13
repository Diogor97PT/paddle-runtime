package pt.iscte.paddle.runtime.tests;

import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.GREATER_EQ;
import static pt.iscte.paddle.model.IOperator.SUB;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IVariableDeclaration;

public class ArrayIndexErrorBackwardTest extends Test {

	public ArrayIndexErrorBackwardTest() {
		module = IModule.create();												//Criar classe
		module.setId("ArrayIndexErrorBackwardTest");							//dar nome à classe
		
		procedure = module.addProcedure(INT.array().reference());				//criar função
		procedure.setId("naturals");
		
		IVariableDeclaration n = procedure.addParameter(INT);					//Parâmetro da Função
		n.setId("n");
		
		IBlock body = procedure.getBody();										//corpo da função
		
		IVariableDeclaration array = body.addVariable(INT.array().reference());
		array.setId("array");
		body.addAssignment(array, INT.array().heapAllocation(n));
		
		IVariableDeclaration i = body.addVariable(INT, SUB.on(n, INT.literal(1)));
		i.setId("i");
		
		ILoop loop = body.addLoop(GREATER_EQ.on(i, INT.literal(-1)));
		loop.addArrayElementAssignment(array, ADD.on(i, INT.literal(1)), i);
		loop.addAssignment(i, SUB.on(i, INT.literal(1)));
		
		body.addReturn(array);
	}

}
