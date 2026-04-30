import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            return;
        }

        String command = args[0];
        String filename = args[1];

        try {
            String source = Files.readString(Path.of(filename));
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.tokenize();

            switch (command) {
                case "lex":
                    for (Token token : tokens) {
                        System.out.println(token);
                    }
                    break;

                case "parse":
                    Parser parser = new Parser(tokens);
                    Program program = parser.parse();
                    System.out.println(program);
                    break;

                case "run":
                    Parser runParser = new Parser(tokens);
                    Program runProgram = runParser.parse();
                    Evaluator evaluator = new Evaluator();
                    evaluator.execute(runProgram);
                    break;

                default:
                    System.out.println("Unknown command: " + command);
                    printUsage();
                    break;
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("Numerica");
        System.out.println("Usage:");
        System.out.println("  java Main lex <filename>");
        System.out.println("  java Main parse <filename>");
        System.out.println("  java Main run <filename>");
    }
}