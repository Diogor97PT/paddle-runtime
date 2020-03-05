package pt.iscte.paddle.runtime.roles.impl;

import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.runtime.roles.IFixedValue;

public class FixedValue implements IFixedValue {
	
	private boolean isModified;
	
	public FixedValue(IVariableDeclaration var) {
		assert isFixedValue(var);
		Visitor v = new Visitor(var);
		var.getOwnerProcedure().accept(v);
		this.isModified = v.isModified;
	}
	
	public static boolean isFixedValue(IVariableDeclaration var) {
		Visitor v = new Visitor(var);
		var.getOwnerProcedure().accept(v);
		return v.isValid;
	}
	
	public boolean isModified() {
		return isModified;
	}
	
	@Override
	public String toString() {
		if(isModified)
			return getName() + " array that has been modified";
		else
			return getName();
	}
	
	private static class Visitor implements IBlock.IVisitor {		//acrescentar para objetos (records)
		final IVariableDeclaration var;
		
		boolean isValid = true;	//valid until assigned
		boolean first;			//if is first assignment
		
		boolean isModified;		//true if variable is an array and is modified internally
		
		public Visitor(IVariableDeclaration var) {
			this.var = var;
			
			if(var.getOwnerProcedure().getParameters().contains(var))	//if var is parameter of function, it's value is already assigned
				first = false;
			else
				first = true;
			
		}
		
		@Override
		public boolean visit(IVariableAssignment assignment) {
			if(assignment.getTarget().equals(var)) {
				isModified = false;
				if(first)
					first = false;
				else if(isValid)
					isValid = false;
			}
			return false;
		}
		
		@Override
		public boolean visit(IArrayElementAssignment assignment) {
			if(((IVariableExpression)assignment.getTarget()).getVariable().equals(var)) {
				isModified = true;
			}
			return false;
		}
		
	}
	
}
