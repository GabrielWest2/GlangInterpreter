package org.example;

/**
 * Represents a token
 */
public enum TokenType {
    EMPTY("^\\s+", "^\\/\\/.*", "^/\\*[\\s\\S]*?\\*\\/"),
    // Single-character tokens.
    LEFT_PAREN("^[\\(]"),
    RIGHT_PAREN("^[\\)]"),
    LEFT_BRACE("^[\\{]"),
    LEFT_BRACKET("^[\\[]"),
    RIGHT_BRACE("^[\\}]"),
    RIGHT_BRACKET("^[\\]]"),
    COMMA("^[,]"),
    DOT("^[.]"),
    QUESTION("^[\\?]"),

    MINUS_EQUALS("^[-][=]"),
    PLUS_EQUALS("^[+][=]"),
    PLUS_PLUS("^[+][+]"),
    MINUS_MINUS("^[-][-]"),

    // Binary Operators
    MINUS("^[\\-]"),
    PLUS("^[+]"),
    SEMICOLON("^[;]"),
    COLON("^[:]"),
    SLASH("^[/]"),
    STAR("^[*]"),
    PERCENT("^[%]"),

    // Logical operators
    BANG_EQUAL("^!="),
    BANG("^!"),
    EQUAL_EQUAL("^=="),
    EQUAL("^="),
    GREATER_EQUAL("^>="),
    LESS_EQUAL("^<="),
    GREATER("^>"),
    LESS("^<"),

    // Keywords.
    AND("^and(?![_a-zA-Z0-9])"),
    EXTENDS("^extends(?![_a-zA-Z0-9])"),
    CLASS("^class(?![_a-zA-Z0-9])"),
    ELSE("^else(?![_a-zA-Z0-9])"),
    FALSE("^false(?![_a-zA-Z0-9])"),
    DEF("^def(?![_a-zA-Z0-9])"),
    FOREACH("^foreach(?![_a-zA-Z0-9])"),
    FOR("^for(?![_a-zA-Z0-9])"),
    IF("^if(?![_a-zA-Z0-9])"),
    NULL("^null(?![_a-zA-Z0-9])"),
    OR("^or(?![_a-zA-Z0-9])"),
    XOR("^xor(?![_a-zA-Z0-9])"),
    PRINT("^print(?![_a-zA-Z0-9])"),
    RETURN("^return(?=[\\s;])"),
    SUPER("^super(?![_a-zA-Z0-9])"),
    THIS("^this(?![_a-zA-Z0-9])"),
    TRUE("^true(?![_a-zA-Z0-9])"),
    VAR("^var(?![_a-zA-Z0-9])"),
    WHILE("^while(?![_a-zA-Z0-9])"),
    BREAK("^break(?![_a-zA-Z0-9])"),
    SWITCH("^switch(?![_a-zA-Z0-9])"),
    CASE("^case(?![_a-zA-Z0-9])"),
    DEFAULT("^default(?![_a-zA-Z0-9])"),


    // Literals.
    STRING("^\\\"(\\\\.|[^\\\"\\\\])*\\\""),
    NUMBER("^\\d+\\.?\\d*"),
    IDENTIFIER("^[_a-zA-Z\\p{So}_]{1,31}[_a-zA-Z0-9\\p{So}_]{0,31}"),

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
