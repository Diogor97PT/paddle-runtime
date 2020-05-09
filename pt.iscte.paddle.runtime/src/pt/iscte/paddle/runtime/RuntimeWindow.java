package pt.iscte.paddle.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.Iterables;

import pt.iscte.paddle.javardise.service.IClassWidget;
import pt.iscte.paddle.javardise.service.ICodeDecoration;
import pt.iscte.paddle.javardise.service.IJavardiseService;
import pt.iscte.paddle.javardise.service.IWidget;
import pt.iscte.paddle.model.IArrayType;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.runtime.graphics.ArrayIndexErrorDraw;
import pt.iscte.paddle.runtime.messages.ArrayIndexErrorMessage;
import pt.iscte.paddle.runtime.messages.ErrorMessage;
import pt.iscte.paddle.runtime.messages.Message;
import pt.iscte.paddle.runtime.variableInfo.ArrayVariableInfo;
import pt.iscte.paddle.runtime.variableInfo.VariableInfo;
import pt.iscte.paddle.runtime.variableInfo.VariableInfo.VariableType;
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
		GridLayout l = new GridLayout(2, false);
		l.horizontalSpacing = 200;
		comp.setLayout(l);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		IClassWidget widget = IJavardiseService.createClassWidget(comp, runtime.getModule());
		widget.setReadOnly(true);
		
		CFGViewer cfg = new CFGViewer(comp);
		cfg.setInput(runtime.getIcfg());
		GridData gdCfg = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdCfg.widthHint = 300;
		cfg.setLayoutData(gdCfg);
		
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
			ICodeDecoration<Control> canvasDec;
			
			private List<ICodeDecoration<Text>> valores = new ArrayList<>();
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Delete Previous Decorations
				if(link != null) link.dispose();
				
				if(shortTextDecoration != null) shortTextDecoration.delete();
				
				if(errorVariableValueDecoration != null) errorVariableValueDecoration.delete();
				
				if(errorExpressionHighlight != null) errorExpressionHighlight.delete();
				
				if(canvasDec != null) canvasDec.delete();
				
				valores.forEach(dec -> dec.delete());
				valores.clear();
				
				//Create new Decorations
				Message message = runtime.execute();
				link = message.getText().create(buttonsAndText, SWT.BORDER);
				link.requestLayout();
				
				if(message instanceof ErrorMessage) {
					ErrorMessage errorMessage = (ErrorMessage) message;
					
					IVariableExpression varExp = ErrorMessage.getVariableFromExpression(errorMessage.getErrorExpression());		//Expression where the error Occurs
					String varValue = Iterables.getLast(message.getVarValues().get(varExp.getVariable()).getVarValues());		//Value of the error expression
					
					IWidget errorLine = IJavardiseService.getWidget(errorMessage.getErrorElement());
					shortTextDecoration = errorLine.addNote(errorMessage.getShortText(), ICodeDecoration.Location.RIGHT);	//Add short text right of the line
					errorVariableValueDecoration = errorLine.addNote(varExp.getVariable() + " = " + varValue, ICodeDecoration.Location.LEFT);
					shortTextDecoration.show();
					errorVariableValueDecoration.show();
					
					IWidget errorVariable = IJavardiseService.getWidget(errorMessage.getErrorExpression());
					errorExpressionHighlight = errorVariable.addMark(InterfaceColors.RED.getColor());
					errorExpressionHighlight.show();
					
					if(errorMessage instanceof ArrayIndexErrorMessage) {
						ArrayIndexErrorMessage arrayIndexError = (ArrayIndexErrorMessage) errorMessage;
						canvasDec = errorLine.addDecoration((parent, control) -> {
							ArrayIndexErrorDraw arrayDraw = new ArrayIndexErrorDraw(parent);
							/*arrayDraw.draw(message.getVarValues().get(errorMessage.getErrorTarget()).getReference(), 
									arrayIndexError.getErrorIndex(), 
									errorMessage.getErrorExpression(), 
									arrayIndexError.getArraySize(), 
									((ArrayVariableInfo)message.getVarValues().get(errorMessage.getErrorTarget())).getAccessedPositions());*/
							arrayDraw.draw(((ArrayVariableInfo)message.getVarValues().get(errorMessage.getErrorTarget())), arrayIndexError.getErrorIndex(), arrayIndexError.getArraySize());
							return arrayDraw;
						}, ICodeDecoration.Location.RIGHT);
						canvasDec.show();
					}
				}
				
				for(Entry<IVariableDeclaration, VariableInfo> entry : message.getVarValues().entrySet()) {
					IVariableDeclaration var = entry.getKey();
					VariableInfo info = entry.getValue();
					IWidget widget = IJavardiseService.getWidget(var);
					String varValue;
					
					if(info.getReference().getType() instanceof IArrayType)
						varValue = Arrays.toString(ArrayIndexErrorDraw.stringToShrinkedArray(info.getReference().getValue().toString()));
					else 
						varValue = info.getReference().getValue().toString();
					
					ICodeDecoration<Text> d;
					if(info.getVariableType() == VariableType.PARAMETER) {
						d = widget.addNote(varValue, ICodeDecoration.Location.TOP);
					} else {
						d = widget.addNote(varValue, ICodeDecoration.Location.LEFT);
					}
					
					valores.add(d);
					d.show();
				}
//				shell.pack();
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
		shell.open();
		while (!shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
