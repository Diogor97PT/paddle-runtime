package pt.iscte.paddle.runtime.roles;

import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILiteral;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.roles.IVariableRole;

public interface IStepper extends IVariableRole {
	
	Direction getDirection();

	default String getName() {
		return "Stepper";
	}

	enum Direction {
		INC, DEC;
	}
	
	static boolean isStepper(IVariable var) {
		Visitor v = new Visitor(var);
		var.getOwnerProcedure().accept(v);
		return v.isValid && v.direction != null;
	}
	
	static IVariableRole createStepper(IVariable var) {
		assert isStepper(var);
		Visitor v = new Visitor(var);
		var.getOwnerProcedure().accept(v);
		return new Stepper(v.direction);
	}
	
	class Visitor implements IBlock.IVisitor {
		
		final IVariable var;
		
		boolean first = true;
		
		Direction direction = null;
		int stepSize = Integer.MIN_VALUE;
		
		boolean isValid = true; //true until proven otherwise
		
		public Visitor(IVariable var) {
			this.var = var;
		}
		
		@Override
		public boolean visit(IVariableAssignment assignment) {
			if(assignment.getTarget().equals(var) && isValid){
				if(first) {
					first = !first;
				} else {
					//check direction and size of step(steps have to be of same size / direction)
					Direction dir = getDirection(assignment);
					if(dir == null || direction != null && dir != direction)
						isValid = false;
					else
						direction = dir;
				}
			}
			return false;
		}
		
		Direction getDirection(IVariableAssignment var) { //Check step size / direction
			IExpression expression = var.getExpression();
			if(expression instanceof IBinaryExpression) {
				IBinaryExpression be = (IBinaryExpression) expression;
				IExpression left = be.getLeftOperand();							//left e right -> ex:  var = left + right
				IExpression right = be.getRightOperand();
				if((be.getOperator() == IOperator.ADD || be.getOperator() == IOperator.SUB)		//Stepper only sums or subtracts
						&& (left instanceof IVariable && (((IVariable)left).equals(var.getTarget()) && right instanceof ILiteral))) {	//left variable and right literal
					
					return getDirectionHelper((ILiteral) right, be);
				} else if (be.getOperator() == IOperator.ADD
						&& (left instanceof ILiteral && (right instanceof IVariable && (((IVariable)right).equals(var.getTarget()))))) { //left literal and right variable
																																		//1 + i	<- only sum is accepted
					return getDirectionHelper((ILiteral) left, be);
				}
				
			}
			return null;
		}
		
		Direction getDirectionHelper(ILiteral i, IBinaryExpression be) {
			int step = Integer.parseInt(i.getStringValue());

			if(stepSize != Integer.MIN_VALUE && step != stepSize) return null;	//step size must always be the same
			else if (stepSize == Integer.MIN_VALUE) stepSize = step;

			return calculateDirection(be.getOperator(), step);
		}
	}
	
	static Direction calculateDirection(IOperator op, int step) {	//does not check if step == 0
		if((op == IOperator.ADD && step > 0) || op == IOperator.SUB && step < 0)
			return Direction.INC;
		else 
			return Direction.DEC;
	}
	
	public static class Stepper implements IStepper {

		private Direction direction;
		
		public Stepper(Direction direction) {
			this.direction = direction;
		}
		
		@Override
		public Direction getDirection() {
			return direction;
		}
		
		
		@Override
		public String toString() {
			return getName() + "(" + getDirection() + ")";
		}
	}
}
