package gorder.lexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Lexer {
    private ArrayList<Token> tokens;
    private HashMap<LexerTypes, String> regExp;

    private void init() {
        tokens = new ArrayList<>();
        regExp = LexerTypes.initRegExp();
    }

    public void analyze(String path) throws TokenizeException {
        init();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            long startIndex = 1;
            long row = 0;
            StringBuilder lexeme = new StringBuilder();
            tokens.add(null);
            for (String buf = br.readLine(); buf != null; buf = br.readLine()) {
                row++;
                long column = 0;
                long currentRow = row;
                long currentColumn = column;
                for (int i = 0; i < buf.length(); i++) {
                    lexeme.append(buf.charAt(i));
                    Token token = processLexeme(lexeme.toString(), startIndex, currentRow, currentColumn);
                    if (token != null) {
                        tokens.set(tokens.size() - 1, token);
                    } else {
                        if (tokens.get(tokens.size() - 1) == null)
                            throw new TokenizeException("Unknown lexeme in line: " + row + " column: " + column);
                        lexeme.delete(0, lexeme.length() - 1);
                        if (tokens.get(tokens.size() - 1) != null) {
                            if (tokens.get(tokens.size() - 1).getType().equals(LexerTypes.SPACE_SP)
                                    || tokens.get(tokens.size() - 1).getType().equals(LexerTypes.TAB_SP)) {
                                tokens.remove(tokens.size() - 1);
                            }
                        }
                        currentRow = row;
                        currentColumn = column + 1;
                        tokens.add(processLexeme(lexeme.toString(), startIndex, currentRow, currentColumn));
                    }
                    column++;
                    startIndex++;
                }
                lexeme.delete(0, lexeme.length() - 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    private Token processLexeme(String lexeme, long stIndex, long row, long column) {
        for (LexerTypes type : LexerTypes.values()) {
            Pattern pattern = Pattern.compile(regExp.get(type));
            if (pattern.matcher(lexeme).matches()) {
                return new Token(stIndex, stIndex + lexeme.length(), type, lexeme, row, column);
            }
        }
        return null;
    }

    public void printTokens() {
        for (Token token : tokens) {
            System.out.println(
                            token.getType()
                            + "\t" + token.getValue()
                            + "\t" + token.getIndexStart()
                            + "\t" + token.getIndexEnding()
                            + "\t" + token.getRow()
                            + "\t" + token.getCol());
        }
    }
}
