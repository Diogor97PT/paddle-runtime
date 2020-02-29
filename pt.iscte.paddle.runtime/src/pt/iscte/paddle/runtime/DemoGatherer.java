package pt.iscte.paddle.runtime;
import java.io.File;

import pt.iscte.paddle.javali.translator.Translator;
import pt.iscte.paddle.model.IModule;
import pt.iscte.paddle.model.IProcedure;
import pt.iscte.paddle.model.IVariable;
import pt.iscte.paddle.model.roles.IVariableRole;

public class DemoGatherer {

	public static void main(String[] args) {
		Translator translator = new Translator(new File("sum.javali").getAbsolutePath());
		IModule module = translator.createProgram();
		IProcedure sum = module.getProcedures().iterator().next(); // first procedure

		for (IVariable var : sum.getVariables()) {
			IVariableRole role = IVariableRole.match(var);
			System.out.println(var.getId() + ": " + role);
		}

	}
}
