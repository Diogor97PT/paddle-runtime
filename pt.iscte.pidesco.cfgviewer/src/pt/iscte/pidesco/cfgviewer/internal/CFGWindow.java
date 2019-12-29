package pt.iscte.pidesco.cfgviewer.internal;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import pt.iscte.paddle.codequality.cfg.CFGBuilder;
import pt.iscte.paddle.javali.translator.Translator;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.cfg.IControlFlowGraph;
import pt.iscte.pidesco.cfgviewer.ext.IColorScheme;

public class CFGWindow {
	
	public CFGWindow(IControlFlowGraph cfg, IColorScheme ics) {
		Display display = new Display();
		
		Shell shell = new Shell(display);
		shell.setText("Control Flow Graph");
		shell.setLayout(new FillLayout());
		
		Composite viewArea = new Composite(shell, SWT.NONE);
		viewArea.setLayout(new FillLayout());
		CFGView view = new CFGView(viewArea, ics);
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
		
		/*IControlFlowGraph cfg = CFG_Creator.create_cfg();
		
		new CFGWindow(cfg);*/
		
		File codeToCheck = new File("test.javali");
		Translator translator = new Translator(codeToCheck.getAbsolutePath());
		IModule module1 = translator.createProgram();
		IProcedure procedure = module1.getProcedures().iterator().next(); // first procedure
		
		CFGBuilder icfg = new CFGBuilder(procedure);
		
		IColorScheme ics = new ColorScheme();
		new CFGWindow(icfg.getCFG(), ics);
	}

}
