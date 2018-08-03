package hu.sed.solmet.visitors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import hu.sed.parser.antlr4.grammar.solidity.SolidityBaseVisitor;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.DoWhileStatementContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.ForStatementContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.FunctionDefinitionContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.IfStatementContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.ModifierDefinitionContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.StatementContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.WhileStatementContext;

@SuppressWarnings("deprecation")
public abstract class NestingCounterVisitor extends SolidityBaseVisitor<Integer> {

	private int nl;

	public NestingCounterVisitor() {
		nl = 0;
	}

	@Override
	public Integer visitFunctionDefinition(@NotNull FunctionDefinitionContext ctx) {
		super.visitFunctionDefinition(ctx);
		return nl;
	}

	@Override
	public Integer visitStatement(@NotNull StatementContext ctx) {
		ParserRuleContext parent = ctx.getParent();

		if (parent instanceof ModifierDefinitionContext) {
			return null;
		}
		int newNL = 0;
		while (!(parent instanceof FunctionDefinitionContext)) {
			if (parent instanceof ModifierDefinitionContext) {
				break;
			}
			if (parent instanceof IfStatementContext && isRelevantIf((IfStatementContext)parent) || parent instanceof WhileStatementContext
					|| parent instanceof ForStatementContext || parent instanceof DoWhileStatementContext) {

				newNL++;
			}
			parent = parent.getParent();
		}
		if (newNL > nl) {
			nl = newNL;
		}
		super.visitStatement(ctx);
		return null;
	}

	abstract protected boolean isRelevantIf(IfStatementContext parent);
}
