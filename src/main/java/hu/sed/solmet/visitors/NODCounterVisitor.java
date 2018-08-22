package hu.sed.solmet.visitors;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.misc.NotNull;

import hu.sed.parser.antlr4.grammar.solidity.SolidityBaseVisitor;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.ContractDefinitionContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.InheritanceSpecifierContext;

@SuppressWarnings("deprecation")
public class NODCounterVisitor extends SolidityBaseVisitor<Void> {

	private Map<String, Integer> contractNOD = new HashMap<>();
	
	@Override
	public Void visitContractDefinition(@NotNull ContractDefinitionContext ctx) {
		contractNOD.put(ctx.identifier().getText(), 0);
		for (InheritanceSpecifierContext ictx : ctx.inheritanceSpecifier()) {
			String baseName = ictx.userDefinedTypeName().identifier().get(0).getText();
			contractNOD.put(baseName, contractNOD.get(baseName) + 1);
		}
		return null;
	}
	
	public int getNODCount(ContractDefinitionContext ctx) {
		return contractNOD.get(ctx.identifier().getText());
	}
}
