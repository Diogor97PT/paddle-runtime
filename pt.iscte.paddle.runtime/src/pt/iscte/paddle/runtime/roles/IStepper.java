package pt.iscte.paddle.runtime.roles;

import pt.iscte.paddle.roles.IVariableRole;

public interface IStepper extends IVariableRole {
	Direction getDirection();

	default String getName() {
		return "Stepper";
	}

	enum Direction {
		INC, DEC;
	}
}
