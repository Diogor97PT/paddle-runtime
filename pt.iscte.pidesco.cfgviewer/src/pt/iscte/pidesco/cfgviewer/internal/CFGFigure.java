package pt.iscte.pidesco.cfgviewer.internal;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.graphics.Color;

public class CFGFigure extends Figure {
	
	//private static final Color figureColor = new Color(null, 50, 155, 168);
	public static Color figureColor = new Color(null,255,255,206);
	
	public CFGFigure(String text) {
		BorderLayout layout = new BorderLayout();
//		layout.setHorizontalSpacing(10);
//		layout.setVerticalSpacing(10);
		
		setLayoutManager(layout);
		setBackgroundColor(figureColor);
		setOpaque(true);
		
		LineBorder border = new LineBorder(ColorConstants.black, 1, Graphics.LINE_SOLID);
		border.setWidth(2);
		setBorder(border);
		
		setPreferredSize(100, 50);
		setSize(100, 50);
		
		Label l = new Label(text);
		l.setLabelAlignment(PositionConstants.CENTER);
//		l.setBorder(new LineBorder(1));
		add(l, BorderLayout.CENTER);
	}
	
	public CFGFigure(String text, String tooltip) {
		this(text);
		
		setToolTip(new Label(tooltip));
	}
}