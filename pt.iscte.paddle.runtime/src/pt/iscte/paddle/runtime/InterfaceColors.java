package pt.iscte.paddle.runtime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

// enum for color shortcuts
public enum InterfaceColors {
	BLUE(Display.getDefault().getSystemColor(SWT.COLOR_BLUE)),
	CYAN(Display.getDefault().getSystemColor(SWT.COLOR_CYAN)),
	MAGENTA(Display.getDefault().getSystemColor(SWT.COLOR_DARK_MAGENTA));
	
	private Color color;
	
	private InterfaceColors(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
}
