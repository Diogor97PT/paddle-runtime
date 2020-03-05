package pt.iscte.paddle.runtime.roles;

import java.util.List;

import pt.iscte.paddle.model.IVariableDeclaration;

public interface IArrayIndexIterator extends IStepper {
	
	List<IVariableDeclaration> getArrayVariables();	//arrays em que a variavel é usada
	
	@Override
	default String getName() {
		return "Array Index Iterator";
	}
}
