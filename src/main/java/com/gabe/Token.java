package com.gabe;

/**
 * A single lexical unit of the program
 * Stores any literal value associated with the
 * token, as well as the line and character
 * number it was on.
 */
public record Token(TokenType tokenType, String lexeme, Object literal,
                    int line, int horizontal) {

    @Override
    public String toString() {
        return String.format("[ %s %s %s]",
                tokenType.toString(),
                (tokenType == TokenType.EMPTY ? "" : lexeme),
                (literal == null ? "" : literal.toString() + " ")
        );
    }
}
