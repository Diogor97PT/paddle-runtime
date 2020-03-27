package pt.iscte.paddle.runtime;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
import pt.iscte.paddle.runtime.messages.ErrorMessage;
import pt.iscte.paddle.runtime.messages.Message;
import pt.iscte.pidesco.cfgviewer.ext.CFGViewer;

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
		
		Composite comp = new Composite(shell, SWT.NONE);
		GridLayout l = new GridLayout(2, true);
		l.horizontalSpacing = 50;
		comp.setLayout(l);
		
		IClassWidget widget = IJavardiseService.createClassWidget(comp, runtime.getModule());
		widget.setReadOnly(true);
		
		CFGViewer cfg = new CFGViewer(comp);
		cfg.setInput(runtime.getProcedure().getCFG().getNodes());
		cfg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite buttonsAndText = new Composite(shell, SWT.NONE);
		buttonsAndText.setLayout(new GridLayout(2, false));
		buttonsAndText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		//Group where buttons are inserted
		Group buttonGroup = new Group(buttonsAndText, SWT.BORDER);
		buttonGroup.setText("Actions");
		buttonGroup.setLayout(new FillLayout());
		buttonGroup.setFocus();
		
		//Button to mark the roles of each variable
//		Button markRoles = new Button(buttonGroup, SWT.TOGGLE);
//		markRoles.setText("Mark Roles");
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
			ICodeDecoration<Text> shortTextDecoration;
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(link != null) link.dispose();
				if(shortTextDecoration != null) shortTextDecoration.delete();
				
				Message message = runtime.execute();
				link = message.getText().create(buttonsAndText, SWT.BORDER);
				
				if(message instanceof ErrorMessage) {
					ErrorMessage errorMessage = (ErrorMessage) message;
					IWidget w = IJavardiseService.getWidget(errorMessage.getErrorElement());
					shortTextDecoration = w.addNote(errorMessage.getShortText(), ICodeDecoration.Location.RIGHT);	//Add short text right of the line
					shortTextDecoration.show();
				}
				
				for(Map.Entry<IVariableDeclaration, IReference> entry : message.getVarReferences().entrySet()) {
					IWidget widget = IJavardiseService.getWidget(entry.getKey());
					ICodeDecoration<Text> d = widget.addNote(entry.getValue().getValue().toString(), ICodeDecoration.Location.RIGHT);
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
