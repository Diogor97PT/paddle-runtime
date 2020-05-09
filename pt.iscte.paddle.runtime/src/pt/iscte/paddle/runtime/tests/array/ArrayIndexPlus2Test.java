package pt.iscte.paddle.runtime.tests.array;

import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.SMALLER_EQ;
import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.tests.Test;

public class ArrayIndexPlus2Test extends Test {
	
	public ArrayIndexPlus2Test() {
		module = IModule.create();												//Criar classe
		module.setId("ArrayIndexErrorTest");									//dar nome à classe
		
		procedure = module.addProcedure(INT.array().reference());				//criar função
		procedure.setId("naturals");
		
		IVariableDeclaration n = procedure.addParameter(INT);					//Parâmetro da Função
		n.setId("n");
		
		IBlock body = procedure.getBody();										//corpo da função
		
		IVariableDeclaration array = body.addVariable(INT.array().reference());
		array.setId("array");
		body.addAssignment(array, INT.array().heapAllocation(n));
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER_EQ.on(i, n));
		loop.addArrayElementAssignment(array, ADD.on(i, INT.literal(1)), i);
		loop.addAssignment(i, ADD.on(i, INT.literal(2)));
		
		body.addReturn(array);
	}

}
