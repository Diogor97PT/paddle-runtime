package pt.iscte.paddle.runtime.roles;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.roles.IVariableRole;

public interface IArrayIndexIterator extends IStepper {
	
	/*
	 * Para casos como por exemplo: v[i] = q[i] + ...
	 * o 2º vetor não é detetado
	 */
	
	List<IVariable> getArrayVariables();	//arrays em que a variavel é usada
	
	@Override
	default String getName() {
		return "Array Index Iterator";
	}
	
	static boolean isArrayIndexIterator(IVariable var) {
		assert(IStepper.isStepper(var));
		Visitor v = new Visitor(var);
		var.getOwnerProcedure().accept(v);
		return v.isUsedInArrays();
	}
	
	static IVariableRole createArrayIndexIterator(IVariable var) {
		assert isArrayIndexIterator(var);
		Stepper stepper = (Stepper) IStepper.createStepper(var);
		Visitor v = new Visitor(var);
		var.getOwnerProcedure().accept(v);
		return new ArrayIndexIterator(stepper.getDirection(), v.arrayVariables);
	}
	
	class Visitor implements IBlock.IVisitor {
		
		final IVariable var;
		List<IVariable> arrayVariables = new ArrayList<>();
		
		public Visitor(IVariable var) {
			this.var = var;
		}
		
		@Override
		public boolean visit(IArrayElement arrayElement) {	//... = v[i] + ....
			//System.out.println(exp.getIndexes());
			if(arrayElement.getIndexes().contains(var)) {
				System.out.println(arrayElement);
				IVariable v = (IVariable) arrayElement.getTarget();
				if(!arrayVariables.contains(v))
					arrayVariables.add(v);
			}
			return false;
		}
		
		@Override
		public boolean visit(IArrayElementAssignment assignment) {	//v[i] = ...
			if(assignment.getIndexes().contains(var)) {
				System.out.println(assignment);
				IVariable v = (IVariable) assignment.getTarget();
				if(!arrayVariables.contains(v))
					arrayVariables.add(v);
			}
			return false;
		}
		
		public boolean isUsedInArrays() {
			return arrayVariables.size() > 0;
		}
	}
	
	public static class ArrayIndexIterator implements IArrayIndexIterator {
		
		private Direction direction;
		private List<IVariable> arrayVariables;
		
		public ArrayIndexIterator(Direction direction, List<IVariable> arrayVariables) {
			this.direction = direction;
			this.arrayVariables = arrayVariables;
		}

		@Override
		public Direction getDirection() {
			return direction;
		}

		@Override
		public List<IVariable> getArrayVariables() {
			return arrayVariables;
		}
		
		@Override
		public String toString() {
			return getName() + "(" + getDirection() + ", " + getArrayVariables() + ")";
		}
	}
}
