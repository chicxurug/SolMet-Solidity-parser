package hu.sed.solmet.visitors;

import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.IfStatementContext;

public class NLCounterVisitor extends NestingCounterVisitor {

	@Override
	protected boolean isRelevantIf(IfStatementContext parent) {
		return true;
	}

}
