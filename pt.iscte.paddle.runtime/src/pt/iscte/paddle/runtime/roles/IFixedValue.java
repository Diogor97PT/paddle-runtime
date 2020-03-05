package pt.iscte.paddle.runtime.roles;

import pt.iscte.paddle.model.roles.IVariableRole;

public interface IFixedValue extends IVariableRole {
	
	public boolean isModified();
	
	default String getName() {
		return "Fixed Value";
	}
}
