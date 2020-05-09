package pt.iscte.paddle.runtime.tests;

import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;

public abstract class Test {
	
	protected IModule module;
	protected IProcedure procedure;
	
	public IModule getModule() {
		return module;
	}
	
	public IProcedure getProcedure() {
		return procedure;
	}
	
}
