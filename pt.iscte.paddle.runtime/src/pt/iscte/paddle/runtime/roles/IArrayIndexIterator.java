package pt.iscte.paddle.runtime.roles;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IVariableRole;

public interface IArrayIndexIterator extends IStepper {
	
	/*
	 * Para casos como por exemplo: v[i] = q[i] + ...
	 * o 2º vetor não é detetado
	 */
	
	List<IVariableDeclaration> getArrayVariables();	//arrays em que a variavel é usada
	
	@Override
	default String getName() {
		return "Array Index Iterator";
	}
	
	static boolean isArrayIndexIterator(IVariableDeclaration var) {
		assert(IStepper.isStepper(var));
		Visitor v = new Visitor(var);
		var.getOwnerProcedure().accept(v);
		return v.isUsedInArrays();
	}
	
	static IVariableRole createArrayIndexIterator(IVariableDeclaration var) {
		assert isArrayIndexIterator(var);
		Stepper stepper = (Stepper) IStepper.createStepper(var);
		Visitor v = new Visitor(var);
		var.getOwnerProcedure().accept(v);
		return new ArrayIndexIterator(stepper.getDirection(), v.arrayVariables);
	}
	
	class Visitor implements IBlock.IVisitor {
		
		final IVariableDeclaration var;
		List<IVariableDeclaration> arrayVariables = new ArrayList<>();
		
		public Visitor(IVariableDeclaration var) {
			this.var = var;
		}
		
		@Override
		public boolean visit(IArrayElement arrayElement) {	//... = v[i] + ....
			//System.out.println(exp.getIndexes());
			
			for(IExpression exp : arrayElement.getIndexes()) {
				IVariableDeclaration var = ((IVariableExpression) exp).getVariable();
				if(var.equals(this.var) && !arrayVariables.contains(var))
					arrayVariables.add(var);
			}
			
			/*if(arrayElement.getIndexes().contains(var.expression().expression())) {
				System.out.println(arrayElement);
				IVariableDeclaration v = (IVariableDeclaration) arrayElement.getTarget();
				if(!arrayVariables.contains(v))
					arrayVariables.add(v);
			}*/
			return false;
		}
		
		@Override
		public boolean visit(IArrayElementAssignment assignment) {	//v[i] = ...
			
			for(IExpression exp : assignment.getIndexes()) {
				IVariableDeclaration var = ((IVariableExpression) exp).getVariable();
				if(var.equals(this.var) && !arrayVariables.contains(var))
					arrayVariables.add(var);
			}
			
			/*if(assignment.getIndexes().contains(var.expression().expression())) {
				System.out.println(assignment);
				IVariableDeclaration v = (IVariableDeclaration) assignment.getTarget();
				if(!arrayVariables.contains(v))
					arrayVariables.add(v);
			}*/
			return false;
		}
		
		public boolean isUsedInArrays() {
			return arrayVariables.size() > 0;
		}
	}
	
	public static class ArrayIndexIterator implements IArrayIndexIterator {
		
		private Direction direction;
		private List<IVariableDeclaration> arrayVariables;
		
		public ArrayIndexIterator(Direction direction, List<IVariableDeclaration> arrayVariables) {
			this.direction = direction;
			this.arrayVariables = arrayVariables;
		}

		@Override
		public Direction getDirection() {
			return direction;
		}

		@Override
		public List<IVariableDeclaration> getArrayVariables() {
			return arrayVariables;
		}
		
		@Override
		public String toString() {
			return getName() + "(" + getDirection() + ", " + getArrayVariables() + ")";
		}
	}
}
