package pt.iscte.paddle.runtime.messages;

import java.util.Map;

import pt.iscte.paddle.interpreter.ArrayIndexError;
import pt.iscte.paddle.interpreter.ExecutionError;
import pt.iscte.paddle.javardise.MarkerService;
import pt.iscte.paddle.javardise.util.HyperlinkedText;
import pt.iscte.paddle.model.IVariableDeclaration;
import pt.iscte.paddle.runtime.Runtime;
import pt.iscte.paddle.runtime.RuntimeWindow.InterfaceColor;

public interface IMessage {

	static IMessage getMessage(HyperlinkedText text, ExecutionError e, Runtime runtime) {
		
		if(e instanceof ArrayIndexError) {
			return new ArrayIndexErrorMessage(text, (ArrayIndexError)e, runtime);
		}
		
		return EMPTY;
	}
	
	public HyperlinkedText getText();
	
	public Map<IVariableDeclaration, String> getVarValues();
	
	public void generateShortText();
	
	IMessage EMPTY = new IMessage() {
		
		@Override
		public HyperlinkedText getText() {
			HyperlinkedText text = new HyperlinkedText(e1 -> MarkerService.mark(InterfaceColor.BLUE.getColor(), e1));
			return text.line("Empty Message");
		}
		
		@Override
		public void generateShortText() {}

		@Override
		public Map<IVariableDeclaration, String> getVarValues() {
			return null;
		}
	};
	
}
