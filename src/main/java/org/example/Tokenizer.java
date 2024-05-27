package org.example;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private final String program;
    public Tokenizer(String program) {
        this.program = program;
    }

    /**
     * Tokenizes the input string
     * @return a {@code List<Token>} containing the tokens
     * @throws ParseError if invalid token is recognized
     */
    public List<Token> tokenize(){
        List<Token> tokens = new ArrayList<>();
        String s = program;
        int linenum = 1;
        int charnum = 0;

        while(!s.isEmpty()) {
            boolean consumed = false;
            token_search:
            for (TokenType t : TokenType.values()) {
                for (String regex : t.getRegexen()) {
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.find()) {
                        String match = matcher.group(0);
                        String removed = s.substring(0, match.length());

                        int matches = StringUtils.countMatches(removed, "\n");
                        linenum += matches;
                        if(matches > 0) charnum = 0;

                        for(int i = removed.length() - 1; i >= 0; i--) {
                            if(removed.charAt(i) != '\n'){
                                charnum ++;
                            }else{
                                break;
                            }
                        }

                        s = s.substring(match.length());
                        consumed = true;
                        switch (t){
                            case EMPTY -> {
                                break token_search;
                            }
                            case NUMBER -> {
                                tokens.add(new Token(t, match, Double.parseDouble(match), linenum, charnum));
                                break token_search;
                            }
                            case STRING -> {
                                String val = match.substring(1,match.length()-1);
                                val = val.replaceAll("\\\\\\\"", "\"");
                                val = val.replaceAll("\\\\n", "\n");
                                tokens.add(new Token(t, match, val, linenum, charnum));
                            }
                            default -> {
                                tokens.add(new Token(t, match, null, linenum, charnum));
                                break token_search;
                            }
                        }
                    }
                }
            }

            if(!consumed){
                String lexeme = s.substring(0, 1);
                App.parseError(new Token(TokenType.EMPTY, lexeme,null, linenum, charnum ), "Invalid token: '" +lexeme+"'");
            }
        }

        tokens.add(new Token(TokenType.EOF, "", null, linenum, charnum));
        return tokens;
    }
}
