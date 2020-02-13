package pt.iscte.paddle.runtime.roles;

import java.util.List;

import pt.iscte.paddle.model.IVariable;

public interface IArrayIndexIterator extends IStepper {
	
	List<IVariable> getArrayVariables();	//arrays em que a variavel é usada
	
	/*
	 * casos:
	 * v[i] = ...
	 * 
	 * ... = v[i] + ....
	 */
}
