package gorder;

import gorder.lexer.LexerTypes;
import org.junit.jupiter.api.Test;
import gorder.interperer.executor.PolishExecutor;
import gorder.interperer.generator.PolishGenerator;
import gorder.interperer.CompileException;
import gorder.parser.ParseException;
import gorder.lexer.TokenizeException;
import gorder.lexer.Lexer;
import gorder.lexer.Variable;
import gorder.parser.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PolishExecutorTest {
    private static final String FILE_NAME = "testfile.txt";

    @Test
    void count() {
        processText(new double[]{405d}
                , new String[]{"d"}
                , "int d = 0;\n" +
                        "\n" +
                        "func int main(double i, int b) {\n" +
                        "    i = 0;\n" +
                        "    b = 0;\n" +
                        "    for (i = 0; i < 5; i += 1) {\n" +
                        "        b += 1;\n" +
                        "        for (int c = 0; c < 20; c += 1) {\n" +
                        "            b += 2;\n" +
                        "        }\n" +
                        "        for (int c = 0; c < 20; c += 1) {\n" +
                        "            b += 2;\n" +
                        "        }\n" +
                        "        d = b;\n" +
                        "    }\n" +
                        "    return 0;\n" +
                        "}");
        processText(new double[]{81d}
                , new String[]{"d"}
                , "int d = 0;\n" +
                        "\n" +
                        "func int main(double i, int b) {\n" +
                        "    i = 0;\n" +
                        "    b = 0;\n" +
                        "    for (i = 0; i < 5; i += 1) {\n" +
                        "        b += 1;\n" +
                        "        for (int c = 0; c < 20; c += 1) {\n" +
                        "            b += 2;\n" +
                        "        }\n" +
                        "        for (int c = 0; c < 20; c += 1) {\n" +
                        "            b += 2;\n" +
                        "        }\n" +
                        "        d = b;\n" +
                        "        return 0;\n" +
                        "    }\n" +
                        "}");
        processText(new double[]{2d}
                , new String[]{"d"}
                , "int d = 0;\n" +
                        "\n" +
                        "func int main(double i, int b) {\n" +
                        "    i = 0;\n" +
                        "    do {\n" +
                        "        i += 1;\n" +
                        "        int c = i;\n" +
                        "    } while(i < 2);\n" +
                        "    d = i;\n" +
                        "    return i;\n" +
                        "}");
        processText(new double[]{1d, 0d}
                , new String[]{"a", "c"}
                , "double a;\n" +
                        "double c;\n" +
                        "\n" +
                        "func int main(double b) {\n" +
                        "    HashSet<int> tmp;\n" +
                        "    b = 10;\n" +
                        "    b = tmp::add(b);\n" +
                        "    b = tmp::add(3.1);\n" +
                        "    b = tmp::add(3.5);\n" +
                        "    a = getValue(tmp::contains(3));\n" +
                        "    c = getValue(tmp::contains(3.1));\n" +
                        "    return 0;\n" +
                        "}\n" +
                        "\n" +
                        "func int getValue(int val) {\n" +
                        "    return val;\n" +
                        "}");
        processText(new double[]{51d}
                , new String[]{"d"}
                , "double d = 0;\n" +
                        "\n" +
                        "func int main(double i, int b) {\n" +
                        "    d = mul(10.3, 5);\n" +
                        "    return 2.32;\n" +
                        "}\n" +
                        "\n" +
                        "func int mul(double i, int b) {\n" +
                        "    return i * b;\n" +
                        "}");
        processText(new double[]{255d}
                , new String[]{"d"}
                , "double d = 0;\n" +
                        "\n" +
                        "func int main(double i, int b) {\n" +
                        "    d = mul(10.3, 5);\n" +
                        "    return 2.32;\n" +
                        "}\n" +
                        "\n" +
                        "func int mul(double i, int b) {\n" +
                        "    int c = i * b;\n" +
                        "    if (c == 51) {\n" +
                        "        c = mul(c, b);\n" +
                        "    }\n" +
                        "    return c;\n" +
                        "}");
        processText(new double[]{20d}
                , new String[]{"d"}
                , "double d = 0;\n" +
                        "\n" +
                        "func int main(double i, int b) {\n" +
                        "    i = 0;\n" +
                        "    while (i < 2) {\n" +
                        "        i += 1;\n" +
                        "        double c = i;\n" +
                        "        while (c < 10) {\n" +
                        "            c += 1;\n" +
                        "        }\n" +
                        "        d += c;\n" +
                        "    }\n" +
                        "    return 2.32;\n" +
                        "}");
        processText(new double[]{720d}
                , new String[]{"d"}
                , "int d = 0;\n" +
                        "\n" +
                        " func int main(double i) {\n" +
                        "     d = factorial(6);\n" +
                        "     return 0;\n" +
                        " }\n" +
                        "\n" +
                        " func int factorial(int n) {\n" +
                        "     int result = 1;\n" +
                        "     if (n == 1 || n == 0) {\n" +
                        "         return result;\n" +
                        "     }\n" +
                        "     return n * factorial(n - 1);\n" +
                        " }");
        processText(new double[]{3d}
                , new String[]{"a"}
                , "double a;\n" +
                        "\n" +
                        "func int main(double b) {\n" +
                        "    List<int> tmp;\n" +
                        "    b = tmp::add(1);\n" +
                        "    b = tmp::add(3.1);\n" +
                        "    a = getValue(tmp::get(1));\n" +
                        "    return 0;\n" +
                        "}\n" +
                        "\n" +
                        "func int getValue(int val) {\n" +
                        "    return val;\n" +
                        "}");
        processText(new double[]{720d}
                , new String[]{"d"}
                , "  func int getInt(int n) {\n" +
                        "     return n;\n" +
                        "  }\n" +
                        "\n" +
                        " func int main(double i) {\n" +
                        "     d = factorial(getInt(5) + 1);\n" +
                        "     return 0;\n" +
                        " }\n" +
                        "\n" +
                        " func int factorial(int n) {\n" +
                        "     int result = 1;\n" +
                        "     if (n == 1 || n == 0) {\n" +
                        "         return result;\n" +
                        "     }\n" +
                        "     return n * factorial(n - 1);\n" +
                        " }\n" +
                        "\n" +
                        " int d = 0;");
        exceptionTest("Function 'main' did not return a value"
                , "int d;\n" +
                        "func int main() {}");
        exceptionTest("Variable 'd' has not been initialized"
                , "int d;\n" +
                        "func int main() { return d;}");
        exceptionTest("Variable 'd' has not been initialized"
                , "int d;\n" +
                        "func int main() {\n" +
                        "    d += 1;\n" +
                        "    return 0;\n" +
                        "}");
        exceptionTest("Variable 'c' has not been declared"
                , "int d;\n" +
                        "func int main() {\n" +
                        "    d += c;\n" +
                        "    return 0;\n" +
                        "}");
        exceptionTest("Variable 'c' has not been declared"
                , "int d;\n" +
                        "func int main(int i) {\n" +
                        "    for (int c = 0; c < 10; c += 1) {\n" +
                        "        i = 0;\n" +
                        "    }\n" +
                        "    d = c;\n" +
                        "    return 0;\n" +
                        "}");
        exceptionTest("Variable 'i' has not been initialized"
                , "int d;\n" +
                        "func int main(int i) {\n" +
                        "    return i;\n" +
                        "}");
        exceptionTest("Variable 'i' has not been initialized"
                , "int d;\n" +
                        "func int main(int i) {\n" +
                        "    return test(i);\n" +
                        "}\n" +
                        "\n" +
                        "func int test(int i) {\n" +
                        "    i = 0;\n" +
                        "    return i;\n" +
                        "}");

    }

    private void processText(double[] values, String[] names, String... code) {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        PolishGenerator polishGenerator = new PolishGenerator();
        PolishExecutor polishExecutor = new PolishExecutor();
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(FILE_NAME);
            for (String line : code) {
                writer.println(line);
            }
            writer.close();
            lexer.analyze(FILE_NAME);
            parser.parse(lexer.getTokens());
            polishGenerator.generate(parser.getTree());
            polishExecutor.execute(polishGenerator.getPolish(), polishGenerator.getTableOfNames());
            HashMap<String, Variable> table = polishGenerator.getTableOfNames();
            for (int i = 0; i < names.length; i++) {
                Variable variable = table.get(names[i]);
                if (variable.getValueType().equals(LexerTypes.INT_TP))
                    assertTrue(((int) variable.getValue()) == values[i]);
                else
                    assertTrue(((double) variable.getValue()) == values[i]);
            }
        } catch (ParseException | FileNotFoundException | TokenizeException | CompileException e) {
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
        PolishGenerator polishGenerator = new PolishGenerator();
        PolishExecutor polishExecutor = new PolishExecutor();
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(FILE_NAME);
            for (String line : code) {
                writer.println(line);
            }
            writer.close();
            lexer.analyze(FILE_NAME);
            parser.parse(lexer.getTokens());
            polishGenerator.generate(parser.getTree());
            HashMap<String, Variable> table = polishGenerator.getTableOfNames();
            CompileException thrown = assertThrows(CompileException.class,
                    () -> {
                        polishExecutor.execute(polishGenerator.getPolish(), table);
                    });
            assertTrue(thrown.getMessage().contains(exceptionMsg));
        } catch (ParseException | FileNotFoundException | TokenizeException e) {
            e.printStackTrace();
            assertTrue(false);
        } finally {
            writer.close();
            new File(FILE_NAME).delete();
        }
    }
}