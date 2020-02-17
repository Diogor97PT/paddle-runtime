package pt.iscte.paddle.runtime.roles;

import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.IVariableAssignment;
import pt.iscte.paddle.roles.IVariableRole;

public interface IFixedValue extends IVariableRole {
	
	default String getName() {
		return "Fixed_Value";
	}

	static boolean isFixedValue(IVariable var) {
		Visitor v = new Visitor(var);
		var.getOwnerProcedure().accept(v);
		return v.isValid;
	}
	
	/*static IVariableRole createFixedValue(IVariable var) {
		
	}*/
	
	class Visitor implements IBlock.IVisitor {
		final IVariable var;
		
		boolean isValid = true;	//valid until assigned
		boolean first;
		
		public Visitor(IVariable var) {
			this.var = var;
			
			if(var.getOwnerProcedure().getParameters().contains(var))
				first = false;
			else
				first = true;
		}
		
		@Override
		public boolean visit(IVariableAssignment assignment) {
			if(assignment.getTarget().equals(var)) {
				//System.out.println(assignment);
				if(first) {
					first = false;
				} else if(isValid)
					isValid = false;
			}
			return false;
		}
		
		@Override
		public boolean visit(IArrayElementAssignment assignment) {
			//System.out.println(assignment);
			return false;
		}
		
	}
	
	public static class FixedValue implements IFixedValue {
		
		@Override
		public String toString() {
			return getName();
		}
		
	}
}
