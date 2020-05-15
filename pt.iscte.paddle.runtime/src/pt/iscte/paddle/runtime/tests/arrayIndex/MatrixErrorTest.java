package pt.iscte.paddle.runtime.tests.arrayIndex;

import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IType;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.tests.Test;

public class MatrixErrorTest extends Test {
	
	public MatrixErrorTest() {
		module = IModule.create();
		module.setId("MatrixErrorTest");
		
		procedure = module.addProcedure(IType.VOID);				//criar função
		procedure.setId("multiplyMatrix");
		
		IVariableDeclaration n = procedure.addParameter(INT);
		n.setId("n");
		
		IBlock body = procedure.getBody();
		//TODO finish this Test
	}

}
