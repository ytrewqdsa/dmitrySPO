package gorder.lexer;

public class TokenizeException extends Exception {
    public TokenizeException(String errorMessage) {
        super(errorMessage);
    }

    public TokenizeException() {
        super();
    }
}
