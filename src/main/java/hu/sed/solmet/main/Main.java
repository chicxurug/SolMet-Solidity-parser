package hu.sed.solmet.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import hu.sed.parser.antlr4.grammar.solidity.SolidityLexer;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser;
import hu.sed.parser.antlr4.grammar.solidity.SolidityParser.ContractDefinitionContext;
import hu.sed.solmet.visitors.ContractVisitor;

public class Main {

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	private static Options createCmdLineOptions() {
		Options options = new Options();
		OptionGroup input = new OptionGroup();
		input.addOption(Option.builder("inputFile").hasArg().argName("file").build());
		input.addOption(Option.builder("basePath").hasArg().argName("path").build());
		input.setRequired(true);
		options.addOption(Option.builder("outFile").hasArg().argName("name").desc("output file").required().build());
		options.addOptionGroup(input);
		// options.addOption(Option.builder("help").build());
		return options;
	}

	public static void main(String[] args) throws IOException {
		Options options = null;
		try {
			CommandLineParser cmdParser = new DefaultParser();
			options = createCmdLineOptions();
			CommandLine cmdLine = cmdParser.parse(options, args);
			List<String> solPaths = new ArrayList<>();

			if (cmdLine.hasOption("inputFile")) {
				solPaths.add(cmdLine.getOptionValue("inputFile"));
			}
			if (cmdLine.hasOption("basePath")) {
				File path = new File(cmdLine.getOptionValue("basePath"));
				for (String s : path.list()) {
					solPaths.add(new File(path, s).getAbsolutePath());
				}
			}
			if (solPaths.isEmpty()) {
				throw new ParseException("Empty contract list!");
			}
			
			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(cmdLine.getOptionValue("outFile")));
					CSVPrinter csvPrinter = new CSVPrinter(writer,
							CSVFormat.DEFAULT.withHeader("SolidityFile", "ContractName", "Type", "SLOC", "LLOC", "CLOC",
									"NF", "WMC", "NL", "NLE", "NUMPAR", "NOS", "DIT", "NOA", "NOD", "CBO", "NA", "NOI",
									"Avg. McCC", "Avg. NL", "Avg. NLE", "Avg. NUMPAR", "Avg. NOS", "Avg. NOI").withDelimiter(';'));) {

				for (String solPath : solPaths) {
					String contractCode = readFile(solPath, Charset.forName("UTF-8"));
					CharStream charStream = CharStreams.fromString(contractCode);
					SolidityLexer lexer = new SolidityLexer(charStream);
					TokenStream tokens = new CommonTokenStream(lexer);
					SolidityParser parser = new SolidityParser(tokens);

					ContractVisitor clontractVisitor = new ContractVisitor(contractCode);
					clontractVisitor.visit(parser.sourceUnit());
					Map<ContractDefinitionContext, Integer[]> metrics = clontractVisitor.getMetricMap();
					for (ContractDefinitionContext contract : metrics.keySet()) {
						ArrayList<Object> record = new ArrayList<Object>();
						record.add(new File(solPath).getName());
						record.add(contract.getChild(1).getText());
						record.add(contract.getChild(0).getText());
						record.addAll(Arrays.asList(metrics.get(contract)));
						record.add((double) metrics.get(contract)[4] / metrics.get(contract)[3]);
						record.add((double) metrics.get(contract)[5] / metrics.get(contract)[3]);
						record.add((double) metrics.get(contract)[6] / metrics.get(contract)[3]);
						record.add((double) metrics.get(contract)[7] / metrics.get(contract)[3]);
						record.add((double) metrics.get(contract)[8] / metrics.get(contract)[3]);
						record.add((double) metrics.get(contract)[14] / metrics.get(contract)[3]);
						csvPrinter.printRecord(record);
						csvPrinter.flush();
					}
				}
			}
		} catch (ParseException exp) {
			System.out.println(exp.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar SolMet-1.0-SNAPSHOT.jar", options);
		}
	}

}
