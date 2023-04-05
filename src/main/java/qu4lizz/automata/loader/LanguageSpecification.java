package qu4lizz.automata.loader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import qu4lizz.automata.exception.*;
import qu4lizz.automata.automaton.*;
import qu4lizz.automata.regex.*;
import qu4lizz.automata.state.NfaState;

/**
 * Language specification of DFA, NFA or RegEx loaded from file.
 * @author Miroslav Bratic &lt;<a href="miroslavbratic00@gmail.com">miroslavbratic00@gmail.com</a>&gt;
 */
public class LanguageSpecification {
    /**
     * Based on first token loaded from file, determines the type of language
     * representation and creates a given representation.
     * @param fileName path of a file from which language specification will be loaded.
     * @return created language specification
     * @param <T> generic return type based on language representation specified in file (DFA, NFA or RegEx)
     */
    public static <T> T loadSpecification(String fileName) {
        Lexer lexer;
        try {
            String input = Files.readString(Path.of(fileName));
            lexer = new Lexer(input);
            Token representation = lexer.getTokens().get(0);
            switch (representation.getType()) {
                case "DFA" -> {
                    try {
                        return (T) createDfa(lexer);
                    } catch (StateExistsException | InvalidCharacterException | DfaTransitionsException e) {
                        e.printStackTrace();
                    }
                }
                case "NFA" -> {
                    try {
                        return (T) createNfa(lexer);
                    } catch (InvalidCharacterException | StateExistsException e) {
                        e.printStackTrace();
                    }
                }
                case "RegEx" -> {
                    try {
                        return (T) createRegEx(lexer);
                    } catch (InvalidRegexException e) {
                        e.printStackTrace();
                    }
                }
                default -> {
                    return null;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Creates DFA based on tokens created by lexer.
     * Complexity: O(n * m) where n is number of states and m is number of transitions
     * @param lexer lexer which created tokens from file
     * @return Dfa created from lexer
     * @throws StateExistsException if the state already exists
     * @throws InvalidCharacterException if invalid character is found
     * @throws DfaTransitionsException if the number of transitions is not equal to number of characters in alphabet
     */
    private static Dfa createDfa(Lexer lexer) throws StateExistsException, InvalidCharacterException, DfaTransitionsException {
        Dfa res = new Dfa();

        HashMap<String, ArrayList<String>> stateTransitions = process(lexer, res);

        for (var trs : stateTransitions.entrySet()) {
            for (var tr : trs.getValue()) {
                String[] symbolToState = tr.split("-");
                if (symbolToState[0].length() > 1)
                    throw new InvalidCharacterException(symbolToState[0] + " can't be transition symbol");
                res.addTransition(trs.getKey(), symbolToState[0].charAt(0), symbolToState[1]);
            }
        }
        if (res.validAutomata())
            return res;
        else throw new DfaTransitionsException("Not every state has transition for each symbol");
    }

    /**
     * Creates NFA based on tokens created by lexer.
     * Complexity: O(n * m) where n is number of states and m is number of transitions
     * @param lexer lexer which created tokens from file
     * @return Nfa created from lexer
     * @throws InvalidCharacterException, StateExistsException
     */
    private static Nfa createNfa(Lexer lexer) throws InvalidCharacterException, StateExistsException {
        Nfa res = new Nfa();

        HashMap<String, ArrayList<String>> stateTransitions = process(lexer, res);

        for (var trs : stateTransitions.entrySet()) {
            for (var tr : trs.getValue()) {
                String[] symbolToState = tr.split("-");
                if (symbolToState[0].length() > 1)
                    throw new InvalidCharacterException(symbolToState[0] + " can't be transition symbol");
                for (int i = 1; i < symbolToState.length; i++) {
                    if (symbolToState[0].charAt(0) == 'e')
                        res.addDestinationState(trs.getKey(), NfaState.EPSILON, symbolToState[i]);
                    else
                        res.addDestinationState(trs.getKey(), symbolToState[0].charAt(0), symbolToState[i]);
                }
            }
        }

        return res;
    }

    private static HashMap<String, ArrayList<String>> process(Lexer lexer, Fa res) throws StateExistsException, InvalidCharacterException {
        res.setInitialState(lexer.getTokens().get(1).getValue());
        HashMap<String, ArrayList<String>> stateTransitions = new HashMap<>();
        ArrayList<String> transitions = new ArrayList<>();

        String currState = null;
        for (int i = 2; i < lexer.getTokens().size(); i++) {
            var tok = lexer.getTokens().get(i);
            if (tok.getType().equals("state") && lexer.getTokens().get(i+1).getType().equals("finality")) {
                if (currState != null) {
                    stateTransitions.put(currState, transitions);
                    transitions = new ArrayList<>();
                }
                res.addState(tok.getValue(), lexer.getTokens().get(i + 1).getValue().equals("true"));
                currState = lexer.getTokens().get(i).getValue();
                i++;
            }
            else if (tok.getType().equals("transition"))
                transitions.add(tok.getValue());
            else if (!tok.getType().equals("EOF") && !lexer.getTokens().get(i+1).getType().equals("finality"))
                throw new InvalidCharacterException("State finality not specified");
        }
        stateTransitions.put(currState, transitions);
        return stateTransitions;
    }

    /**
     * Creates RegEx based on tokens created by lexer.
     * @param lexer lexer which created tokens from file
     * @return regular expression created from lexer
     * @throws InvalidRegexException if the input is invalid
     */
    private static RegExp createRegEx(Lexer lexer) throws InvalidRegexException {
        if (lexer.getTokens().get(1).getType().equals("expression"))
            return new RegExp(lexer.getTokens().get(1).getValue());
        throw new InvalidRegexException("Invalid input");
    }
}
