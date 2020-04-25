package pt.iscte.paddle.runtime.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import pt.iscte.paddle.interpreter.IReference;
import pt.iscte.paddle.runtime.InterfaceColors;

public class ArrayIndexErrorDraw extends Canvas {
	
	private static final int canvasSizeX = 200;
	private static final int canvasSizeY = 80;
	//-------- Rectangle constants --------//
	private static final int rectangleStartY = 10;						//Onde começar a desenhar o retangulo
	private static final int rectangleSizeY = 60;
	//-------- Rectangle constants --------//
	//-------- Square constants --------//
	private static final int maxArraySize = 8;							//Número de posições da array desenhada
	private static final int squareStartY = rectangleStartY + 10;		//Onde começar a desenhar os quadrados no retangulo na vertical
	private static final int squareSizeY = 30;							//Tamanho dos quadrados na vertical
	private static final int squareStartX = 5;							//Onde começar a desenhar os quadrados no retangulo na horizontal
	//-------- Square constants --------//
	private static final Font boldFont = new Font(null, "Arial", 11, SWT.BOLD);
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
	
	public void draw(IReference arrayReference, int errorPosition) {
		if(paintListener != null) removePaintListener(paintListener);
		
		String [] array = stringToArray(arrayReference.getValue().toString());
		
		paintListener = new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				int arrayLength = array.length > maxArraySize ? maxArraySize : array.length;
				
				GC gc = e.gc;
				gc.setBackground(InterfaceColors.BLACK.getColor());
				gc.setAntialias(SWT.ON);
																				//Horizontal
				int availableSpaceX = (getSize().x - 10) / (arrayLength + 1);	//Espaço que cada quadrado ocupa (quadrado em si + margem esquerda e direita
				int spacingX = availableSpaceX / 10;							//margem de um dos lados
				int sizeX = availableSpaceX - (spacingX * 2);					//tamanho do quadrado em si
				
				int centerY = (squareStartY + (squareStartY + squareSizeY)) / 2;
				
//				gc.setBackground(InterfaceColors.WHITE.getColor());
				int offset = 0;
				if(errorPosition < 0) {
					offset++;
					gc.fillRectangle(squareStartX + availableSpaceX, rectangleStartY, getSize().x - 10 - availableSpaceX, rectangleSizeY);
					drawErrorPosition(Integer.toString(errorPosition), gc, availableSpaceX, spacingX, sizeX, centerY, 0);
				} else {
					gc.fillRectangle(squareStartX, rectangleStartY, getSize().x - 10 - availableSpaceX, rectangleSizeY);
					drawErrorPosition(Integer.toString(errorPosition), gc, availableSpaceX, spacingX, sizeX, centerY, array.length);
				}
				
				if(array.length > maxArraySize) {
					for(int i = 0; i < maxArraySize - 3; i++) {
						drawSquare(array[i], i + "", gc, availableSpaceX, spacingX, sizeX, centerY, i + offset);
					}
					drawSquare("...", "...", gc, availableSpaceX, spacingX, sizeX, centerY, maxArraySize - 3 + offset);
					drawSquare(array[array.length - 2], (array.length - 2) + "", gc, availableSpaceX, spacingX, sizeX, centerY, maxArraySize - 2 + offset);
					drawSquare(array[array.length - 1], (array.length - 1) + "", gc, availableSpaceX, spacingX, sizeX, centerY, maxArraySize - 1 + offset);
				} else {
					for(int i = 0; i < arrayLength; i++) {
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
		int currentX = (i * availableSpaceX) + squareStartX;
		gc.fillRectangle(currentX + spacingX, squareStartY, sizeX, squareSizeY);
		
		int centerX = (currentX + (currentX + sizeX + spacingX * 2)) / 2;
		
		gc.setFont(boldFont);
		gc.setForeground(InterfaceColors.BLACK.getColor());
		Point boldTextSize = gc.textExtent(text);
		int boldTextX = centerX - (boldTextSize.x / 2);
		int textY = centerY - (boldTextSize.y / 2);
		gc.drawString(text, boldTextX, textY);						//Text inside square
		
		gc.setFont(normalFont);
		gc.setForeground(InterfaceColors.WHITE.getColor());
		Point normalTextSize = gc.textExtent(positionText);
		int normalTextX = centerX - (normalTextSize.x / 2);
		gc.drawString(positionText, normalTextX, centerY + (squareSizeY / 2) + 5, true);
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
		gc.drawString(positionText, normalTextX, centerY + (squareSizeY / 2) + 5, true);
		
//		drawArrow(gc, centerX, centerY - 60, centerX, centerY - 20, 15, Math.toRadians(45));
	}
	
	//Draws an arrow
//	private static void drawArrow(GC gc, int x1, int y1, int x2, int y2, double arrowLength, double arrowAngle) {
//		gc.setForeground(InterfaceColors.RED.getColor());
//		gc.setBackground(InterfaceColors.RED.getColor());
//		
//	    double theta = Math.atan2(y2 - y1, x2 - x1);
//	    double offset = (arrowLength - 2) * Math.cos(arrowAngle);
//
//	    gc.drawLine(x1, y1, (int)(x2 - offset * Math.cos(theta)), (int)(y2 - offset * Math.sin(theta)));
//
//	    Path path = new Path(gc.getDevice());
//	    path.moveTo((float)(x2 - arrowLength * Math.cos(theta - arrowAngle)), (float)(y2 - arrowLength * Math.sin(theta - arrowAngle)));
//	    path.lineTo((float)x2, (float)y2);
//	    path.lineTo((float)(x2 - arrowLength * Math.cos(theta + arrowAngle)), (float)(y2 - arrowLength * Math.sin(theta + arrowAngle)));
//	    path.close();
//
//	    gc.fillPath(path);
//
//	    path.dispose();
//	}
	
	public static String[] stringToShrinkedArray(String s) {
		String s2 = s.substring(1, s.length()-1);
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
		String s2 = s.substring(1, s.length()-1);
		return s2.trim().replaceAll(" ", "").split(",");
	}
	
}