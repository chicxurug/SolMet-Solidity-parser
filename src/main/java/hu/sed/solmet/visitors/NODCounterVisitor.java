package hu.sed.solmet.visitors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.antlr.v4.runtime.misc.NotNull;

import hu.sed.parser.antlr4.grammar.solidity.SolidityBaseVisitor;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.ContractDefinitionContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.InheritanceSpecifierContext;

@SuppressWarnings("deprecation")
public class NODCounterVisitor extends SolidityBaseVisitor<Void> {

	private Map<String, Integer> contractNOD = new HashMap<>();
	private Map<String, Set<String>> descendantMap = new HashMap<>();

	@Override
	public Void visitContractDefinition(@NotNull ContractDefinitionContext ctx) {
		for (InheritanceSpecifierContext ictx : ctx.inheritanceSpecifier()) {
			String baseName = ictx.userDefinedTypeName().identifier().get(0).getText();
			if (descendantMap.get(baseName) == null) {
				descendantMap.put(baseName, new HashSet<>());
			}
			descendantMap.get(baseName).add(ctx.identifier().getText());
		}
		super.visitContractDefinition(ctx);
		return null;
	}

	public int getNODCount(ContractDefinitionContext ctx) {
		Stack<String> cStack = new Stack<>();
		Map<String, Set<String>> descMap = new HashMap<>();
		for (String contract : descendantMap.keySet()) {
			Set<String> tmpSet = new HashSet<>();
			tmpSet.addAll(descendantMap.get(contract));
			descMap.put(contract, tmpSet);
			cStack.addAll(descendantMap.get(contract));
			while (!cStack.empty()) {
				String top = cStack.pop();
				if (descendantMap.get(top) == null) {
					continue;
				}
				descMap.get(contract).addAll(descendantMap.get(top));
				cStack.addAll(descendantMap.get(top));
			}
			contractNOD.put(contract, descMap.get(contract).size());
		}
		return contractNOD.get(ctx.identifier().getText()) == null ? 0 : contractNOD.get(ctx.identifier().getText());
	}
}
