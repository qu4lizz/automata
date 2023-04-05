package qu4lizz.automata.loader;

import java.util.ArrayList;
import qu4lizz.automata.exception.*;
import qu4lizz.automata.regex.RegExp;

/**
 * Lexer for specification of language representations (DFA, NFA and RegEx).
 * @author Miroslav Bratic &lt;<a href="miroslavbratic00@gmail.com">miroslavbratic00@gmail.com</a>&gt;
 */
public class Lexer {
    private String source;
    private int sourcePosition;
    private ArrayList<Token> tokens = new ArrayList<>();

    public Lexer() {}
    public Lexer(String input) {
        source = input;
        createTokens();
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    private Token next() throws InvalidReadingException, InvalidCharacterException {
        StringBuilder buffer = new StringBuilder();
        if (sourcePosition == source.length())
            return new Token("EOF");
        else if (sourcePosition >= source.length())
            throw new InvalidReadingException("Reading behind end of file");

        while (sourcePosition < source.length() && Character.isWhitespace(source.charAt(sourcePosition)))
            sourcePosition++;

        if (sourcePosition == source.length())
            return new Token("EOF");

        if (tokens.isEmpty() && Character.isLetter(source.charAt(sourcePosition))) {
            do {
                buffer.append(source.charAt(sourcePosition));
                sourcePosition++;
            } while (sourcePosition < source.length() && Character.isLetter(source.charAt(sourcePosition)));

            return switch (buffer.toString()) {
                case "DFA" -> new Token("DFA");
                case "NFA" -> new Token("NFA");
                case "RegEx" -> new Token("RegEx");
                default -> throw new InvalidReadingException("Unrecognizable representation");
            };
        }
        else if (tokens.get(0).getType().equals("RegEx") && RegExp.validRegExChar(source.charAt(sourcePosition))) {
            do {
                buffer.append(source.charAt(sourcePosition));
                sourcePosition++;
            } while (sourcePosition < source.length() && RegExp.validRegExChar(source.charAt(sourcePosition)));
            return new Token("expression", buffer.toString());
        }
        else if (tokens.size() == 1 && (tokens.get(0).getType().equals("DFA") || tokens.get(0).getType().equals("NFA")) && Character.isLetterOrDigit(source.charAt(sourcePosition))) {
            do {
                buffer.append(source.charAt(sourcePosition));
                sourcePosition++;
            } while (sourcePosition < source.length() && Character.isLetterOrDigit(source.charAt(sourcePosition)));
            return new Token("initialState", buffer.toString());
        }
        else if (Character.isLetterOrDigit(source.charAt(sourcePosition)) &&
                (tokens.get(0).getType().equals("DFA") || tokens.get(0).getType().equals("NFA"))) {
            do {
                buffer.append(source.charAt(sourcePosition));
                sourcePosition++;
            } while (sourcePosition < source.length() && (Character.isLetterOrDigit(source.charAt(sourcePosition)) || source.charAt(sourcePosition) == '-'));
            if (buffer.toString().contains("-"))
                return new Token("transition", buffer.toString());
            else if (buffer.toString().equals("true") || buffer.toString().equals("false"))
                return new Token("finality", buffer.toString());
            else
                return  new Token("state", buffer.toString());
        }
        throw new InvalidCharacterException("Unknown input" + source.charAt(sourcePosition));
    }
    private void createTokens() {
        Token curr;
        do {
            try {
                curr = next();
            } catch (InvalidReadingException | InvalidCharacterException e) {
                throw new RuntimeException(e);
            }
            tokens.add(curr);
        } while (!curr.getType().equals("EOF"));
    }
}
