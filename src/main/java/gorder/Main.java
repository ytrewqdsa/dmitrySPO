package gorder;

import gorder.interperer.ExecutionTypes;
import gorder.interperer.executor.PolishExecutor;
import gorder.interperer.generator.PolishGenerator;
import gorder.interperer.CompileException;
import gorder.parser.ParseException;
import gorder.lexer.TokenizeException;
import gorder.lexer.Lexer;
import gorder.lexer.Token;
import gorder.lexer.Variable;
import gorder.parser.Parser;

public class Main {

    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        PolishGenerator polishGenerator = new PolishGenerator();
        PolishExecutor polishExecutor = new PolishExecutor();
        try {
            lexer.analyze("example.txt");
            lexer.printTokens();

        } catch (TokenizeException e) {
            e.printStackTrace();
        }

        System.out.println();
        try {
            parser.parse(lexer.getTokens());
            System.out.println("\nParser tree:");
            parser.getTree().printTree();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        polishGenerator.generate(parser.getTree());
        System.out.println("\nPolish array:");
        int i = 0;
        for (Token token : polishGenerator.getPolish()) {
            System.out.println(token.getType() + " " + token.getValue());
        }

        System.out.println("\nLabels:");
        for (Variable variable : polishGenerator.getTableOfNames().values()) {
            if (variable.getValueType().equals(ExecutionTypes.LABEL)) {
                System.out.println(variable.getName() + " : " + variable.getValue());
            }
        }
        System.out.println();

        try {
            polishExecutor.execute(polishGenerator.getPolish(), polishGenerator.getTableOfNames());
        } catch (CompileException e) {
            e.printStackTrace();
        }
    }
}
