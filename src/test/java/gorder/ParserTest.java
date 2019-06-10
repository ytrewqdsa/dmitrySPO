package gorder;

import org.junit.jupiter.api.Test;
import gorder.parser.ParserTypes;
import gorder.lexer.LexerTypes;
import gorder.parser.ParseException;
import gorder.lexer.TokenizeException;
import gorder.lexer.Lexer;
import gorder.parser.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserTest {
    private static final String FILE_NAME = "testfile.txt";

    @Test
    public void parse() {
        exceptionTest("Required: " + ParserTypes.VALUE, "func int get(int i, double t) {", "while () {", "i = 234 + 74;", "}}");
        exceptionTest("Required: " + ParserTypes.TYPE, "func int get(int i, double t,);");
        exceptionTest("Required: " + LexerTypes.VAR, "func int get(int i, double t, int);");
        exceptionTest("Required: " + ParserTypes.VALUE, "int i = !++(-(--20 - --34));");
        exceptionTest("Required: " + ParserTypes.BINARY_OP, "int i = (+1)");
        exceptionTest("Required: VALUE", "int i = (-1  +* 13);");

        processingTest("int i = 2 + (1 + 34 - (21/20)) + getInt(123, 567);");
        processingTest("int i = 2 + (1 + 34 - (21/20)) + getInt();");
        processingTest("int i = 2 + (1 + 34 - (21/20)) + getInt(123);");
        processingTest("int i = (1 + 34 - (21/20));", "int i = (+1);");
        processingTest("int i = (1 + 34 - (21/20));", "int i;", "double b;");

        processingTest("func int get();");
        processingTest("func int get(int i);");
        processingTest("func int get(int i, double t);");
        processingTest("func int get(int i, double t) {", "int b = 0;", "double a = 123 * 26-456;}");
        processingTest("func int get(int i, double t) {", "while (20 + 30 < 42) {", "i = 234 + 74;", "}}");

        processingTest("func int testFunc(double i, int b) {\n" +
                "    do {\n" +
                "        int a = 50;\n" +
                "        do {\n" +
                "            int b = 100;\n" +
                "        } while (i > 30);\n" +
                "    } while (20 > 30);\n" +
                "}");
        processingTest("double a;\n" +
                "double c;\n" +
                "\n" +
                "func int main(double b) {\n" +
                "    HashSet<int> tmp;\n" +
                "    List<double> test;\n" +
                "    b = test::add(10);\n" +
                "    b = test::add(0, 5);\n" +
                "    b = 10;\n" +
                "    b = tmp::add(b);\n" +
                "    b = tmp::add(3.1);\n" +
                "    b = tmp::add(3.5);\n" +
                "    a = getValue(tmp::contains(3));\n" +
                "    c = getValue(tmp::contains(3.1));\n" +
                "    print;\n" +
                "    return 0;\n" +
                "}\n" +
                "\n" +
                "func int getValue(int val) {\n" +
                "    return val;\n" +
                "}");
        processingTest("func int testFunc(double i, int b) {\n" +
                "    while (20 > 10) {\n" +
                "        while (i > 30) {\n" +
                "            int i = i + 1;\n" +
                "        }\n" +
                "    }\n" +
                "}");
        processingTest("func int testFunc(double i, int b) {\n" +
                "    if (20) {\n" +
                "        int i = 0;\n" +
                "    } else if (30) {\n" +
                "        int i = 100;\n" +
                "    } else if (50) {\n" +
                "        int i = 200;\n" +
                "    } else {\n" +
                "        int i = 500;\n" +
                "    }\n" +
                " }");
        processingTest("func int testFunc(double i, int b) {\n" +
                "    for (int i = 0, int d = 30 ;i < 10; i = 20, b = 30) {\n" +
                "        int b = 300;\n" +
                "    }\n" +
                "}");
        processingTest("func int testFunc(double i, int b) {\n" +
                "    for (i = 0, int d = 30, b = 10;i < 10; i = 20, b = 30) {\n" +
                "        int b = 300;\n" +
                "    }\n" +
                "}");
    }

    private void processingTest(String... code) {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(FILE_NAME);
            for (String line : code) {
                writer.println(line);
            }
            writer.close();
            lexer.analyze(FILE_NAME);
            parser.parse(lexer.getTokens());
        } catch (ParseException | FileNotFoundException | TokenizeException e) {
            e.printStackTrace();
            assertTrue(false);
        } finally {
            writer.close();
            new File(FILE_NAME).delete();
        }
    }

    private void exceptionTest(String exceptionMsg, String... code) {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(FILE_NAME);
            for (String line : code) {
                writer.println(line);
            }
            writer.close();
            lexer.analyze(FILE_NAME);

            ParseException thrown = assertThrows(ParseException.class,
                    () -> {
                        parser.parse(lexer.getTokens());
                    });
            assertTrue(thrown.getMessage().contains(exceptionMsg));
        } catch (FileNotFoundException | TokenizeException e) {
            e.printStackTrace();
        } finally {
            writer.close();
            new File(FILE_NAME).delete();
        }
    }
}