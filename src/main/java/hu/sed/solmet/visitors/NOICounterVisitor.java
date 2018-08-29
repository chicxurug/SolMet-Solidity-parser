package hu.sed.solmet.visitors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.misc.NotNull;

import hu.sed.parser.antlr4.grammar.solidity.SolidityBaseVisitor;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.ExpressionContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.FunctionCallArgumentsContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.FunctionDefinitionContext;

@SuppressWarnings("deprecation")
public class NOICounterVisitor extends SolidityBaseVisitor<Integer> {

	private Map<String, Set<String>> funcCalls = new HashMap<>();
	private String currentFunc;
	private int anonymCtr = 0;

	@Override
	public Integer visitFunctionDefinition(@NotNull FunctionDefinitionContext ctx) {
		currentFunc = ctx.identifier() == null ? "anonym" + anonymCtr++ : ctx.identifier().getText();
		funcCalls.put(currentFunc, new HashSet<>());
		super.visitFunctionDefinition(ctx);
		return funcCalls.get(currentFunc).size();
	}

	@Override
	public Integer visitFunctionCallArguments(@NotNull FunctionCallArgumentsContext ctx) {
		if (ctx.getParent() instanceof ExpressionContext) {
			ExpressionContext ectx = (ExpressionContext) ctx.getParent();
			funcCalls.get(currentFunc).add(ectx.getChild(0).getText());
		}
		return null;
	}
}
