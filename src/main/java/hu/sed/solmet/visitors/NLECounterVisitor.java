package hu.sed.solmet.visitors;

import org.antlr.v4.runtime.misc.Interval;

import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.IfStatementContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.StatementContext;

public class NLECounterVisitor extends NestingCounterVisitor {

	@Override
	protected boolean isRelevantIf(IfStatementContext ifStm) {
		// Count only if it is not in an else branch of another ifStatement
		boolean ret = true;
		if (ifStm.getParent().getParent() instanceof IfStatementContext) {
			IfStatementContext parentIf = (IfStatementContext) ifStm.getParent().getParent();
			if (!parentIf.getTokens(38).isEmpty()) {
				StatementContext parentStm = (StatementContext) ifStm.getParent();
				Interval elseInt = parentIf.getTokens(38).get(0).getSourceInterval();
				Interval stmInt = parentStm.getSourceInterval();
				ret = !elseInt.adjacent(stmInt);
			}
		}
		return ret;
	}

}
