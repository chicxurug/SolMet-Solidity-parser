package hu.sed.solmet.visitors;

import org.antlr.v4.runtime.misc.NotNull;

import hu.sed.parser.antlr4.grammar.solidity.SolidityBaseVisitor;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.DoWhileStatementContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.ForStatementContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.FunctionDefinitionContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.IfStatementContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.WhileStatementContext;

@SuppressWarnings("deprecation")
public class McCabeCounterVisitor extends SolidityBaseVisitor<Integer> {

	private int mcc;
	
	public McCabeCounterVisitor() {
		mcc = 0;
	}
	
	@Override
	public Integer visitFunctionDefinition(@NotNull FunctionDefinitionContext ctx) {
		super.visitFunctionDefinition(ctx);
		return mcc + 1;
	}

	@Override
	public Integer visitIfStatement(@NotNull IfStatementContext ctx) {
		mcc++;
		super.visitIfStatement(ctx);
		return null;
	}
	
	@Override
	public Integer visitWhileStatement(@NotNull WhileStatementContext ctx) {
		mcc++;
		super.visitWhileStatement(ctx);
		return null;
	}
	
	@Override
	public Integer visitForStatement(@NotNull ForStatementContext ctx) {
		mcc++;
		super.visitForStatement(ctx);
		return null;
	}
	
	@Override
	public Integer visitDoWhileStatement(@NotNull DoWhileStatementContext ctx) {
		mcc++;
		super.visitDoWhileStatement(ctx);
		return null;
	}
}
