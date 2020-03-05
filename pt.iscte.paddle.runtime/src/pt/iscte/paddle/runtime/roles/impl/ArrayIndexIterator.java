package pt.iscte.paddle.runtime.roles.impl;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.model.IArrayElement;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.runtime.roles.IArrayIndexIterator;

public class ArrayIndexIterator implements IArrayIndexIterator {
	
	/*
	 * Para casos como por exemplo: v[i] = q[i] + ...
	 * o 2º vetor não é detetado
	 */
	
	private Direction direction;
	private List<IVariableDeclaration> arrayVariables;
	
	public ArrayIndexIterator(IVariableDeclaration var) {
		assert isArrayIndexIterator(var);
		Stepper stepper = new Stepper(var);
		Visitor v = new Visitor(var);
		var.getOwnerProcedure().accept(v);
		this.direction = stepper.getDirection();
		this.arrayVariables = v.arrayVariables;
	}
	
	public static boolean isArrayIndexIterator(IVariableDeclaration var) {
		assert(Stepper.isStepper(var));
		Visitor v = new Visitor(var);
		var.getOwnerProcedure().accept(v);
		return v.isUsedInArrays();
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
	
	private static class Visitor implements IBlock.IVisitor {
		
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

}
