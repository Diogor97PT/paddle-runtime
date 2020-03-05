package pt.iscte.paddle.runtime.tests;

import static org.junit.Assert.assertEquals;
import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IType.INT;

import org.junit.Test;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.roles.IVariableRole;
import pt.iscte.paddle.runtime.roles.impl.FixedValue;

public class FixedValueTest {
	
	@Test
	public void fixedValueTest() {
		IModule module = IModule.create();				//Criar classe
		module.setId("FixedValueTest");					//dar nome à classe
		
		IProcedure naturals = module.addProcedure(INT.array().reference());	//criar função
		naturals.setId("naturals");
		
		IVariableDeclaration n = naturals.addParameter(INT);		//Parâmetro da Função
		n.setId("n");
		
		IBlock body = naturals.getBody();				//corpo da função
		
		IVariableDeclaration array = body.addVariable(INT.array().reference());
		array.setId("array");
		body.addAssignment(array, INT.array().heapAllocation(n));
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER.on(i, n));
		loop.addArrayElementAssignment(array, ADD.on(i, INT.literal(1)), i);
		loop.addAssignment(i, ADD.on(i, INT.literal(1)));
		
		body.addReturn(array);
		
		/*String code = module.translate(new IModel2CodeTranslator.Java());
		System.out.println(code);*/
		
		assertEquals(FixedValue.isFixedValue(n), true);
		assertEquals(FixedValue.isFixedValue(array), true);
		assertEquals(FixedValue.isFixedValue(i), false);
		
		IVariableRole vrn = new FixedValue(n);
		IVariableRole vrarray = new FixedValue(array);
		
		assertEquals(vrn.toString(), vrn.getName());
		assertEquals(vrarray.toString(), vrarray.getName() + " array that has been modified");
	}
	
	@Test
	public void fixedValueTestArrayChange() {
		IModule module = IModule.create();				//Criar classe
		module.setId("FixedValueTest");					//dar nome à classe
		
		IProcedure naturals = module.addProcedure(INT.array().reference());	//
		naturals.setId("naturals");
		
		IVariableDeclaration n = naturals.addParameter(INT);		//Parâmetro da Função
		n.setId("n");
		
		IBlock body = naturals.getBody();				//corpo da função
		
		IVariableDeclaration array = body.addVariable(INT.array().reference());
		array.setId("array");
		body.addAssignment(array, INT.array().heapAllocation(n));
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER.on(i, n));
		loop.addArrayElementAssignment(array, ADD.on(i, INT.literal(1)), i);
		loop.addAssignment(i, ADD.on(i, INT.literal(1)));
		
		body.addAssignment(array, INT.array().heapAllocation(n));
		
		body.addReturn(array);
		
		assertEquals(FixedValue.isFixedValue(n), true);
		assertEquals(FixedValue.isFixedValue(array), false);
		assertEquals(FixedValue.isFixedValue(i), false);
	}

}
