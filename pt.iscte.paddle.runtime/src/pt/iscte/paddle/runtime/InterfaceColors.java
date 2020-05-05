package pt.iscte.paddle.runtime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

// enum for color shortcuts
public enum InterfaceColors {
	BLUE(Display.getDefault().getSystemColor(SWT.COLOR_BLUE)),
	RED(Display.getDefault().getSystemColor(SWT.COLOR_RED)),
	BLACK(Display.getDefault().getSystemColor(SWT.COLOR_BLACK)),
	WHITE(Display.getDefault().getSystemColor(SWT.COLOR_WHITE)),
	GREEN(Display.getDefault().getSystemColor(SWT.COLOR_GREEN)),
	GRAY(new Color(null, 135, 140, 136));
	
	private Color color;
	
	private InterfaceColors(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
}
