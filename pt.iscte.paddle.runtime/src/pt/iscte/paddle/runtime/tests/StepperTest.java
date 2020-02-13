package pt.iscte.paddle.runtime.tests;

import static org.junit.Assert.assertEquals;
import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.SMALLER;
import static pt.iscte.paddle.model.IOperator.SUB;
import static pt.iscte.paddle.model.IType.INT;

import org.junit.Test;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModel2CodeTranslator;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.roles.IVariableRole;
import pt.iscte.paddle.runtime.roles.IStepper;

public class StepperTest {

	//Possivelmente melhorar estes teste no Futuro
	
	@Test
	public void StepperAddPositiveTest() {		//i + 1
		IModule module = IModule.create();				//Criar classe
		module.setId("AddPositive");					//dar nome à classe
		
		IProcedure naturals = module.addProcedure(INT.array().reference());	//
		naturals.setId("naturals");
		
		IVariable n = naturals.addParameter(INT);		//Parâmetro da Função
		n.setId("n");
		
		IBlock body = naturals.getBody();				//corpo da função
		
		IVariable array = body.addVariable(INT.array().reference());
		array.setId("array");
		body.addAssignment(array, INT.array().heapAllocation(n));
		
		IVariable i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER.on(i, n));
		loop.addArrayElementAssignment(array, ADD.on(i, INT.literal(1)), i);
		loop.addAssignment(i, ADD.on(i, INT.literal(1)));
		
		body.addReturn(array);
		
		/*String code = module.translate(new IModel2CodeTranslator.Java());
		System.out.println(code);*/
		
		IVariableRole role = IStepper.createStepper(i);
		assertEquals(role.toString(), "Stepper(INC)");
	}
	
	@Test
	public void StepperSubPositiveTest() {		//i - 1
		IModule module = IModule.create();
		module.setId("SubPositive");
		
		IProcedure naturals = module.addProcedure(INT.array().reference());
		naturals.setId("naturals");
		
		IVariable n = naturals.addParameter(INT);
		n.setId("n");
		
		IBlock body = naturals.getBody();
		
		IVariable array = body.addVariable(INT.array().reference());
		array.setId("array");
		body.addAssignment(array, INT.array().heapAllocation(n));
		
		IVariable i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER.on(i, n));
		loop.addArrayElementAssignment(array, ADD.on(i, INT.literal(1)), i);
		loop.addAssignment(i, SUB.on(i, INT.literal(1)));
		
		body.addReturn(array);
		
		IVariableRole role = IStepper.createStepper(i);
		assertEquals(role.toString(), "Stepper(DEC)");
	}
	
	@Test
	public void StepperAddNegativeTest() {		//i + (-1)
		IModule module = IModule.create();
		module.setId("AddNegative");
		
		IProcedure naturals = module.addProcedure(INT.array().reference());
		naturals.setId("naturals");
		
		IVariable n = naturals.addParameter(INT);
		n.setId("n");
		
		IBlock body = naturals.getBody();
		
		IVariable array = body.addVariable(INT.array().reference());
		array.setId("array");
		body.addAssignment(array, INT.array().heapAllocation(n));
		
		IVariable i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER.on(i, n));
		loop.addArrayElementAssignment(array, ADD.on(i, INT.literal(1)), i);
		loop.addAssignment(i, ADD.on(i, INT.literal(-1)));
		
		body.addReturn(array);
		
		IVariableRole role = IStepper.createStepper(i);
		assertEquals(role.toString(), "Stepper(DEC)");
	}

	@Test
	public void StepperSubNegativeTest() {		//i - (-1)
		IModule module = IModule.create();
		module.setId("SubNegative");
		
		IProcedure naturals = module.addProcedure(INT.array().reference());
		naturals.setId("naturals");
		
		IVariable n = naturals.addParameter(INT);
		n.setId("n");
		
		IBlock body = naturals.getBody();
		
		IVariable array = body.addVariable(INT.array().reference());
		array.setId("array");
		body.addAssignment(array, INT.array().heapAllocation(n));
		
		IVariable i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER.on(i, n));
		loop.addArrayElementAssignment(array, ADD.on(i, INT.literal(1)), i);
		loop.addAssignment(i, SUB.on(i, INT.literal(-1)));
		
		body.addReturn(array);
		
		IVariableRole role = IStepper.createStepper(i);
		assertEquals(role.toString(), "Stepper(INC)");
	}
	
	@Test
	public void StepperLeftLiteralRightVarSum() {	//1 + i
		IModule module = IModule.create();
		module.setId("SubNegative");
		
		IProcedure naturals = module.addProcedure(INT.array().reference());
		naturals.setId("naturals");
		
		IVariable n = naturals.addParameter(INT);
		n.setId("n");
		
		IBlock body = naturals.getBody();
		
		IVariable array = body.addVariable(INT.array().reference());
		array.setId("array");
		body.addAssignment(array, INT.array().heapAllocation(n));
		
		IVariable i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER.on(i, n));
		loop.addArrayElementAssignment(array, ADD.on(i, INT.literal(1)), i);
		loop.addAssignment(i, ADD.on(INT.literal(1), i));
		
		body.addReturn(array);
		
		IVariableRole role = IStepper.createStepper(i);
		assertEquals(role.toString(), "Stepper(INC)");
	}
	
	@Test
	public void StepperLeftLiteralRightVarSub() {	//1 - i		Should not be a Stepper
		IModule module = IModule.create();
		module.setId("SubNegative");
		
		IProcedure naturals = module.addProcedure(INT.array().reference());
		naturals.setId("naturals");
		
		IVariable n = naturals.addParameter(INT);
		n.setId("n");
		
		IBlock body = naturals.getBody();
		
		IVariable array = body.addVariable(INT.array().reference());
		array.setId("array");
		body.addAssignment(array, INT.array().heapAllocation(n));
		
		IVariable i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER.on(i, n));
		loop.addArrayElementAssignment(array, ADD.on(i, INT.literal(1)), i);
		loop.addAssignment(i, SUB.on(INT.literal(1), i));
		
		body.addReturn(array);
		
		assertEquals(IStepper.isStepper(i), false);
	}
}
