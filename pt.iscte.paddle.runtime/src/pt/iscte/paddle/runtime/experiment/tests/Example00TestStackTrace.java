package pt.iscte.paddle.runtime.experiment.tests;

import static pt.iscte.paddle.model.IType.INT;

import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.tests.Test;

public class Example00TestStackTrace extends Test {

	public Example00TestStackTrace() {
		module = IModule.create();
		module.setId("Example00");
		
		IProcedure p2 = createProcedure();
		
		procedure = module.addProcedure(INT);
		procedure.setId("main");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration v = body.addVariable(INT.array().reference());
		v.setId("v");
		
		body.addAssignment(v, INT.array().heapAllocation(INT.literal(2)));
		
		body.addArrayElementAssignment(v, INT.literal(1), INT.literal(0));
		body.addArrayElementAssignment(v, INT.literal(2), INT.literal(1));
		
		body.addReturn(p2.expression(v));
	}

	private IProcedure createProcedure() {
		IProcedure procedure = module.addProcedure(INT);
		procedure.setId("example");
		
		IVariableDeclaration v = procedure.addParameter(INT.array().reference());
		v.setId("v");
		
		IBlock body = procedure.getBody();
		
		IVariableDeclaration i = body.addVariable(INT);
		i.setId("i");
		
		body.addAssignment(i, v.element(v.length()));
		
		body.addReturn(i);
		
		return procedure;
	}
	
}
