package pt.iscte.paddle.runtime.tests;

import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILiteral;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IVariableDeclaration;

//TODO completar NullPointerErrorTest
public class NullPointerErrorTest extends Test {
	
	public NullPointerErrorTest() {
		module = IModule.create();
		module.setId("NullPointerErrorTest");
		
//		IRecordType recordType = module.addRecordType("Teste");
		
		procedure = module.addProcedure(INT);
		procedure.setId("nullPointerIsCool");
		
		IVariableDeclaration n = procedure.addParameter(INT);
		n.setId("n");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration obj = body.addVariable(INT.array().reference());
		obj.setId("array");
		body.addAssignment(obj, ILiteral.getNull());
		
		IVariableDeclaration i1 = body.addVariable(INT, INT.literal(0));
		i1.setId("i1");
		
//		ILoop loop1 = body.addLoop(SMALLER.on(i1, n));
//		loop1.addArrayElementAssignment(array, ADD.on(i1, INT.literal(1)), i1);
//		loop1.addIncrement(i1);
		
		body.addReturn(n);
	}

}
