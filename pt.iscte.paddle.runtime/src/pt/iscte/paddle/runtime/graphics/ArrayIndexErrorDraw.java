package pt.iscte.paddle.runtime.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import pt.iscte.paddle.interpreter.IReference;
import pt.iscte.paddle.runtime.InterfaceColors;

public class ArrayIndexErrorDraw extends Canvas {
	
	private PaintListener paintListener;

	public ArrayIndexErrorDraw(Composite comp) {
		super(comp, SWT.BORDER);
		setSize(500, 500);
		
//		canvas.setSize(2000, canvas.getSize().y);
		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.setAntialias(SWT.ON);
				e.gc.drawString("Representação do Erro", 10, 10);
			}
		});
	}
	
	public void draw(IReference ref) {
		if(paintListener != null) removePaintListener(paintListener);
		
		String [] array = stringToArray(ref.getValue().toString());
		
		paintListener = new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.setBackground(InterfaceColors.BLACK.getColor());
				
				int startY = 40;
				
				int startX = 10;
				int availableSpace = (getSize().x - 20) / 5;
				int spacing = availableSpace / 10;
				int size = availableSpace - (spacing * 2);
				
//				e.gc.drawRoundRectangle(10, 10, getSize().x - 20, getSize().y - 20, 30, 30);
				e.gc.fillRoundRectangle(startX, startY, getSize().x - 20, 180, 30, 30);
				
				e.gc.setBackground(InterfaceColors.WHITE.getColor());
				for(int i = 0; i < array.length; i++) {
					e.gc.fillRectangle((i * availableSpace) + startX + spacing, 60, size, size);
				}
			}
		};
		addPaintListener(paintListener);
		redraw();
	}
	
	private String[] stringToArray(String s) {
		String s2 = s.substring(1, s.length()-1);
		return s2.split(",");
	}

}