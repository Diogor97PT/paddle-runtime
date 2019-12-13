package pt.iscte.pidesco.cfgviewer.internal;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Color;

public class CFGExitFigure extends Ellipse {
	
	private static Color backgroundColor = new Color(null,255,255,206);
	
	/*public CFGExitFigure(String text) {
		BorderLayout layout = new BorderLayout();
		layout.setHorizontalSpacing(10);
		
		setLayoutManager(layout);
		setBackgroundColor(ColorConstants.white);
		setOpaque(false);
		setSize(100, 20);
//		setBorder(new LineBorder(1));
		
		Ellipse e = new Ellipse();
		e.setBackgroundColor(ColorConstants.black);
		e.setAntialias(SWT.ON);
		e.setSize(10, 30);
		e.setMaximumSize(new Dimension(10, 30));
//		e.setBorder(new LineBorder(1));
		add(e, BorderLayout.CENTER);
		
		Label label = new Label(text);
//		label.setBorder(new LineBorder(1));
		add(label, BorderLayout.RIGHT);
		
		Label temp = new Label(text);
//		temp.setBackgroundColor(ColorConstants.white);
		temp.setForegroundColor(ColorConstants.white);
		add(temp, BorderLayout.LEFT);
		
	}*/
	
	public CFGExitFigure(String text) {
		Label l = new Label(text);
		
		setSize(100, 50);
		setBackgroundColor(backgroundColor);
		setOpaque(true);
		setLineWidth(2);
		
		BorderLayout layout = new BorderLayout();
		setLayoutManager(layout);
		
		add(l, BorderLayout.CENTER);
	}

}
