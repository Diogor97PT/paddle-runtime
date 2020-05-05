package pt.iscte.paddle.runtime.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import pt.iscte.paddle.interpreter.IReference;
import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.runtime.InterfaceColors;

public class ArrayIndexErrorDraw extends Canvas {
	
	private static final int canvasSizeX = 250;
	private static final int canvasSizeY = 110;
	
	//-------- Rectangle constants --------//
	private static final int rectangleStartY = 5;						//Onde começar a desenhar o retangulo
	private static final int rectangleSizeY = 60;
	//-------- Rectangle constants --------//
	
	//-------- Square constants --------//
	private static final int maxArraySize = 8;							//Número de posições da array desenhada
	private static final int squareStartY = rectangleStartY + 10;		//Onde começar a desenhar os quadrados no retangulo na vertical
	private static final int squareSizeY = 30;							//Tamanho dos quadrados na vertical
	private static final int squareStartX = 5;							//Onde começar a desenhar os quadrados no retangulo na horizontal
	//-------- Square constants --------//
	
	private static final Font boldFont = new Font(null, "Arial", 10, SWT.BOLD);
	private static final Font normalFont = new Font(null, "Arial", 7, SWT.NORMAL);
	
	private PaintListener paintListener;

	public ArrayIndexErrorDraw(Composite comp) {
		super(comp, SWT.BORDER);
	}
	
	//Tamanho do canvas
	@Override
	public Point computeSize(int wHint, int hHint) {
		return new Point(canvasSizeX, canvasSizeY);
	}
	
	//Tamanho do canvas
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(canvasSizeX, canvasSizeY);
	}
	
	//TODO acertar melhor o desenho
	public void draw(IReference arrayReference, int errorPosition, IExpression errorExpression, int originalArraySize) {
		if(paintListener != null) removePaintListener(paintListener);
		
		String [] array = stringToArray(arrayReference.getValue().toString());
		
		paintListener = new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				int arraySize = array.length > maxArraySize ? maxArraySize : array.length;
				
				GC gc = e.gc;
				gc.setBackground(InterfaceColors.GRAY.getColor());
				gc.setAntialias(SWT.ON);
																				//Horizontal
				int availableSpaceX = (getSize().x - 10) / (arraySize + 1);		//Espaço que cada quadrado ocupa (quadrado em si + margem esquerda e direita
				int spacingX = availableSpaceX / 8;								//margem de um dos lados
				int sizeX = availableSpaceX - (spacingX * 2);					//tamanho do quadrado em si
				
				int centerY = (squareStartY + (squareStartY + squareSizeY)) / 2;
				
				int rectangleSizeX = getSize().x - 10 - availableSpaceX;
				
				int offset = 0;
				if(errorPosition < 0) offset++;
				
				int rectangleStartX = squareStartX + (availableSpaceX * offset);
				gc.fillRoundRectangle(rectangleStartX, rectangleStartY, rectangleSizeX, rectangleSizeY, 10, 10);
				
				int errorOffset = offset ^ 1;				//XOR -> if offset = 1, errorOffset = 0 and vice-versa (inverts errorOffset value)
				drawErrorPosition(Integer.toString(errorPosition), gc, availableSpaceX, spacingX, sizeX, centerY, (arraySize * errorOffset));
				
				drawArraySize(gc, rectangleStartX, rectangleStartX + (rectangleSizeX / 2), rectangleStartX + rectangleSizeX, centerY + (squareSizeY / 2) + 10, errorExpression, originalArraySize);
				
				if(array.length > maxArraySize) {
					for(int i = 0; i < maxArraySize - 3; i++) {
						drawSquare(array[i], i + "", gc, availableSpaceX, spacingX, sizeX, centerY, i + offset);
					}
					drawSquare("...", "...", gc, availableSpaceX, spacingX, sizeX, centerY, maxArraySize - 3 + offset);
					drawSquare(array[array.length - 2], (array.length - 2) + "", gc, availableSpaceX, spacingX, sizeX, centerY, maxArraySize - 2 + offset);
					drawSquare(array[array.length - 1], (array.length - 1) + "", gc, availableSpaceX, spacingX, sizeX, centerY, maxArraySize - 1 + offset);
				} else {
					for(int i = 0; i < arraySize; i++) {
						drawSquare(array[i], i + "", gc, availableSpaceX, spacingX, sizeX, centerY, i + offset);
					}
				}
			}
		};
		addPaintListener(paintListener);
		redraw();
	}
	
	//Draws a square with the value inside
	private void drawSquare(String text, String positionText, GC gc, int availableSpaceX, int spacingX, int sizeX, int centerY, int i) {
		gc.setBackground(InterfaceColors.WHITE.getColor());
		gc.setForeground(InterfaceColors.GREEN.getColor());
		int currentX = (i * availableSpaceX) + squareStartX;
		gc.fillRectangle(currentX + spacingX, squareStartY, sizeX, squareSizeY);
		gc.drawRectangle(currentX + spacingX, squareStartY, sizeX, squareSizeY);
		
		int centerX = (currentX + (currentX + sizeX + spacingX * 2)) / 2;
		
		gc.setFont(boldFont);
		gc.setForeground(InterfaceColors.BLACK.getColor());
		Point boldTextSize = gc.textExtent(text);
		int boldTextX = centerX - (boldTextSize.x / 2);
		int textY = centerY - (boldTextSize.y / 2);
		gc.drawText(text, boldTextX, textY);						//Text inside square
		
		gc.setFont(normalFont);
		gc.setForeground(InterfaceColors.WHITE.getColor());
		Point normalTextSize = gc.textExtent(positionText);
		int normalTextX = centerX - (normalTextSize.x / 2);
		gc.drawText(positionText, normalTextX, centerY + (squareSizeY / 2) + 5, true);
	}
	
	//Draws the position where the error happened
	private void drawErrorPosition(String positionText, GC gc, int availableSpaceX, int spacingX, int sizeX, int centerY, int i) {
		gc.setForeground(InterfaceColors.RED.getColor());
		gc.setLineStyle(SWT.LINE_DASH);
		gc.setLineWidth(2);
		int currentX = (i * availableSpaceX) + squareStartX;
		gc.drawRectangle(currentX + spacingX, squareStartY, sizeX, squareSizeY);
		
		int centerX = (currentX + (currentX + sizeX + spacingX * 2)) / 2;
		
		gc.setFont(normalFont);
		Point normalTextSize = gc.textExtent(positionText);
		int normalTextX = centerX - (normalTextSize.x / 2);
		gc.drawText(positionText, normalTextX, centerY + (squareSizeY / 2) + 5, true);
	}
	
	//Draws the symbol that represents the array size
	private void drawArraySize(GC gc, int rectStartX, int rectCenterX, int rectEndX, int startY, IExpression errorExpression, int arraySize) {
		gc.setForeground(InterfaceColors.BLACK.getColor());
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setFont(boldFont);
		
		Path path = new Path(getDisplay());
		int arcSize = 30;
		
		//Left half
		path.addArc(rectStartX, startY, arcSize, 20, 180, 90);
		path.addArc(rectCenterX - arcSize, startY + 20, arcSize, 20, 90, -90);
		
		//Right half
		path.addArc(rectCenterX, startY + 20, arcSize, 20, 180, -90);
		path.addArc(rectEndX - arcSize, startY, arcSize, 20, 270, 90);
		
		gc.drawPath(path);
		
		String errorExpressionString = arraySize + " (" + errorExpression.toString() + ")";
		Point textSize = gc.textExtent(errorExpressionString);
		gc.drawText(errorExpressionString, rectCenterX - (textSize.x / 2), startY + 30, true);
		
		path.dispose();
	}
	
	public static String[] stringToShrinkedArray(String s) {
		String s2 = s.substring(s.indexOf("[") + 1, s.length()-1);
		String [] array = s2.trim().replaceAll(" ", "").split(",");
		if(array.length > maxArraySize) {
			String [] shrinkedArray = new String [maxArraySize];
			for(int i = 0; i < maxArraySize - 3; i++) {
				shrinkedArray[i] = array[i];
			}
			shrinkedArray[maxArraySize-3] = "...";
			shrinkedArray[maxArraySize-2] = array[array.length - 2];
			shrinkedArray[maxArraySize-1] = array[array.length - 1];
			return shrinkedArray; 
		} else 
			return array;
	}
	
	public static String[] stringToArray(String s) {
		String s2 = s.substring(s.indexOf("[") + 1, s.length()-1);
		return s2.trim().replaceAll(" ", "").split(",");
	}
	
}