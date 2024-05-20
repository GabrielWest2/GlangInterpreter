package org.example;

public class Token {
    private final TokenType tokenType;
    private final String lexeme;
    private final Object literal;
    private final int line;
    private final int horizontal;

    public Token(TokenType tokenType, String lexeme, Object literal, int line, int horizontal) {
        this.tokenType = tokenType;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.horizontal = horizontal;
    }

    @Override
    public String toString(){
        return String.format("[ %s %s %s]", tokenType.toString(), (tokenType == TokenType.EMPTY ? "" : lexeme), (literal == null ? "" : literal.toString() + " "));
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getLexeme() {
        return lexeme;
    }

    public Object getLiteral() {
        return literal;
    }

    public int getLine() {
        return line;
    }

    public int getHorizontal() {
        return horizontal;
    }
}
