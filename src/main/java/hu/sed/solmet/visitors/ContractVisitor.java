package hu.sed.solmet.visitors;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.misc.NotNull;

import hu.sed.parser.antlr4.grammar.solidity.SolidityBaseVisitor;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.ContractDefinitionContext;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.FunctionDefinitionContext;
import hu.sed.solmet.helper.LOCCalculator;


public class ContractVisitor extends SolidityBaseVisitor<Void> {
    
	private int count;
	private Map<String, Integer> contractDefCounts = new HashMap<>();	
	
	private ContractDefinitionContext currentContract;
	// sloc, lloc, cloc, fcount, wmc, tnl
	private Map<ContractDefinitionContext, Integer[]> contractMetrics = new HashMap<>();
	private String sourceText;
	
	public ContractVisitor(String sourceText) {
		this.sourceText = sourceText;
	}
    
	@Override
    public Void visitContractDefinition(@NotNull ContractDefinitionContext ctx) {
		count++;
		int sloc = ctx.getStop().getLine() - ctx.getStart().getLine() + 1;
		LOCCalculator locCalc = new LOCCalculator("\n", ctx.getStart().getLine()-1, ctx.getStop().getLine()-1);
		locCalc.calculateLOCMetrics(sourceText);
		int lloc = locCalc.getLLOC();
		int cloc = locCalc.getCLOC();
        contractDefCounts.put(ctx.getStart().getText(), contractDefCounts.getOrDefault(ctx.getStart().getText(), 0) + 1 );
        Integer[] mets = new Integer[6];
        mets[0] = sloc;
        mets[1] = lloc;
        mets[2] = cloc;
        mets[3] = 0;
        mets[4] = 0;
        mets[5] = 0;
        currentContract = ctx;
        contractMetrics.put(ctx, mets);
        super.visitContractDefinition(ctx);
        currentContract = null;
        return null;
    }
	
	@Override
    public Void visitFunctionDefinition(@NotNull FunctionDefinitionContext ctx) {
		contractMetrics.get(currentContract)[3]++; 
		int mcc = new McCabeCounterVisitor().visitFunctionDefinition(ctx);
		contractMetrics.get(currentContract)[4] += mcc;
		int nl = new NLCounterVisitor().visitFunctionDefinition(ctx);
		contractMetrics.get(currentContract)[5] += nl;
		super.visitFunctionDefinition(ctx);
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

