package pt.iscte.pidesco.cfgviewer.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import pt.iscte.paddle.model.cfg.IControlFlowGraph;

public class CFGWindow {
	
	public CFGWindow(IControlFlowGraph cfg) {
		Display display = new Display();
		
		Shell shell = new Shell(display);
		shell.setText("Control Flow Graph");
		
		shell.setLayout(new FillLayout());
		
		Composite viewArea = new Composite(shell, SWT.NONE);
		viewArea.setLayout(new FillLayout());
		CFGView view = new CFGView(viewArea);
		view.setInput(cfg.getNodes());
		
		shell.open();
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		display.dispose();
	}
	
	public static void main(String[] args) {
		
		IControlFlowGraph cfg = CFG_Creator.create_cfg();
		
		new CFGWindow(cfg);
	}

}
