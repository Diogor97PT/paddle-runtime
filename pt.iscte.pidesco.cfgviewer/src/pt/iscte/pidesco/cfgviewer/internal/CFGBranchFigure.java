package pt.iscte.pidesco.cfgviewer.internal;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LineBorder;

public class CFGBranchFigure extends CFGFigure{

	public CFGBranchFigure(String text) {
		super(text);
		
		LineBorder border = (LineBorder) getBorder();
		border.setStyle(Graphics.LINE_DASH);
	}

}
