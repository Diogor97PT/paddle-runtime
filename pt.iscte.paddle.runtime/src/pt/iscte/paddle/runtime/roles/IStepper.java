package pt.iscte.paddle.runtime.roles;

import pt.iscte.paddle.model.IBinaryExpression;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.ILiteral;
import pt.iscte.paddle.model.IOperator;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.roles.IVariableRole;
import pt.iscte.paddle.roles.IGatherer.Gatherer;
import pt.iscte.paddle.roles.IGatherer.Visitor;

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
		return v.isValid && v.direction != null; //trocar
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
		int firstValue;
		
		Direction direction = null;
		int stepSize;
		
		boolean isValid = true; //true até prova do contrário
		
		public Visitor(IVariable var) {
			this.var = var;
		}
		
		@Override
		public boolean visit(IVariableAssignment assignment) {
			if(assignment.getTarget().equals(var)){
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
				IExpression left = be.getLeftOperand();							//left e right -> ex:  v = left + right
				IExpression right = be.getRightOperand();
				if(be.getOperator() == IOperator.ADD || be.getOperator() == IOperator.SUB) {		//Stepper only sums or subtracts
					if(left instanceof IVariable && (((IVariable)left).equals(var.getTarget()) && right instanceof ILiteral)){ //left variable and right literal
						ILiteral i = (ILiteral) right;
						System.out.println(i.getStringValue());
						return calculateDirection(be.getOperator());
					} else if (right instanceof IVariable && (((IVariable)right).equals(var.getTarget()) && left instanceof ILiteral)) { //left literal and right variable
						ILiteral i = (ILiteral) right;
						System.out.println(i.getStringValue());
						return calculateDirection(be.getOperator());
					}
				}
			}
			return null;
		}
		
	}
	
	static Direction calculateDirection(IOperator op) {
		if(op == IOperator.ADD)
			return Direction.INC;
		else 
			return Direction.DEC;
	}
	
	static class Stepper implements IStepper {

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
