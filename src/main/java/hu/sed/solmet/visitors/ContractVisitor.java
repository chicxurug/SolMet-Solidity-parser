package hu.sed.solmet.visitors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.misc.NotNull;

import hu.sed.parser.antlr4.grammar.solidity.SolidityBaseVisitor;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.BlockContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.ContractDefinitionContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.FunctionDefinitionContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.InheritanceSpecifierContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.SourceUnitContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.StateVariableDeclarationContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.StatementContext;
import hu.sed.solmet.helper.LOCCalculator;

@SuppressWarnings("deprecation")
public class ContractVisitor extends SolidityBaseVisitor<Void> {

	private int count;
	private Map<String, Integer> contractDefCounts = new HashMap<>();

	private ContractDefinitionContext currentContract;
	// sloc, lloc, cloc, fcount, wmc, tnl, tnle, tnumpar, tnos, dit, tnoa, tnod, cbo, tna, tnoi
	private Map<ContractDefinitionContext, Integer[]> contractMetrics = new HashMap<>();
	private Map<String, Integer> contractDIT = new HashMap<>();
	private Map<String, Set<String>> contractNOA = new HashMap<>();
	private CBOCounterVisitor cboCounter = new CBOCounterVisitor();
	private String sourceText;
	private NODCounterVisitor ncv = new NODCounterVisitor();

	public ContractVisitor(String sourceText) {
		this.sourceText = sourceText;
	}

	@Override
	public Void visitSourceUnit(@NotNull SourceUnitContext ctx) {
		cboCounter.runInitialization(ctx);
		ncv.visitSourceUnit(ctx);
		super.visitSourceUnit(ctx);
		return null;
	}

	@Override
	public Void visitContractDefinition(@NotNull ContractDefinitionContext ctx) {
		count++;
		int sloc = ctx.getStop().getLine() - ctx.getStart().getLine() + 1;
		LOCCalculator locCalc = new LOCCalculator("\n", ctx.getStart().getLine() - 1, ctx.getStop().getLine() - 1);
		locCalc.calculateLOCMetrics(sourceText);
		int lloc = locCalc.getLLOC();
		int cloc = locCalc.getCLOC();
		contractDefCounts.put(ctx.getStart().getText(),
				contractDefCounts.getOrDefault(ctx.getStart().getText(), 0) + 1);
		Integer[] mets = new Integer[15];
		mets[0] = sloc;
		mets[1] = lloc;
		mets[2] = cloc;
		mets[3] = 0;
		mets[4] = 0;
		mets[5] = 0;
		mets[6] = 0;
		mets[7] = 0;
		mets[8] = 0;
		mets[9] = calculateDIT(ctx);
		mets[10] = calculateNOA(ctx);
		mets[11] = ncv.getNODCount(ctx);
		mets[12] = cboCounter.calculateCBO(ctx);
		mets[13] = 0;
		mets[14] = 0;
		currentContract = ctx;
		contractMetrics.put(ctx, mets);
		super.visitContractDefinition(ctx);
		currentContract = null;
		return null;
	}

	private int calculateDIT(ContractDefinitionContext ctx) {
		int dit = 0;
		for (InheritanceSpecifierContext ictx : ctx.inheritanceSpecifier()) {
			String baseName = ictx.userDefinedTypeName().identifier().get(0).getText();
			if (contractDIT.get(baseName) + 1 > dit) {
				dit = contractDIT.get(baseName) + 1;
			}
		}
		contractDIT.put(ctx.identifier().getText(), dit);
		return dit;
	}

	private int calculateNOA(ContractDefinitionContext ctx) {
		contractNOA.put(ctx.identifier().getText(), new HashSet<>());
		for (InheritanceSpecifierContext ictx : ctx.inheritanceSpecifier()) {
			String baseName = ictx.userDefinedTypeName().identifier().get(0).getText();
			contractNOA.get(ctx.identifier().getText()).add(baseName);
			contractNOA.get(ctx.identifier().getText()).addAll(contractNOA.get(baseName));
		}

		return contractNOA.get(ctx.identifier().getText()).size();
	}

	@Override
	public Void visitFunctionDefinition(@NotNull FunctionDefinitionContext ctx) {
		contractMetrics.get(currentContract)[3]++;
		int mcc = new McCabeCounterVisitor().visitFunctionDefinition(ctx);
		contractMetrics.get(currentContract)[4] += mcc;
		int nl = new NLCounterVisitor().visitFunctionDefinition(ctx);
		contractMetrics.get(currentContract)[5] += nl;
		int nle = new NLECounterVisitor().visitFunctionDefinition(ctx);
		contractMetrics.get(currentContract)[6] += nle;
		int numpar = new NUMPARCounterVisitor().visitFunctionDefinition(ctx);
		contractMetrics.get(currentContract)[7] += numpar;
		int noi = new NOICounterVisitor().visitFunctionDefinition(ctx);
		contractMetrics.get(currentContract)[14] += noi;
		super.visitFunctionDefinition(ctx);
		return null;
	}

	@Override
	public Void visitStatement(@NotNull StatementContext ctx) {
		if (!(ctx.getChild(0) instanceof BlockContext)) {
			contractMetrics.get(currentContract)[8]++;
		}
		super.visitStatement(ctx);
		return null;
	}
	
	@Override
	public Void visitStateVariableDeclaration(@NotNull StateVariableDeclarationContext ctx) {
		contractMetrics.get(currentContract)[13]++;
		super.visitStateVariableDeclaration(ctx);
		return null;
	}

	public int getContractCount() {
		return contractDefCounts.getOrDefault("contract", 0);
	}

	public int getLibraryCount() {
		return contractDefCounts.getOrDefault("library", 0);
	}

	public int getInterfaceCount() {
		return contractDefCounts.getOrDefault("interface", 0);
	}

	public int getTotalContractCount() {
		return count;
	}

	public Map<ContractDefinitionContext, Integer[]> getMetricMap() {
		return contractMetrics;
	}
}
