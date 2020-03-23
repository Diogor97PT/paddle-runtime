package pt.iscte.paddle.runtime;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import pt.iscte.paddle.interpreter.IReference;
import pt.iscte.paddle.javardise.service.IClassWidget;
import pt.iscte.paddle.javardise.service.ICodeDecoration;
import pt.iscte.paddle.javardise.service.IJavardiseService;
import pt.iscte.paddle.javardise.service.IWidget;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.messages.Message;

public class RuntimeWindow {
	
	private Shell shell;
	
	public RuntimeWindow(Runtime runtime) {
		Display display = new Display();
		shell = new Shell(display);
		shell.setText("Runtime");
		
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = 50;
		layout.marginLeft = 50;
		layout.verticalSpacing = 20;
		shell.setLayout(layout);
		
		IClassWidget widget = IJavardiseService.createClassWidget(shell, runtime.getModule());
		widget.setReadOnly(true);
		
		//Group where buttons are inserted
		Group buttonGroup = new Group(shell, SWT.BORDER);
		buttonGroup.setText("Actions");
		buttonGroup.setLayout(new FillLayout());
		buttonGroup.setFocus();
		
		//Button to marak the roles of each variable
		Button markRoles = new Button(buttonGroup, SWT.TOGGLE);
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
		
		//Button to execute the code inside the widget
		Button executeCode = new Button(buttonGroup, SWT.PUSH);
		executeCode.setText("Executar Código");
		executeCode.addSelectionListener(new SelectionAdapter() {
			Link link;
			ICodeDecoration<Text> dec;
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(link != null) link.dispose();
				if(dec != null) dec.delete();
				
				Message message = runtime.execute();
				link = message.getText().create(shell, SWT.BORDER);
				IWidget w = IJavardiseService.getWidget(message.getProgramElement());
				dec = w.addNote(message.getShortText(), ICodeDecoration.Location.RIGHT);
				dec.show();
				
				for(Map.Entry<IVariableDeclaration, IReference> entry : message.getVarReferences().entrySet()) {
					IWidget widget = IJavardiseService.getWidget(entry.getKey());
					ICodeDecoration<Text> d = widget.addNote("Valor atual: " + entry.getValue().getValue(), ICodeDecoration.Location.RIGHT);
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
