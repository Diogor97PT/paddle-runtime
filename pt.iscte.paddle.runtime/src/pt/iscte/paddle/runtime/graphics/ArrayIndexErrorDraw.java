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
import pt.iscte.paddle.runtime.InterfaceColors;

public class ArrayIndexErrorDraw extends Canvas {
	
	//-------- Rectangle constants --------//
	private static final int rectangleStartY = 40;						//Onde começar a desenhar o retangulo
	private static final int rectangleSizeY = 120;
	//-------- Rectangle constants --------//
	//-------- Square constants --------//
	private static final int maxArraySize = 10;
	private static final int squareStartY = rectangleStartY + 20;		//Onde começar a desenhar os quadrados no retangulo na vertical
	private static final int squareSizeY = 60;							//Tamanho dos quadrados na vertical
	private static final int squareStartX = 10;							//Onde começar a desenhar os quadrados no retangulo na horizontal
	//-------- Square constants --------//
	private static final Font boldFont = new Font(null, "Arial", 20, SWT.BOLD);
	private static final Font normalFont = new Font(null, "Arial", 15, SWT.NORMAL);
	
	private PaintListener paintListener;

	public ArrayIndexErrorDraw(Composite comp) {
		super(comp, SWT.BORDER);
//		setSize(500, 500);
		
//		canvas.setSize(2000, canvas.getSize().y);
		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.setAntialias(SWT.ON);
				e.gc.drawString("Representação do Erro", 10, 10);
			}
		});
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
				int availableSpaceX = (getSize().x - 20) / (arrayLength + 1);	//Espaço que cada quadrado ocupa (quadrado em si + margem esquerda e direita
				int spacingX = availableSpaceX / 10;							//margem de um dos lados
				int sizeX = availableSpaceX - (spacingX * 2);					//tamanho do quadrado em si
				
				gc.fillRoundRectangle(squareStartX, rectangleStartY, getSize().x - 20, rectangleSizeY, 30, 30);
				
				int centerY = (squareStartY + (squareStartY + squareSizeY)) / 2;
				
				gc.setBackground(InterfaceColors.WHITE.getColor());
				if(array.length > maxArraySize) {
					int offset = 0;
					if(errorPosition < 0) {
						offset++;
						drawErrorPosition(errorPosition + "", gc, availableSpaceX, spacingX, sizeX, centerY, 0);
					} else {
						drawErrorPosition(errorPosition + "", gc, availableSpaceX, spacingX, sizeX, centerY, maxArraySize);
					}
					
					for(int i = 0; i < maxArraySize - 3; i++) {
						drawSquare(array[i], i + "", gc, availableSpaceX, spacingX, sizeX, centerY, i + offset);
					}
					drawSquare("...", "...", gc, availableSpaceX, spacingX, sizeX, centerY, maxArraySize - 3 + offset);
					drawSquare(array[array.length - 2], (array.length - 2) + "", gc, availableSpaceX, spacingX, sizeX, centerY, maxArraySize - 2 + offset);
					drawSquare(array[array.length - 1], (array.length - 1) + "", gc, availableSpaceX, spacingX, sizeX, centerY, maxArraySize - 1 + offset);
				} else {
					int offset = 0;
					if(errorPosition < 0) {
						offset++;
						drawErrorPosition(Integer.toString(errorPosition), gc, availableSpaceX, spacingX, sizeX, centerY, 0);
					} else {
						drawErrorPosition(Integer.toString(errorPosition), gc, availableSpaceX, spacingX, sizeX, centerY, array.length);
					}
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
		gc.setForeground(InterfaceColors.WHITE.getColor());
		int currentX = (i * availableSpaceX) + squareStartX;
		gc.drawOval(currentX + spacingX, squareSizeY, sizeX, squareSizeY);
		
		int centerX = (currentX + (currentX + sizeX + spacingX * 2)) / 2;
		
		gc.setFont(normalFont);
		Point normalTextSize = gc.textExtent(positionText);
		int normalTextX = centerX - (normalTextSize.x / 2);
		gc.drawString(positionText, normalTextX, centerY + (squareSizeY / 2) + 5, true);
		
		drawArrow(gc, centerX, centerY - 80, centerX, centerY - 40, 20, Math.toRadians(45));
	}
	
	//Draws an arrow
	public static void drawArrow(GC gc, int x1, int y1, int x2, int y2, double arrowLength, double arrowAngle) {
		gc.setForeground(InterfaceColors.BLUE.getColor());
		gc.setBackground(InterfaceColors.BLUE.getColor());
		
	    double theta = Math.atan2(y2 - y1, x2 - x1);
	    double offset = (arrowLength - 2) * Math.cos(arrowAngle);

	    gc.drawLine(x1, y1, (int)(x2 - offset * Math.cos(theta)), (int)(y2 - offset * Math.sin(theta)));

	    Path path = new Path(gc.getDevice());
	    path.moveTo((float)(x2 - arrowLength * Math.cos(theta - arrowAngle)), (float)(y2 - arrowLength * Math.sin(theta - arrowAngle)));
	    path.lineTo((float)x2, (float)y2);
	    path.lineTo((float)(x2 - arrowLength * Math.cos(theta + arrowAngle)), (float)(y2 - arrowLength * Math.sin(theta + arrowAngle)));
	    path.close();

	    gc.fillPath(path);

	    path.dispose();
	}
	
	public static String[] stringToArray(String s) {
		String s2 = s.substring(1, s.length()-1);
		return s2.trim().replaceAll(" ", "").split(",");
	}
	
}