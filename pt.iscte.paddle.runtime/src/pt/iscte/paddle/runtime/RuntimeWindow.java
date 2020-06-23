package pt.iscte.paddle.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
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
import pt.iscte.paddle.runtime.graphics.MatrixIndexErrorDraw;
import pt.iscte.paddle.runtime.messages.ArrayIndexErrorMessage;
import pt.iscte.paddle.runtime.messages.ErrorMessage;
import pt.iscte.paddle.runtime.messages.Message;
import pt.iscte.paddle.runtime.variableInfo.ArrayVariableInfo;
import pt.iscte.paddle.runtime.variableInfo.VariableInfo;
import pt.iscte.paddle.runtime.variableInfo.VariableInfo.VariableType;

public class RuntimeWindow {
	
	private Shell shell;
	private Runtime runtime;
	
	public RuntimeWindow(Runtime runtime) {
		this.runtime = runtime;
		
		Display display = new Display();
		shell = new Shell(display);
		shell.setText("Runtime");
		shell.setSize(1060, 800);
		
		RowLayout layout = new RowLayout();
		layout.wrap = false;
		layout.pack = true;
		layout.justify = false;
		layout.marginLeft = 20;
		layout.marginRight = 5;
		layout.marginTop = 5;
		layout.marginBottom = 5;
		shell.setLayout(layout);
		
		//Create Menu
		Menu menubar = new Menu(shell, SWT.BAR);

		MenuItem fileMenuItem = new MenuItem(menubar, SWT.CASCADE);
		fileMenuItem.setText("File");									//TODO Add Test Selection in the future
		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		fileMenuItem.setMenu(fileMenu);
		
		MenuItem fileInfoItem = new MenuItem(fileMenu, SWT.PUSH);
		fileInfoItem.setText("&Info");
		
		fileInfoItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox dialog = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
				dialog.setText("Alert");
				dialog.setMessage("In Construction");										//Dialog Test
				dialog.open();
			}
		});
		
		shell.setMenuBar(menubar);
		
		//Code widget Composite
		Composite codeComposite = new Composite(shell, SWT.WRAP | SWT.V_SCROLL);	//Vertical Scroll doesn’t work
		codeComposite.setLayout(new FillLayout(SWT.WRAP | SWT.V_SCROLL));

		//Code Widget
		IClassWidget widget = IJavardiseService.createClassWidget(codeComposite, runtime.getModule());
		widget.setReadOnly(true);
		
		//Buttons and Text Composite
		Composite rightSide = new Composite(shell, SWT.NONE);
		RowLayout rightSidelayout = new RowLayout(SWT.VERTICAL);
		rightSidelayout.wrap = false;
		rightSidelayout.pack = true;
		rightSidelayout.justify = false;
		rightSide.setLayout(rightSidelayout);
		
		//Group where buttons are inserted
		Group buttonGroup = new Group(rightSide, SWT.BORDER);
		buttonGroup.setText("Actions");
		buttonGroup.setLayout(new FillLayout());
		buttonGroup.setFocus();
		
		//Button to execute the code inside the widget and display information
		Button executeCode = new Button(buttonGroup, SWT.PUSH);
		executeCode.setText("Executar Código");
		executeCode.addSelectionListener(new ExecuteSelectionAdapter(rightSide));
		
		shell.open();
		while (!shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	private class ExecuteSelectionAdapter extends SelectionAdapter {
		Link link;
		ICodeDecoration<Text> shortTextDecoration;
		ICodeDecoration<Text> errorVariableValueDecoration;
		ICodeDecoration<Canvas> errorExpressionHighlight;
//		ICodeDecoration<Control> canvasDec;
		
		private List<ICodeDecoration<Text>> valores = new ArrayList<>();
		
		private Composite messageComposite;
		
		public ExecuteSelectionAdapter(Composite messageComposite) {
			this.messageComposite = messageComposite;
		}
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			//Delete Previous Decorations
			if(link != null) link.dispose();
			if(shortTextDecoration != null) shortTextDecoration.delete();
			if(errorVariableValueDecoration != null) errorVariableValueDecoration.delete();
			if(errorExpressionHighlight != null) errorExpressionHighlight.delete();
//			if(canvasDec != null) canvasDec.delete();
			
			valores.forEach(dec -> dec.delete());
			valores.clear();
			
			//Create new Decorations
			Message message = runtime.execute();
			link = message.getText().create(messageComposite, SWT.BORDER);
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
					ArrayVariableInfo info = (ArrayVariableInfo)message.getVarValues().get(errorMessage.getErrorTarget());
					if(info.getAccessedPositions().get(0).getCoordinates().size() == 1) {
//						canvasDec = errorLine.addDecoration((parent, control) -> {
//							ArrayIndexErrorDraw arrayDraw = new ArrayIndexErrorDraw(parent);
//							arrayDraw.draw(info, arrayIndexError.getErrorCoordinates()[0], arrayIndexError.getArraySize());
//							return arrayDraw;
//						}, ICodeDecoration.Location.RIGHT);
//						canvasDec.show();
						ArrayIndexErrorDraw arrayDraw = new ArrayIndexErrorDraw(messageComposite);
						arrayDraw.draw(info, arrayIndexError.getErrorCoordinates()[0], arrayIndexError.getArraySize());
					} else if (info.getAccessedPositions().get(0).getCoordinates().size() == 2) {
//						canvasDec = errorLine.addDecoration((parent, control) -> {
//							MatrixIndexErrorDraw matrixDraw = new MatrixIndexErrorDraw(parent);
//							matrixDraw.draw(info, arrayIndexError.getErrorCoordinates(), arrayIndexError.getArraySize());
//							return matrixDraw;
//						}, ICodeDecoration.Location.RIGHT);
//						canvasDec.show();
						MatrixIndexErrorDraw matrixDraw = new MatrixIndexErrorDraw(messageComposite);
						matrixDraw.draw(info, arrayIndexError.getErrorCoordinates(), arrayIndexError.getArraySize());
					}
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
//			shell.pack();
		}
	}
}
