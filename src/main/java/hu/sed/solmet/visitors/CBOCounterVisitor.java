package hu.sed.solmet.visitors;

import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.misc.NotNull;

import hu.sed.parser.antlr4.grammar.solidity.SolidityBaseVisitor;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.ContractDefinitionContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.InheritanceSpecifierContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.SourceUnitContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.UserDefinedTypeNameContext;

@SuppressWarnings("deprecation")
public class CBOCounterVisitor extends SolidityBaseVisitor<Integer> {

	private boolean pre_flight = false;
	private Set<String> contractIds = new HashSet<>();
	private Set<String> alreadyCounted = new HashSet<>();
	int cbo = 0;

	public void runInitialization(SourceUnitContext ctx) {
		pre_flight = true;
		visitSourceUnit(ctx);
	}

	public int calculateCBO(@NotNull ContractDefinitionContext ctx) {
		pre_flight = false;
		return visitContractDefinition(ctx);
	}

	@Override
	public Integer visitContractDefinition(@NotNull ContractDefinitionContext ctx) {
		if (pre_flight) {
			contractIds.add(ctx.identifier().getText());
		} else {
			cbo = 0;
			alreadyCounted.clear();
			super.visitContractDefinition(ctx);
		}
		return cbo;
	}

	@Override
	public Integer visitUserDefinedTypeName(@NotNull UserDefinedTypeNameContext ctx) {
		String usedContract = ctx.identifier().get(0).getText();
		if (contractIds.contains(usedContract) && !alreadyCounted.contains(usedContract)
				&& !(ctx.getParent() instanceof InheritanceSpecifierContext)) {
			cbo++;
			alreadyCounted.add(usedContract);
		}
		super.visitUserDefinedTypeName(ctx);
		return null;
	}

}
