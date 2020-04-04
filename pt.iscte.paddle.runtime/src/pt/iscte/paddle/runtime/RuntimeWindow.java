package pt.iscte.paddle.runtime;

import java.util.ArrayList;
import java.util.List;
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
import pt.iscte.paddle.model.IVariableExpression;
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
		cfg.setInput(runtime.getIcfg());
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
			ICodeDecoration<Text> errorVariableValueDecoration;
			
			private List<ICodeDecoration<Text>> valores = new ArrayList<>();
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(link != null) link.dispose();
				if(shortTextDecoration != null) shortTextDecoration.delete();
				if(errorVariableValueDecoration != null) errorVariableValueDecoration.delete();
				valores.forEach(dec -> dec.delete());
				
				Message message = runtime.execute();
				link = message.getText().create(buttonsAndText, SWT.BORDER);
				link.requestLayout();
				
				if(message instanceof ErrorMessage) {
					ErrorMessage errorMessage = (ErrorMessage) message;
					IWidget errorLine = IJavardiseService.getWidget(errorMessage.getErrorElement());
					shortTextDecoration = errorLine.addNote(errorMessage.getShortText(), ICodeDecoration.Location.BOTTOM);	//Add short text right of the line
					shortTextDecoration.show();
					
					IVariableExpression varExp = ErrorMessage.getVariableFromExpression(errorMessage.getErrorExpression());
					IWidget errorVariable = IJavardiseService.getWidget(varExp);
					IReference errorVariableReference = message.getVarReferences().get(varExp.getVariable());
					errorVariableValueDecoration = errorVariable.addNote(varExp.getVariable() + " = " + errorVariableReference.getValue(), ICodeDecoration.Location.TOP);
					errorVariableValueDecoration.show();
				}
				
				for(Map.Entry<IVariableDeclaration, IReference> entry : message.getVarReferences().entrySet()) {
					IWidget widget = IJavardiseService.getWidget(entry.getKey());
					ICodeDecoration<Text> d = widget.addNote(entry.getValue().getValue().toString(), ICodeDecoration.Location.RIGHT);
					valores.add(d);
					d.show();
				}
			}
		});
		
//		Button testSelection = new Button(buttonGroup, SWT.PUSH);
//		testSelection.setText("Testar Seleção");
//		testSelection.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				INode node1 = runtime.getIcfg().getNodes().get(1);
//				INode node2 = runtime.getIcfg().getNodes().get(2);
//				INode node3 = runtime.getIcfg().getNodes().get(3);
//				
//				List<INode> nodes = new ArrayList<>();
//				nodes.add(node1);
//				nodes.add(node2);
//				nodes.add(node3);
//				
//				cfg.selectNodes(nodes);
//			}
//		});
		
//		shell.pack();
		shell.setSize(800, 700);
		shell.open();
		while (!shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
