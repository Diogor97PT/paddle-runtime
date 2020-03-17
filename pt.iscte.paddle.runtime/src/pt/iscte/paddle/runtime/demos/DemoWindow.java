package pt.iscte.paddle.runtime.demos;

import static pt.iscte.paddle.model.IOperator.ADD;
import static pt.iscte.paddle.model.IOperator.SMALLER_EQ;
import static pt.iscte.paddle.model.IType.INT;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import pt.iscte.paddle.interpreter.ArrayIndexError;
import pt.iscte.paddle.interpreter.ExecutionError;
import pt.iscte.paddle.interpreter.IExecutionData;
import pt.iscte.paddle.interpreter.IMachine;
import pt.iscte.paddle.interpreter.IProgramState;
import pt.iscte.paddle.interpreter.IValue;
import pt.iscte.paddle.javardise.ClassWidget;
import pt.iscte.paddle.javardise.Constants;
import pt.iscte.paddle.javardise.Decoration;
import pt.iscte.paddle.javardise.MarkerService;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IArrayElementAssignment;
import pt.iscte.paddle.model.IBlock;
import pt.iscte.paddle.model.IBlock.IVisitor;
import pt.iscte.paddle.model.IBlockElement;
import pt.iscte.paddle.model.ILoop;
import pt.iscte.paddle.model.IModel2CodeTranslator;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IProgramElement;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.model.IVariableExpression;
import pt.iscte.paddle.model.roles.IStepper;
import pt.iscte.paddle.model.roles.IVariableRole;

public class DemoWindow {
	
	private static Shell shell;
	private static HyperlinkedText text;
	
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
		
		String code = module.translate(new IModel2CodeTranslator.Java());
		System.out.println(code);
		
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
				Color blue = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
				HyperlinkedText txt = new HyperlinkedText(e1 -> MarkerService.mark(blue, e1));
				txt.line("Roles present in Procedure:");
				for(IVariableDeclaration var : procedure.getVariables()) {
					IVariableRole role = IVariableRole.match(var);
					txt.line(var.getId() + " : " + role.toString());
				}
				txt.create(shell, SWT.BORDER);
				
				for(IVariableDeclaration var : procedure.getVariables()) {
					IVariableRole role = IVariableRole.match(var);
					Decoration d = MarkerService.addDecoration(var, role.toString(), Decoration.Location.RIGHT);
					if(d == null) continue;		//Não funciona nos parametros da função
					d.show();
				}
				
				shell.pack();
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
				} catch (ArrayIndexError e1) {
					//e1.printStackTrace();
					generateLink(e1);
				} catch (ExecutionError e1) {
					e1.printStackTrace();
				}
			}
		});
		
		/*text = new HyperlinkedText(e -> MarkerService.mark(blue, e))
			.line("Teste")
			.create(shell, SWT.BORDER);*/

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	
	private static void generateLink(ArrayIndexError e) {
		int invalidPos = e.getInvalidIndex();
		IVariableDeclaration variable = ((IVariableExpression)e.getIndexExpression()).getVariable();
		IVariableDeclaration array = ((IVariableExpression)e.getTarget()).getVariable();
		int arrayDimension = e.getIndexDimension();	//Dimensão da array que deu erro
		
		array.getOwnerProcedure().accept(new IVisitor() {
			@Override
			public boolean visit(IArrayElementAssignment assignment) {
				return IVisitor.super.visit(assignment);
			}
		});
		
		
		IProgramElement exceptionPlace = e.getSourceElement();
		System.out.println(e.getSourceElement());
		
		String tamanhoArray = "Não_implementado";
		
		Color blue = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
		text = new HyperlinkedText(c -> MarkerService.mark(blue, c))
				.words("Tentativa de acesso à posição ")
				.words(Integer.toString(invalidPos))
				.words(", que é inválida para o ")
				.link("vetor " + array.getId(), array)
				.words(" (comprimento " + arrayDimension + ", índices válidos [0, " + tamanhoArray + "]. ")
				.newline()
				.words("O acesso foi feito através da ")
				.link("variável i", variable);
		
		IVariableRole role = IVariableRole.match(variable);
		if(role instanceof IStepper) {
			text.words(", que é um iterador para as posições do vetor " + array);
		}
		
		text.words(" | Teste ").link("TTTTTT", exceptionPlace);
		
		text.create(shell, SWT.BORDER);
		
		shell.pack();
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
