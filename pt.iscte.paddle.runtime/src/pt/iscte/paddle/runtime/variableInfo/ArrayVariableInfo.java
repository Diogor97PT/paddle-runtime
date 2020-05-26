package pt.iscte.paddle.runtime.variableInfo;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.paddle.interpreter.IReference;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.model.IVariableDeclaration;

public class ArrayVariableInfo extends VariableInfo {

	private List<Coordinates> accessedPositions = new ArrayList<>();
	private List<IExpression> lengthExpressions;
	
	public ArrayVariableInfo(IVariableDeclaration variableDeclaration, VariableType variableType, IReference reference, 
			String value, List<IExpression> lengthExpressions) {
		
		super(variableDeclaration, variableType, reference, value);
		this.lengthExpressions = lengthExpressions;
	}
	
	public void addArrayAccessInformation(String value, List<Integer> coordinates) {
		accessedPositions.add(new Coordinates(coordinates));
		super.addVarValue(value);
	}

	public List<Coordinates> getAccessedPositions() {
		return accessedPositions;
	}
	
	public List<IExpression> getLengthExpressions() {
		return lengthExpressions;
	}
	
	public class Coordinates {
		
		private List<Integer> coordinates;
		
		public Coordinates(List<Integer> coordinates) {
			this.coordinates = coordinates;
		}
		
		public List<Integer> getCoordinates() {
			return coordinates;
		}
		
		@Override
		public boolean equals(Object obj) {
			Coordinates coord = (Coordinates) obj;
			
			if(this.coordinates.size() != coord.getCoordinates().size()) return false;
			
			for(int i = 0; i < this.coordinates.size(); i++) {
				if(this.coordinates.get(i) != coord.getCoordinates().get(i))
					return false;
			}
			
			return true;
		}
		
		@Override
		public String toString() {
			String s = "";
			for(int coord : coordinates) {
				s += coord + " ";
			}
//			s += "\n";
			return s;
		}
	}
}
