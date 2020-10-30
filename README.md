# SolMet

SolMet is a static analysis based metric calculator tool for Solidity smart contract programs.
It is a **Work In Progress**, supporting the following metrics

- SLOC - number of source code lines
- LLOC - number of logical code lines (lines without empty and comment lines)
- CLOC - number of comment lines
- NF - number of functions
- McCC - McCabe's cyclomatic complexity
- WMC - weighted sum of McCabe's style complexity over the functions of a contract
- NL - the deepest nesting level of control structures in functions summed for a contract
- NLE - nesting level else-if
- NUMPAR -number of parameters
- NOS - number of statements
- DIT - depth of inheritance tree
- NOA - number of ancestors
- NOD - number of descendants
- CBO - coupling between object classes
- NA - number of attributes (i.e. states)
- NOI - number of outgoing invocations (i.e. fan-out)

## Building the tool

You can build the tool with Maven to get an executable jar file:

```
mvn package
```

## Using the tool

Usage is very simple, the built jar is executable.
It requires two parameters:

1.  a Solidity file or a folder containing Solidity files
2.  an output csv file path.

```
java -jar solmet-0.1.jar [input(s)] [output]
```

## Publications

Hegedűs P., "Towards Analyzing the Complexity Landscape of
Solidity Based Ethereum Smart Contracts", ACM/IEEE 1st International Workshop on Emerging Trends in Software Engineering for Blockchain, 2018 [[PDF]](http://publicatio.bibl.u-szeged.hu/14474/1/3hnJuJZyEJkfd6NEz9E0PF.pdf)

## Output

The output is a comma separated file containing the values of the calculated metrics for each analyzed contracts/libraries/interfaces.

## Credits

The parser is based on the excellent antlr4 grammar available at https://github.com/solidityj/solidity-antlr4.
