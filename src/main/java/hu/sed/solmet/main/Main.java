package hu.sed.solmet.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import hu.sed.parser.antlr4.grammar.solidity.SolidityLexer;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.ContractDefinitionContext;
import hu.sed.solmet.visitors.ContractVisitor;

public class Main {

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static void main(String[] args) throws IOException {
		PrintWriter outFile = new PrintWriter(new File(args[1]));
		outFile.println("SolidityFile;ContractName;Type;SLOC;LLOC;CLOC;NF;WMC;NL;Avg. McCC;Avg. NL;");
		List<String> solPaths = new ArrayList<>();
		File path = new File(args[0]);
		if (path.isFile()) {
			solPaths.add(args[0]);
		} else {
			for (String s : path.list()) {
				solPaths.add(new File(path, s).getAbsolutePath());
			}
		}
		for (String solPath : solPaths) {
//			System.out.println(new File(solPath).getName());
			String contractCode = readFile(solPath, Charset.forName("UTF-8"));
			CharStream charStream = CharStreams.fromString(contractCode);
			SolidityLexer lexer = new SolidityLexer(charStream);
			TokenStream tokens = new CommonTokenStream(lexer);
			SolidityParser parser = new SolidityParser(tokens);

			ContractVisitor contractVisitor = new ContractVisitor(contractCode);
			contractVisitor.visit(parser.sourceUnit());
			Map<ContractDefinitionContext, Integer[]> metrics = contractVisitor.getMetricMap();
			for (ContractDefinitionContext contract : metrics.keySet()) {
				outFile.print(new File(solPath).getName() + ";" + contract.getChild(1).getText() + ";"
						+ contract.getChild(0).getText() + ";");
				for (int i = 0; i < metrics.get(contract).length; i++) {
					outFile.print(metrics.get(contract)[i] + ";");
				}
				// Avg. McCC
				outFile.print((double) metrics.get(contract)[4] / metrics.get(contract)[3] + ";");
				// Avg. NL
				outFile.println((double) metrics.get(contract)[5] / metrics.get(contract)[3] + ";");
			}
		}
		outFile.close();
	}

}
