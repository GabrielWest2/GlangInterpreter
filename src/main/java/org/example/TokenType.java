package org.example;

/**
 * Represents a token
 * @param
 */
public enum TokenType {
    EMPTY("^\\s+", "^\\/\\/.*", "^/\\*[\\s\\S]*?\\*\\/"),
    //PLUS_PLUS("^[+][+]"),
    // Single-character tokens.
    LEFT_PAREN("^[\\(]"),
    RIGHT_PAREN("^[\\)]"),
    LEFT_BRACE("^[\\{]"),
    RIGHT_BRACE("^[\\}]"),
    COMMA("^[,]"),
    DOT("^[.]"),
    MINUS("^[\\-]"),

    PLUS_EQUALS("^[+][=]"),


    PLUS("^[+]"),
    SEMICOLON("^[;]"),
    SLASH("^[/]"),
    STAR("^[*]"),
    // One or two character tokens.
    BANG_EQUAL("^!="),
    BANG("^!"),
    EQUAL_EQUAL("^=="),
    EQUAL("^="),
    GREATER_EQUAL("^>="),
    GREATER("^>"),
    LESS_EQUAL("^<="),
    LESS("^<"),

    // Keywords.
    AND("^and(?![_a-zA-Z0-9])"),
    EXTENDS("^extends(?![_a-zA-Z0-9])"),
    CLASS("^class(?![_a-zA-Z0-9])"),
    ELSE("^else(?![_a-zA-Z0-9])"),
    FALSE("^false(?![_a-zA-Z0-9])"),
    DEF("^def(?![_a-zA-Z0-9])"),
    FOR("^for(?![_a-zA-Z0-9])"),
    IF("^if(?![_a-zA-Z0-9])"),
    NULL("^null(?![_a-zA-Z0-9])"),
    OR("^or(?![_a-zA-Z0-9])"),
    PRINT("^print(?![_a-zA-Z0-9])"),
    RETURN("^return(?=[\\s;])"),
    SUPER("^super(?![_a-zA-Z0-9])"),
    THIS("^this(?![_a-zA-Z0-9])"),
    TRUE("^true(?![_a-zA-Z0-9])"),
    VAR("^var(?![_a-zA-Z0-9])"),
    WHILE("^while(?![_a-zA-Z0-9])"),

    // Literals.
    STRING("^\\\"(\\\\.|[^\\\"\\\\])*\\\""),
    NUMBER("^\\d+\\.?\\d*"),
    IDENTIFIER("^[_a-zA-Z0-9]{1,31}"),

    // End of File
    EOF;

    // List of regular expressions representing this token
    final String[] regexen;
    TokenType(String... regexen){
        this.regexen = regexen;
    }

    public String[] getRegexen() {
        return regexen;
    }
}
