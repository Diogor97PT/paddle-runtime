package pt.iscte.paddle.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.Iterables;

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
		l.horizontalSpacing = 80;
		comp.setLayout(l);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
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
		
		//Button to execute the code inside the widget and display information
		Button executeCode = new Button(buttonGroup, SWT.PUSH);
		executeCode.setText("Executar Código");
		executeCode.addSelectionListener(new SelectionAdapter() {
			Link link;
			ICodeDecoration<Text> shortTextDecoration;
			ICodeDecoration<Text> errorVariableValueDecoration;
			ICodeDecoration<Canvas> errorExpressionHighlight;
			
			private List<ICodeDecoration<Text>> valores = new ArrayList<>();
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(link != null) link.dispose();
				if(shortTextDecoration != null) shortTextDecoration.delete();
				if(errorVariableValueDecoration != null) errorVariableValueDecoration.delete();
				if(errorExpressionHighlight != null) errorExpressionHighlight.delete();
				valores.forEach(dec -> dec.delete());
				valores.clear();
				
				Message message = runtime.execute();
				link = message.getText().create(buttonsAndText, SWT.BORDER);
				link.requestLayout();
				
				if(message instanceof ErrorMessage) {
					ErrorMessage errorMessage = (ErrorMessage) message;
					
					IVariableExpression varExp = ErrorMessage.getVariableFromExpression(errorMessage.getErrorExpression());		//Expression where the error Occurs
					String varValue = Iterables.getLast(message.getVarValues().get(varExp.getVariable()));						//Value of the error expression
					
					IWidget errorLine = IJavardiseService.getWidget(errorMessage.getErrorElement());
					shortTextDecoration = errorLine.addNote(errorMessage.getShortText(), ICodeDecoration.Location.RIGHT);	//Add short text right of the line
					errorVariableValueDecoration = errorLine.addNote(varExp.getVariable() + " = " + varValue, ICodeDecoration.Location.LEFT);
					shortTextDecoration.show();
					errorVariableValueDecoration.show();
					
					IWidget errorVariable = IJavardiseService.getWidget(errorMessage.getErrorExpression());
					errorExpressionHighlight = errorVariable.addMark(InterfaceColors.RED.getColor());
					errorExpressionHighlight.show();
				}
				
				for(Map.Entry<IVariableDeclaration, Collection<String>> entry : message.getVarValues().asMap().entrySet()) {	//Add Variable Values to GUI
					IWidget widget = IJavardiseService.getWidget(entry.getKey());
					String varValue = Iterables.getLast(entry.getValue());
					ICodeDecoration<Text> d = widget.addNote(entry.getKey() + " = " + varValue, ICodeDecoration.Location.RIGHT);
					valores.add(d);
					d.show();
				}
				
				for(Map.Entry<IVariableDeclaration, IReference> entry : message.getParameterReferences().entrySet()) {	//Add Parameter Values to GUI
					IWidget widget = IJavardiseService.getWidget(entry.getKey());
					ICodeDecoration<Text> d = widget.addNote(entry.getKey() + " = " + entry.getValue().getValue().toString(), ICodeDecoration.Location.TOP);
					valores.add(d);
					d.show();
				}
				shell.pack();
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
		
		shell.pack();
//		shell.setSize(900, 700);
		shell.open();
		while (!shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
