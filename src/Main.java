import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Main <lex|parse> <filename>");
            return;
        }

        String command = args[0];
        String filename = args[1];

        try {
            String source = Files.readString(Path.of(filename));
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.tokenize();

            if (command.equals("lex")) {
                for (Token token : tokens) {
                    System.out.println(token);
                }
            } else if (command.equals("parse")) {
                Parser parser = new Parser(tokens);
                Program program = parser.parse();
                System.out.println(program);
            } else {
                System.out.println("Unknown command: " + command);
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}