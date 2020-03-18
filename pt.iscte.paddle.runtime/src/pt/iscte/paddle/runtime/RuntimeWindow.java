package pt.iscte.paddle.runtime;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import pt.iscte.paddle.javardise.ClassWidget;
import pt.iscte.paddle.javardise.Constants;
import pt.iscte.paddle.javardise.Decoration;
import pt.iscte.paddle.javardise.MarkerService;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.messages.IMessage;

public class RuntimeWindow {
	
	private static Shell shell;
	
	public static enum InterfaceColor {
		BLUE(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		
		private Color color;
		
		private InterfaceColor(Color color) {
			this.color = color;
		}
		
		public Color getColor() {
			return color;
		}
	}
	
	public RuntimeWindow(Runtime runtime) {
		Display display = new Display();
		shell = new Shell(display);
		shell.setText("Teste");
		shell.setBackground(Constants.COLOR_BACKGROUND);
		
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = 50;
		layout.marginLeft = 50;
		layout.verticalSpacing = 20;
		shell.setLayout(layout);
		
		ClassWidget widget = new ClassWidget(shell, runtime.getModule());
		widget.setEnabled(false);
		
		//Add buttons
		Composite comp = new Composite(shell, SWT.BORDER);
		comp.setLayout(new FillLayout());
		
		Button markRoles = new Button(comp, SWT.TOGGLE);
		markRoles.setText("Mark Roles");
//		markRoles.addSelectionListener(new SelectionAdapter() {
//			Link link;
//			List<Decoration> decs;
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				Button bt = (Button)e.getSource();
//				if(!bt.getSelection()) {
//					link.dispose();
//					decs.forEach(d -> d.hide());
//				} else {
//					HyperlinkedText txt = new HyperlinkedText(e1 -> MarkerService.mark(InterfaceColor.BLUE.getColor(), e1));
//					txt.line("Roles present in Procedure:");
//					for(IVariableDeclaration var : procedure.getVariables()) {
//						IVariableRole role = IVariableRole.match(var);
//						txt.line(var.getId() + " : " + role.toString());
//					}
//					link = txt.create(shell, SWT.BORDER);
//
//					decs = new ArrayList<>();
//					for(IVariableDeclaration var : procedure.getVariables()) {
//						IVariableRole role = IVariableRole.match(var);
//						Decoration d = MarkerService.addDecoration(var, role.toString(), Decoration.Location.RIGHT);
//						if(d == null) continue;		//Não funciona nos parâmetros da função
//						decs.add(d);
//						d.show();
//					}
//				}
//				shell.pack();
//			}
//		});
		
		Button executeCode = new Button(comp, SWT.PUSH);
		executeCode.setText("Executar Código");
		executeCode.addSelectionListener(new SelectionAdapter() {
			Link link;
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(link != null) {
					link.dispose();
				}
				
				IMessage message = runtime.execute();
				link = message.getText().create(shell, SWT.BORDER);
				
				for(Map.Entry<IVariableDeclaration, String> entry : message.getVarValues().entrySet()) {
					Decoration d = MarkerService.addDecoration(entry.getKey(), "Valor atual: " + entry.getValue(), Decoration.Location.RIGHT);
					d.show();
				}
				
				shell.pack(true);
			}
		});
		
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
