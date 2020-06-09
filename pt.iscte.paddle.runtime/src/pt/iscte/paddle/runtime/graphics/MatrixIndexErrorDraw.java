package pt.iscte.paddle.runtime.graphics;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import pt.iscte.paddle.runtime.variableInfo.ArrayVariableInfo;
import pt.iscte.paddle.runtime.variableInfo.ArrayVariableInfo.Coordinates;

public class MatrixIndexErrorDraw extends Composite {
	
	private static final int compositeSizeX = 380;	//250
	private static final int compositeSizeY = 500;	//110

	public MatrixIndexErrorDraw(Composite comp) {
		super(comp, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		setLayout(layout);
	}
	
	//Canvas Size
	@Override
	public Point computeSize(int wHint, int hHint) {
		return new Point(compositeSizeX, compositeSizeY);
	}

	//Canvas Size
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(compositeSizeX, compositeSizeY);
	}

	public void draw(ArrayVariableInfo info, int errorPosition, int originalArraySize) {	//TODO pensar como receber posição com erro
		if(info.getAccessedPositions().get(0).getCoordinates().size()!= 2) {	//Only Draw 2 dimensions
			return;
		}
		
		List<String []> matrix = stringToMatrix(info.getReference().getValue().toString());
		List<Coordinates> accessedPositions = info.getAccessedPositions();
		
		VerticalArrayDraw verticalArrayDraw = new VerticalArrayDraw(this);
		if(info.getLengthExpressions() == null)
			verticalArrayDraw.drawArray(null, false, errorPosition, originalArraySize);
		else
			verticalArrayDraw.drawArray(info.getLengthExpressions().get(0), false, errorPosition, originalArraySize);
		
		Composite rightSide = new Composite(this, SWT.NONE);
		GridLayout rightSideLayout = new GridLayout();
//		rightSideLayout.verticalSpacing = verticalArrayDraw.getSpacingY();
		rightSideLayout.verticalSpacing = 0;
		rightSide.setLayout(rightSideLayout);
		
		for(int i = 0; i < matrix.size(); i++) {
			List<Coordinates> oneDimensionCoordinates = new ArrayList<>(); //TODO evitar esta conversão
			for(Coordinates coordinates : accessedPositions) {
				if (coordinates.getCoordinates().get(0) == i) {
					List<Integer> l = new ArrayList<>();
					l.add(coordinates.getCoordinates().get(1));
					oneDimensionCoordinates.add(info.new Coordinates(l));
				}
			}
			
			ArrayIndexErrorDraw arrayDraw = new ArrayIndexErrorDraw(rightSide);
			arrayDraw.drawArray(matrix.get(i), oneDimensionCoordinates, null, false, errorPosition, false, originalArraySize);
		}
	}
	
	//Returns a List with a structure very similar to a matrix
	public static List<String []> stringToMatrix(String s) {
		String s2 = s.substring(s.indexOf("[") + 1, s.length()-1).trim().replaceAll(" ", "");

		final List<String []> matrix = new ArrayList<>();

		int i = 0;
		int next = s2.indexOf("],", 1) + 1;
		while(next != 0) {						//0 porque next = indexOf + 1
			matrix.add(ArrayIndexErrorDraw.stringToArray(s2.substring(i, next)));
			i = next + 1;
			next = s2.indexOf("],", i + 1) + 1;
		}
		matrix.add(ArrayIndexErrorDraw.stringToArray(s2.substring(i)));

		int maxLength = 0;
		for(String [] v : matrix) {
			if(v.length > maxLength) maxLength = v.length;
		}

		return matrix;
	}
}
