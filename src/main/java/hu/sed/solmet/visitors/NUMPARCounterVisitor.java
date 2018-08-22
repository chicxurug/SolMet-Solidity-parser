package hu.sed.solmet.visitors;

import org.antlr.v4.runtime.misc.NotNull;

import hu.sed.parser.antlr4.grammar.solidity.SolidityBaseVisitor;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.FunctionDefinitionContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.ParameterListContext;

@SuppressWarnings("deprecation")
public class NUMPARCounterVisitor extends SolidityBaseVisitor<Integer> {

	private int numpar;

	public NUMPARCounterVisitor() {
		numpar = 0;
	}

	@Override
	public Integer visitFunctionDefinition(@NotNull FunctionDefinitionContext ctx) {
		super.visitFunctionDefinition(ctx);
		return numpar;
	}

	@Override
	public Integer visitParameterList(@NotNull ParameterListContext ctx) {
		if (ctx.getParent() instanceof FunctionDefinitionContext) {
			numpar = ctx.parameter().size();
		}
		return 0;
	}
}
