package pt.iscte.paddle.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
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
import pt.iscte.paddle.runtime.experiment.tests.Example01SumTest;
import pt.iscte.paddle.runtime.experiment.tests.Example02NaturalsTest;
import pt.iscte.paddle.runtime.experiment.tests.Example03LastOccurrenceTest;
import pt.iscte.paddle.runtime.experiment.tests.Example04InvertTest;
import pt.iscte.paddle.runtime.experiment.tests.Example05MultiplyMatrixTest;
import pt.iscte.paddle.runtime.experiment.tests.Example06TranposeMatrixTest;
import pt.iscte.paddle.runtime.experiment.tests.Example07InvertSameVectorTest;
import pt.iscte.paddle.runtime.experiment.tests.Example08BubbleSortTest;
import pt.iscte.paddle.runtime.experiment.tests.Example09SelectionSortTest;
import pt.iscte.paddle.runtime.experiment.tests.Example10BinarySearchTest;
import pt.iscte.paddle.runtime.graphics.ArrayIndexErrorDraw;
import pt.iscte.paddle.runtime.graphics.MatrixIndexErrorDraw;
import pt.iscte.paddle.runtime.messages.ArrayIndexErrorMessage;
import pt.iscte.paddle.runtime.messages.ErrorMessage;
import pt.iscte.paddle.runtime.messages.Message;
import pt.iscte.paddle.runtime.tests.Test;
import pt.iscte.paddle.runtime.variableInfo.ArrayVariableInfo;
import pt.iscte.paddle.runtime.variableInfo.VariableInfo;
import pt.iscte.paddle.runtime.variableInfo.VariableInfo.VariableType;

public class RuntimeWindow {
	
	private Shell shell;
	private Runtime runtime;
	
	private IClassWidget codeWidget;
	private Composite codeComposite;
	private Composite rightSide;
	
	//Message and graphic stuff
	private Link link;
	private Composite errorDraw;
	private ICodeDecoration<Text> shortTextDecoration;
//	private ICodeDecoration<Text> errorVariableValueDecoration;
	private ICodeDecoration<Canvas> errorExpressionHighlight;
	
	private List<ICodeDecoration<Text>> valores = new ArrayList<>();
	
	//-------------------------------------tests-------------------------------------//
//	Test test = new ArrayIndexErrorTest();
//	Test test = new ArrayIndexErrorExpressionTest();
//	Test test = new ArrayIndexErrorBackwardTest();
//	Test test = new ArrayIndexPlus2Test();
//	Test test = new ArrayIndexFunctionTest();
//	Test test = new MatrixErrorTest();
//	Test test = new SumAllTest();
//	Test test = new NullPointerErrorTest();
	
//	private int testValue = 20;
	//-------------------------------------tests-------------------------------------//
	
	//-------------------------------Experiment tests--------------------------------//
	Test test = new Example01SumTest();
//	Test test = new Example02NaturalsTest();
//	Test test = new Example03LastOccurrenceTest();
//	Test test = new Example04InvertTest();
//	Test test = new Example05MultiplyMatrixTest();
//	Test test = new Example06TranposeMatrixTest();
//	Test test = new Example07InvertSameVectorTest();
//	Test test = new Example08BubbleSortTest();
//	Test test = new Example09SelectionSortTest();
//	Test test = new Example10BinarySearchTest();
	//-------------------------------Experiment tests--------------------------------//
	
	public RuntimeWindow() {
		runtime = new Runtime(test); 
		
		Display display = new Display();
		shell = new Shell(display);
		shell.setText("Runtime");
		shell.setSize(1150, 800);
		
		GridLayout layout = new GridLayout(2, true);
		layout.marginLeft = 30;
		layout.marginRight = 5;
		layout.marginTop = 5;
		layout.marginBottom = 5;
		shell.setLayout(layout);
		
		//Create Menu
		Menu menuBar = new Menu(shell, SWT.BAR);
		fillMenuBar(menuBar);

		shell.setMenuBar(menuBar);
		
		//Code widget Composite
		ScrolledComposite codeScroll = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		codeScroll.setLayout(new GridLayout(1, false));
		codeScroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		codeComposite = new Composite(codeScroll, SWT.NONE);
		codeComposite.setLayout(new FillLayout());
		
		//Code Widget
		codeWidget = IJavardiseService.createClassWidget(codeComposite, runtime.getModule());
		codeWidget.setReadOnly(true);
		
		codeScroll.setContent(codeComposite);
		codeScroll.setMinSize(700, 1000);		//TODO arranjar maneira de saber o tamanho a scrollar de forma correta
		codeScroll.setExpandHorizontal(true);
		codeScroll.setExpandVertical(true);

		//Buttons and Text Composite
		rightSide = new Composite(shell, SWT.NONE);
		rightSide.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		RowLayout rightSidelayout = new RowLayout(SWT.VERTICAL);
		rightSidelayout.wrap = false;
		rightSidelayout.pack = true;
		rightSidelayout.justify = false;
		rightSidelayout.spacing = 10;
		rightSide.setLayout(rightSidelayout);
		
		//Group where buttons are inserted
		Group buttonGroup = new Group(rightSide, SWT.BORDER);
		buttonGroup.setText("Actions");
		buttonGroup.setLayout(new FillLayout());
		buttonGroup.setFocus();
		
		//Button to execute the code inside the widget and display information
		Button executeCode = new Button(buttonGroup, SWT.PUSH);
		executeCode.setText("Executar Código");
		executeCode.addSelectionListener(new ExecuteSelectionAdapter());
		
		shell.open();
		while (!shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	private class ExecuteSelectionAdapter extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			deleteDecorations();
			
			//Create new Decorations
			Message message = runtime.execute();
			link = message.getText().create(rightSide, SWT.NONE);
			link.requestLayout();
			
			if(message instanceof ErrorMessage) {
				ErrorMessage errorMessage = (ErrorMessage) message;
				
				IVariableExpression varExp = ErrorMessage.getVariableFromExpression(errorMessage.getErrorExpression());		//Expression where the error Occurs
				String varValue = Iterables.getLast(message.getVarValues().get(varExp.getVariable()).getVarValues());		//Value of the error expression
				
				IWidget errorLine = IJavardiseService.getWidget(errorMessage.getErrorElement());
//				shortTextDecoration = errorLine.addNote(errorMessage.getShortText(), ICodeDecoration.Location.RIGHT);	//Add short text right of the line
//				errorVariableValueDecoration = errorLine.addNote(varExp.getVariable() + " = " + varValue, ICodeDecoration.Location.LEFT);
//				shortTextDecoration.show();
//				errorVariableValueDecoration.show();
				
				shortTextDecoration = errorLine.addNote(errorMessage.getShortText() + ", " + varExp.getVariable() + " = " + varValue, 
						ICodeDecoration.Location.RIGHT);	//Add short text and value of the variable to the end of the line
				shortTextDecoration.show();
				
				IWidget errorVariable = IJavardiseService.getWidget(errorMessage.getErrorExpression());
				errorExpressionHighlight = errorVariable.addMark(InterfaceColors.RED.getColor());
				errorExpressionHighlight.show();
				
				if(errorMessage instanceof ArrayIndexErrorMessage) {
					ArrayIndexErrorMessage arrayIndexError = (ArrayIndexErrorMessage) errorMessage;
					ArrayVariableInfo info = (ArrayVariableInfo)message.getVarValues().get(errorMessage.getErrorTarget());
					if(info.getAccessedPositions().get(0).getCoordinates().size() == 1) {
						ArrayIndexErrorDraw arrayDraw = new ArrayIndexErrorDraw(rightSide);
						errorDraw = arrayDraw;
						arrayDraw.draw(info, arrayIndexError.getErrorCoordinates()[0], arrayIndexError.getArraySize());
					} else if (info.getAccessedPositions().get(0).getCoordinates().size() == 2) {
						MatrixIndexErrorDraw matrixDraw = new MatrixIndexErrorDraw(rightSide);
						errorDraw = matrixDraw;
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
		}
	}
	
	private void fillMenuBar(Menu menuBar) {
		MenuItem fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
	    fileMenuHeader.setText("&File");

	    Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
	    fileMenuHeader.setMenu(fileMenu);

	    MenuItem firstTest = new MenuItem(fileMenu, SWT.RADIO);
	    firstTest.setText("Test 01 - Sum");
	    firstTest.setSelection(true);
	    firstTest.addSelectionListener(new TestSelectionListener());
	    
	    MenuItem secondTest = new MenuItem(fileMenu, SWT.RADIO);
	    secondTest.setText("Test 02 - Naturals");
	    secondTest.addSelectionListener(new TestSelectionListener());
	    
	    MenuItem thirdTest = new MenuItem(fileMenu, SWT.RADIO);
	    thirdTest.setText("Test 03 - Last Occurrence");
	    thirdTest.addSelectionListener(new TestSelectionListener());
	    
	    MenuItem fourthTest = new MenuItem(fileMenu, SWT.RADIO);
	    fourthTest.setText("Test 04 - Invert");
	    fourthTest.addSelectionListener(new TestSelectionListener());
	    
	    MenuItem fifthTest = new MenuItem(fileMenu, SWT.RADIO);
	    fifthTest.setText("Test 05 - Multiply Matrix");
	    fifthTest.addSelectionListener(new TestSelectionListener());
	    
	    MenuItem sixthTest = new MenuItem(fileMenu, SWT.RADIO);
	    sixthTest.setText("Test 06 - Transpose Matrix");
	    sixthTest.addSelectionListener(new TestSelectionListener());
	    
	    MenuItem seventhTest = new MenuItem(fileMenu, SWT.RADIO);
	    seventhTest.setText("Test 07 - Invert Same Vector");
	    seventhTest.addSelectionListener(new TestSelectionListener());
	    
	    MenuItem eightTest = new MenuItem(fileMenu, SWT.RADIO);
	    eightTest.setText("Test 08 - Bubble Sort");
	    eightTest.addSelectionListener(new TestSelectionListener());
	    
	    MenuItem ninethTest = new MenuItem(fileMenu, SWT.RADIO);
	    ninethTest.setText("Test 09 - Selection Sort");
	    ninethTest.addSelectionListener(new TestSelectionListener());
	    
	    MenuItem tenthTest = new MenuItem(fileMenu, SWT.RADIO);
	    tenthTest.setText("Test 10 - BinarySearch");
	    tenthTest.addSelectionListener(new TestSelectionListener());
	    
//		fileInfoItem.addSelectionListener(new SelectionAdapter() {
//		@Override
//		public void widgetSelected(SelectionEvent e) {
//			MessageBox dialog = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
//			dialog.setText("Alert");
//			dialog.setMessage("In Construction");										//Dialog Test
//			dialog.open();
//		}
//	});
	}
	
	private void deleteDecorations() {
		//Delete Previous Decorations
		if(link != null) link.dispose();
		if(errorDraw != null) errorDraw.dispose();
		if(shortTextDecoration != null) shortTextDecoration.delete();
//		if(errorVariableValueDecoration != null) errorVariableValueDecoration.delete();
		if(errorExpressionHighlight != null) errorExpressionHighlight.delete();
		
		valores.forEach(dec -> dec.delete());
		valores.clear();
	}
	
	private void changeGUIToTest(Test test) {
		codeWidget.getControl().dispose();
		deleteDecorations();
		
		runtime = new Runtime(test);
		
		codeWidget = IJavardiseService.createClassWidget(codeComposite, runtime.getModule());
		codeWidget.setReadOnly(true);
	}
	
	private class TestSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			MenuItem item = (MenuItem) e.widget;
			
			if(!item.getSelection()) return;
			
			switch (item.getText()) {
			case "Test 01 - Sum":
				changeGUIToTest(new Example01SumTest());
				break;
			case "Test 02 - Naturals":
				changeGUIToTest(new Example02NaturalsTest());
				break;
			case "Test 03 - Last Occurrence":
				changeGUIToTest(new Example03LastOccurrenceTest());
				break;
			case "Test 04 - Invert":
				changeGUIToTest(new Example04InvertTest());
				break;
			case "Test 05 - Multiply Matrix":
				changeGUIToTest(new Example05MultiplyMatrixTest());
				break;
			case "Test 06 - Transpose Matrix":
				changeGUIToTest(new Example06TranposeMatrixTest());
				break;
			case "Test 07 - Invert Same Vector":
				changeGUIToTest(new Example07InvertSameVectorTest());
				break;
			case "Test 08 - Bubble Sort":
				changeGUIToTest(new Example08BubbleSortTest());
				break;
			case "Test 09 - Selection Sort":
				changeGUIToTest(new Example09SelectionSortTest());
				break;
			case "Test 10 - BinarySearch":
				changeGUIToTest(new Example10BinarySearchTest());
				break;
			default:
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		new RuntimeWindow();
	}
}
