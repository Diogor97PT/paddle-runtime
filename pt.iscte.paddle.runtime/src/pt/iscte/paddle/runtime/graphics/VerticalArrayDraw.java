package pt.iscte.paddle.runtime.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import pt.iscte.paddle.model.IExpression;
import pt.iscte.paddle.runtime.InterfaceColors;

public class VerticalArrayDraw extends Canvas {

	private static int canvasSizeX = 110;
//	private static int canvasSizeY = 250;
	
	private int canvasSizeY = 250;
	
//	private static final int maxArraySize = 8;
	
	private int rectangleStartX = 45;
	private static final int rectangleSizeX = 60;
	
	private int squareStartX;
	private static final int squareSizeX = 30;							//Tamanho dos quadrados na vertical
	private static final int squareStartY = 5;
	
	private static final Font boldFont = new Font(null, "Arial", 10, SWT.BOLD);
	private static final Font normalFont = new Font(null, "Arial", 7, SWT.NORMAL);
	
	private PaintListener paintListener;
	
	public VerticalArrayDraw(Composite parent) {
		super(parent, SWT.NONE);
	}

	@Override
	public Point computeSize(int wHint, int hHint) {
		return new Point(canvasSizeX, canvasSizeY);
	}
	
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(canvasSizeX, canvasSizeY);
	}
	
	void drawArray(IExpression expression, boolean showErrorPosition, int errorPosition, int originalArraySize, int maxArraySize) {
		if(paintListener != null) removePaintListener(paintListener);
		
		squareStartX = rectangleStartX + 18;
		
		int arraySize = originalArraySize > maxArraySize ? maxArraySize : originalArraySize;
		
		canvasSizeY = arraySize * 65;
		if(showErrorPosition)
			canvasSizeY += 65;
//		canvasSizeY = arraySize * 100;
		
		paintListener = new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				gc.setBackground(InterfaceColors.GRAY.getColor());
				gc.setAntialias(SWT.ON);
																				//Vertical
				int availableSpaceY;											//Espaço que cada quadrado ocupa (quadrado em si + margem cima e baixo)
				if(showErrorPosition)
					availableSpaceY = (getSize().y - 10) / (arraySize + 1);
				else
					availableSpaceY = (getSize().y - 10) / (arraySize);
				
				int spacingY = availableSpaceY / 8;								//margem de um dos lados
				int sizeY = availableSpaceY - (spacingY * 2);					//tamanho do quadrado em si
				
//				int centerX = (squareStartX + (squareStartX + squareSizeX)) / 2;
				
				int rectangleSizeY = getSize().y - 10;
				if(showErrorPosition)
					rectangleSizeY -= availableSpaceY;
				
				int offset = 0;
				if(errorPosition < 0) offset++;
				
				int rectangleStartY = squareStartY + (availableSpaceY * offset);
				gc.fillRoundRectangle(rectangleStartX, rectangleStartY, rectangleSizeX, rectangleSizeY, 10, 10);
				
				if(showErrorPosition) {
					int errorOffset = offset ^ 1;				//XOR -> if offset = 1, errorOffset = 0 and vice-versa (inverts errorOffset value)
					drawErrorPosition(Integer.toString(errorPosition), gc, availableSpaceY, spacingY, sizeY, (arraySize * errorOffset));
				}
				
				String lengthExpression = expression != null ? " (" + expression.toString() + ")" : "";
				drawArraySize(gc, rectangleStartY, rectangleStartY + (rectangleSizeY / 2), rectangleStartY + rectangleSizeY, lengthExpression, originalArraySize);

				if(originalArraySize > maxArraySize) {
					for(int i = 0; i < maxArraySize - 3; i++) {
						drawSquare(i + "", gc, availableSpaceY, spacingY, sizeY, i + offset);
					}
					drawSquare("...", gc, availableSpaceY, spacingY, sizeY, maxArraySize - 3 + offset);
					drawSquare((originalArraySize - 2) + "", gc, availableSpaceY, spacingY, sizeY, maxArraySize - 2 + offset);
					drawSquare((originalArraySize - 1) + "", gc, availableSpaceY, spacingY, sizeY, maxArraySize - 1 + offset);
				} else {
					for(int i = 0; i < arraySize; i++) {
						drawSquare(i + "", gc, availableSpaceY, spacingY, sizeY, i + offset);
					}
				}
			}
		};
		
		addPaintListener(paintListener);
		redraw();
	}
	
	//Draws a square
	private void drawSquare(String positionText, GC gc, int availableSpaceY, int spacingY, int sizeY, int i) {
		gc.setBackground(InterfaceColors.WHITE.getColor());
		int currentY = (i * availableSpaceY) + squareStartY;
		gc.fillRectangle(squareStartX, currentY + spacingY, squareSizeX, sizeY);

		int centerY = (currentY + (currentY + sizeY + spacingY * 2)) / 2;

		gc.setFont(normalFont);
		gc.setForeground(InterfaceColors.WHITE.getColor());
		Point normalTextSize = gc.textExtent(positionText);
		int normalTextY = centerY - (normalTextSize.y / 2);
		gc.drawText(positionText, squareStartX - 10, normalTextY, true);
	}
	
	//Draws the position where the error happened
	private void drawErrorPosition(String positionText, GC gc, int availableSpaceY, int spacingY, int sizeY, int i) {
		gc.setForeground(InterfaceColors.RED.getColor());
		gc.setLineStyle(SWT.LINE_DASH);
		gc.setLineWidth(2);
		int currentY = (i * availableSpaceY) + squareStartY;
		gc.drawRectangle(squareStartX, currentY + spacingY, squareSizeX, sizeY);

		int centerY = (currentY + (currentY + sizeY + spacingY * 2)) / 2;

		gc.setFont(normalFont);
		Point normalTextSize = gc.textExtent(positionText);
		int normalTextY = centerY - (normalTextSize.y / 2);
		gc.drawText(positionText, squareStartX - 10, normalTextY, true);
	}
	
	//Draws the symbol that represents the array size
	private void drawArraySize(GC gc, int rectStartY, int rectCenterY, int rectEndY, String lengthExpression, int arraySize) {
		gc.setForeground(InterfaceColors.BLACK.getColor());
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setFont(boldFont);

		String errorExpressionString = arraySize + lengthExpression;
		Point textSize = gc.textExtent(errorExpressionString);
		
		int pathStartX = textSize.y;

		Path path = new Path(getDisplay());
		int arcSizeY = 30;
		int arcSizeX = 20;

		//Top half
		path.addArc(pathStartX + 20, rectStartY, arcSizeX, arcSizeY, 90, 90);
		path.addArc(pathStartX, rectCenterY - arcSizeY, arcSizeX, arcSizeY, 0, -90);
		
		//Bottom half
		path.addArc(pathStartX, rectCenterY, arcSizeX, arcSizeY, 90, -90);
		path.addArc(pathStartX + arcSizeX, rectEndY - arcSizeY, arcSizeX, arcSizeY, 180, 90);

		gc.drawPath(path);
		
		Transform originalTransform = new Transform(getDisplay());
		gc.getTransform(originalTransform);
		
		Transform transform = new Transform(getDisplay());
		transform.rotate(-90);
		transform.translate(-(rectCenterY + (textSize.x / 2)), 5);
		gc.setTransform(transform);
		
		gc.drawText(errorExpressionString, 0, 0, true);
		
		transform.dispose();
		
		gc.setTransform(originalTransform);
		transform.dispose();

		path.dispose();
	}
}
