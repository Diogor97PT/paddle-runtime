package pt.iscte.paddle.runtime;

import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.SMALLER_EQ;
import static pt.iscte.paddle.model.IType.INT;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import pt.iscte.paddle.interpreter.ExecutionError;
import pt.iscte.paddle.interpreter.IExecutionData;
import pt.iscte.paddle.interpreter.IMachine;
import pt.iscte.paddle.interpreter.IProgramState;
import pt.iscte.paddle.interpreter.IValue;
import pt.iscte.paddle.javardise.ClassWidget;
import pt.iscte.paddle.javardise.Constants;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IBlockElement;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariableDeclaration;

public class DemoWindow {
	
	private static Shell shell;
	
	public static void main(String[] args) {
		
		//Initialize code
		IModule module = IModule.create();										//Criar classe
		module.setId("StepperTest");											//dar nome à classe
		
		IProcedure procedure = module.addProcedure(INT.array().reference());	//criar função
		procedure.setId("naturals");
		
		IVariableDeclaration n = procedure.addParameter(INT);					//Parâmetro da Função
		n.setId("n");
		
		IBlock body = procedure.getBody();										//corpo da função
		
		IVariableDeclaration array = body.addVariable(INT.array().reference());
		array.setId("array");
		body.addAssignment(array, INT.array().heapAllocation(n));
		
		IVariableDeclaration i = body.addVariable(INT, INT.literal(0));
		i.setId("i");
		
		ILoop loop = body.addLoop(SMALLER_EQ.on(i, n));
		loop.addArrayElementAssignment(array, ADD.on(i, INT.literal(1)), i);
		loop.addAssignment(i, ADD.on(i, INT.literal(1)));
		
		body.addReturn(array);
		
		IProgramState state = IMachine.create(module);
		
		//Start Window
		Display display = new Display();
		shell = new Shell(display);
		shell.setBackground(Constants.COLOR_BACKGROUND);
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = 50;
		layout.marginLeft = 50;
		layout.verticalSpacing = 20;
		shell.setLayout(layout);

		ClassWidget widget = new ClassWidget(shell, module);
		widget.setEnabled(false);

		List<IBlockElement> children = procedure.getBody().getChildren();

		Composite comp = new Composite(shell, SWT.BORDER);
		comp.setLayout(new FillLayout());
		
		System.out.println(children);
		
		Button markRoles = new Button(comp, SWT.PUSH);
		markRoles.setText("Mark Roles");
		markRoles.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		
		Button executeCode = new Button(comp, SWT.PUSH);
		executeCode.setText("Executar Código");
		executeCode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					IExecutionData data = state.execute(procedure, 5);
					
					IValue value = data.getReturnValue();
					
					System.out.println("\n" + "RESULT: " + value);
				} catch (ExecutionError e1) {
					e1.printStackTrace();
				}
			}
		});

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	
//	public String generateArrayErrorString(ArrayIndexError e) {
//		int invalidPos = e.getInvalidIndex();
//		String variable = ((IVariableExpression)e.getIndexExpression()).getVariable().getId();
//		String array = e.getTarget().getId();
//		int arrayDimension = e.getIndexDimension();	//Dimensão da array que deu erro
//
//		String tamanhoArray = "Não_implementado";
//
//		StringBuilder sb = new StringBuilder("Tentativa de acesso à posição ");
//		sb.append(invalidPos);
//		sb.append(", que é inválida para o vetor ");
//		sb.append(array);
//		sb.append(" (comprimento " + arrayDimension + ", índices válidos [0, " + tamanhoArray + "]. ");
//		sb.append("O acesso foi feito através da variável ");
//		sb.append(variable);
//
//		System.out.println(procedure.getVariables());
//		System.out.println(variable);
//		
//		IVariableRole role = IVariableRole.match(procedure.getVariable(variable));
//		System.out.println(role.getClass());
//		
//		/*if(Stepper.isStepper(procedure.getVariable(variable))) {
//			sb.append(", que é um iterador para as posições do vetor " + array);
//		} else {
//			sb.append(".");
//		}*/
//
//		return sb.toString();
//	}
}
