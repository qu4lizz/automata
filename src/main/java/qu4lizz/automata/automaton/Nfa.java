package qu4lizz.automata.automaton;

import qu4lizz.automata.exception.StateExistsException;
import qu4lizz.automata.exception.WordLengthException;
import qu4lizz.automata.state.DfaState;
import qu4lizz.automata.state.NfaState;
import qu4lizz.automata.util.Utils;

import java.util.*;

/**
 * Nondeterministic finite automaton.
 * @author Miroslav Bratic &lt;<a href="miroslavbratic00@gmail.com">miroslavbratic00@gmail.com</a>&gt;
 */
public class Nfa extends Fa {
    private Vector<NfaState> states = new Vector<>();

    public Nfa() {}

    /**
     * Constructor that creates NFA with a specified number of states.
     * @param size number of states that will automaton have
     */
    public Nfa(int size) {
        this.initialStateId = "q0";
        for (int i = 0; i < size; i++) {
            try {
                this.addState("q" + i, false);
            } catch (StateExistsException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Constructor that crates NFA that accepts single character.
     * @param c transition symbol
     */
    public Nfa(char c) {
        this.initialStateId = "q0";
        try {
            this.addState("q0", false);
            this.addState("q1", true);
        } catch (StateExistsException e) {
            throw new RuntimeException(e);
        }

        this.addTransitionSafe("q0", c, "q1");
    }
    /**
     * Adds state to vector with setting its name and acceptance.
     * @param state id of the state
     * @param isFinal acceptance of the state
     */
    public void addState(String state, Boolean isFinal) throws StateExistsException {
        NfaState newState = new NfaState(state, isFinal);
        if (states.contains(newState)) {
            throw new StateExistsException("State " + state + " already exists.");
        }
        states.add(newState);
    }

    /**
     * Adds transition to destination state.
     * @param fromState source state
     * @param symbol transition symbol
     * @param toState destination state
     */
    public void addDestinationState(NfaState fromState, Character symbol, NfaState toState) {
        if (fromState.getTransitions().containsKey(symbol))
            fromState.getTransitions().get(symbol).add(toState);
        else {
            Set<NfaState> tmp = new HashSet<>();
            tmp.add(toState);
            fromState.getTransitions().put(symbol, tmp);
        }
    }

    /**
     * Adds transition to destination state.
     * @param fromStateStr id of source state
     * @param symbol transition symbol
     * @param toStateStr id of destination state
     * @throws StateExistsException if ids of state don't exist
     */
    public void addDestinationState(String fromStateStr, Character symbol, String toStateStr) throws StateExistsException {
        NfaState fromState = findStateByString(fromStateStr);
        NfaState toState = findStateByString(toStateStr);

        if (fromState.getTransitions().containsKey(symbol))
            fromState.getTransitions().get(symbol).add(toState);
        else {
            Set<NfaState> tmp = new HashSet<>();
            tmp.add(toState);
            fromState.getTransitions().put(symbol, tmp);
        }
    }

    /**
     * Adds state.
     * @param state state
     */
    public void addState(NfaState state) {
        states.add(state);
    }

    /**
     * Removes state
     * @param state id of state
     */
    public void removeState(String state) throws StateExistsException {
        NfaState curr = findStateByString(state);
        if (curr.getId().equals(initialStateId))
            initialStateId = null;
        states.remove(curr);
    }

    /**
     * Adds transition from one state to list of states.
     * @param currentState id source state
     * @param symbol transition symbol
     * @param nextStatesStr id of destination states
     * @throws StateExistsException if ids of state don't exist
     */
    public void addTransition(String currentState, Character symbol, List<String> nextStatesStr) throws StateExistsException {
        NfaState curr = findStateByString(currentState);
        Set<NfaState> nextStates = new HashSet<>();
        for (var nextStr : nextStatesStr) {
            NfaState next = findStateByString(nextStr);
            nextStates.add(next);
        }
        curr.addTransition(symbol, nextStates);

    }

    /**
     * Adds transition from one state to list of states.
     * @param currentState id of source state
     * @param symbol transition symbol
     * @param nextStatesStr id of destination state
     * @throws StateExistsException if ids of state don't exist
     */
    public void addTransition(String currentState, Character symbol, String nextStatesStr) throws StateExistsException {
        NfaState curr = findStateByString(currentState);
        Set<NfaState> nextStates = new HashSet<>();
        NfaState next = findStateByString(nextStatesStr);
        nextStates.add(next);
        if (curr.getTransitions().get(symbol) == null)
            curr.addTransition(symbol, nextStates);
        else
            curr.getTransitions().get(symbol).add(next);

    }

    void addTransitionSafe(String currentState, Character symbol, String nextStatesStr) {
        NfaState curr;
        NfaState next;
        try {
            curr = findStateByString(currentState);
            next = findStateByString(nextStatesStr);

        } catch (StateExistsException e) {
            throw new RuntimeException(e);
        }
        Set<NfaState> nextStates = new HashSet<>();
        nextStates.add(next);
        if (curr.getTransitions().get(symbol) == null)
            curr.addTransition(symbol, nextStates);
        else
            curr.getTransitions().get(symbol).add(next);

    }

    public Vector<NfaState> getStates() {
        return states;
    }

    /**
     * Adds epsilon transition from source state to destination state.
     * @param currentState id of source state
     * @param nextState id of destination state
     * @throws StateExistsException
     */
    public void addEpsilon(String currentState, List<String> nextState) throws StateExistsException {
        this.addTransition(currentState, NfaState.EPSILON, nextState);
    }

    /**
     * Runs automata on word and returns true if automata accept given word.
     * Complexity: O(n*m) - where n is number of characters in input and m number of states
     * @param input string on which will automata be executed
     * @return true if automata accept word
     * @throws StateExistsException if automaton goes to non-existing state for unsupported symbol
     */
    @Override
    public boolean run(String input) throws StateExistsException {
        NfaState startState = findStateByString(initialStateId);
        Set<NfaState> resStates = new HashSet<>();
        resStates.add(startState);
        for (var symbol : input.toCharArray()) {
            resStates = NfaState.getTransitionsSet(symbol, resStates);
            if (states == null)
                return false;
        }
        for (var state : resStates) {
            if (state.isFinal())
                return true;
        }
        return false;
    }

    /**
     * Transforms NFA to DFA.
     * Complexity: O(n^2 * m) where n is number of states m cardinalities of alphabet
     * @return DFA representation of NFA
     * @throws StateExistsException
     */
    public Dfa toDfa() throws StateExistsException {
        Dfa dfa = new Dfa();
        Set<NfaState> fromStates = new HashSet<>();
        List<Set<NfaState>> allStates = new LinkedList<>();

        List<Character> alphabet = getAlphabet();
        NfaState startState = findStateByString(initialStateId);
        fromStates.add(startState);

        fromStates = NfaState.closure(fromStates);
        dfa.initialStateId = stateName(fromStates);
        dfa.addState(dfa.initialStateId, stateFinal(fromStates));
        allStates.add(fromStates);

        // creating name from a set of states
        for (int i = 0; i < allStates.size(); i++) {
            var states = allStates.get(i);
            Set<NfaState> transitionStates;
            String fromStateName = stateName(states);
            for (var symbol : alphabet) {
                transitionStates = new HashSet<>();
                // transitions from all states for given symbol
                for (var state : states) {
                    if (state.getTransitions().get(symbol) != null)
                        transitionStates.addAll(state.getTransitions().get(symbol));

                }
                // if it doesn't transition, it is a dead state
                if (transitionStates.isEmpty()) {
                    if (!dfa.getStates().contains(new DfaState("empty"))) {
                        dfa.addState("empty", false);
                        for (var w : alphabet)
                            dfa.addTransition("empty", w, "empty");
                    }
                    // transitions to dead state
                    dfa.addTransition(fromStateName, symbol, "empty");
                } else {
                    transitionStates = NfaState.closure(transitionStates);
                    String transitionStateName = stateName(transitionStates);
                    if (!dfa.getStates().contains(new DfaState(transitionStateName))) {
                        dfa.addState(transitionStateName, stateFinal(transitionStates));
                        allStates.add(transitionStates);
                    }
                    dfa.addTransition(fromStateName, symbol, transitionStateName);
                }
            }
        }
        return dfa;
    }

    /**
     * Forms a state name from multiple states.
     * @param states set of states from which new state name will be made
     * @return new state name
     */
    String stateName(Set<NfaState> states) {
        StringBuilder name = new StringBuilder();
        for (var state : states) {
            name.append(state.getId());
        }
        return name.toString();
    }

    private boolean stateFinal(Set<NfaState> passedStates) {
        for (var state : passedStates) {
            if (state.isFinal())
                return true;
        }
        return false;
    }

    private List<Character> getAlphabet() {
        List<Character> alphabet = new LinkedList<>();
        for (var state : states) {
            for (var key : state.getTransitions().keySet()) {
                if (key == NfaState.EPSILON)
                    break;
                if (!alphabet.contains(key))
                    alphabet.add(key);
            }
        }
        return alphabet;
    }

    NfaState findStateByString(String state) throws StateExistsException {
        for (var s : states) {
            if (s.getId().equals(state))
                return s;
        }
        throw new StateExistsException("State " + state + " doesn't exist");
    }

    /**
     * Kleene operator for generating automaton from RegEx.
     * Complexity: O(n * m * k) - n is number of states, m is number of transitions and k
     *  is the number of destination states.
     * @param n automaton on which Kleene star will be applied
     * @return NFA with applied Kleene star
     */
    public static Nfa kleeneForRegex(Nfa n)  {
        Nfa result = new Nfa(n.states.size() + 2);
        // new transition for q0
        process(n, result);

        // add empty transition from final n state to new final state.
        result.addTransitionSafe("q" + n.states.size(), NfaState.EPSILON,
                "q" + (n.states.size() + 1));

        // loop back from last state of n to initial state of n
        result.addTransitionSafe("q" + n.states.size(), NfaState.EPSILON, "q1");

        // add empty transition from new initial state to new final state.
        result.addTransitionSafe("q0", NfaState.EPSILON, "q" + (n.states.size() + 1));

        NfaState fin;
        try {
            fin = result.findStateByString("q" + (n.states.size() + 1));
        } catch (StateExistsException e) {
            throw new RuntimeException(e);
        }
        fin.setFinal(true);

        return result;
    }

    /**
     * Concatenation for generating automaton from RegEx. <br>
     * Complexity: O(n * m * k) - n is number of states, m is number of transitions and k
     * is number of destination states.
     * @param n automaton on which Kleene star will be applied
     * @return NFA with applied concatenation
     */
    public static Nfa concatForRegex(Nfa n, Nfa m) throws StateExistsException {
        Nfa result = new Nfa(n.states.size() + m.states.size());

        for (var state : n.states) {
            for (var tr : state.getTransitions().entrySet()) {
                for (var to : tr.getValue())
                    result.addTransitionSafe(state.getId(), tr.getKey(), to.getId());
            }
            if (state.isFinal())
                result.addTransitionSafe(state.getId(), NfaState.EPSILON, "q" + n.states.size());
        }

        for (var state : m.states) {
            for (var tr : state.getTransitions().entrySet()) {
                for (var to : tr.getValue())
                    result.addTransitionSafe(Utils.addNumber(state.getId(), n.states.size()),
                            tr.getKey(), Utils.addNumber(to.getId(), n.states.size()));
            }
        }
        NfaState fin = result.findStateByString("q" + (n.states.size() + m.states.size() - 1));
        fin.setFinal(true);
        return result;
    }

    /**
     * Union for generating automaton from RegEx. <br>
     * Complexity: O(n * m * k) - n is number of states, m is number of transitions and k
     *  is the number of destination states
     * @param n automaton on which Kleene star will be applied
     * @return NFA with applied union
     */
    public static Nfa unionForRegex(Nfa n, Nfa m) throws StateExistsException {
        Nfa result = new Nfa(n.states.size() + m.states.size() + 2);

        process(n, result);

        result.addTransitionSafe("q" + n.states.size(), NfaState.EPSILON,
                "q" + (n.states.size() + m.states.size() + 1));

        result.addTransitionSafe("q0", NfaState.EPSILON, "q" + (n.states.size() + 1));

        for (var state : m.states) {
            for (var tr : state.getTransitions().entrySet()) {
                for (var to : tr.getValue())
                    result.addTransitionSafe(Utils.addNumber(state.getId(), n.states.size() + 1), tr.getKey(),
                            Utils.addNumber(to.getId(), n.states.size() + 1));
            }
        }

        result.addTransitionSafe("q" + (m.states.size() + n.states.size()), NfaState.EPSILON,
                "q" + (n.states.size() + m.states.size() + 1));

        NfaState fin = result.findStateByString("q" + (n.states.size() + m.states.size() + 1));
        fin.setFinal(true);

        return result;
    }

    private static void process(Nfa n, Nfa result) {
        result.addTransitionSafe("q0", NfaState.EPSILON, "q1");

        for (var state : n.states) {
            for (var tr : state.getTransitions().entrySet()) {
                for (var to : tr.getValue())
                    result.addTransitionSafe(Utils.addNumber(state.getId(), 1), tr.getKey(),
                            Utils.addNumber(to.getId(), 1));
            }
        }
    }

    /**
     * Concatenates two automata by adding epsilon transition from final states
     * of first automaton to beginning state of second automaton (no side effects).
     * Final states of first automaton become non-final.
     * Complexity: O(n * m * k) - n is number of states, m is number of transitions and k
     * is number of destination states
     * @param other second operand automaton
     * @return automaton that is concatenation of two automata
     * @throws StateExistsException
     */
    public Nfa concatenation(Nfa other) throws StateExistsException {
        Nfa result = new Nfa();
        Map<NfaState, String> keys = new HashMap<>();
        int i = 0;
        result.initialStateId = "q0";
        for (var state : this.states) {
            result.addState("q" + i, false);
            keys.put(state, "q" + i);
            i++;
        }
        for (var state : other.states) {
            result.addState("q" + i, state.isFinal());
            keys.put(state, "q" + i);
            i++;
        }
        for (var state : this.states) {
            for (var tr : state.getTransitions().entrySet()) {
                for (var to : tr.getValue())
                    result.addTransitionSafe(keys.get(state), tr.getKey(), keys.get(to));
            }
        }
        for (var state : other.states) {
            for (var tr : state.getTransitions().entrySet()) {
                for (var to : tr.getValue())
                    result.addTransitionSafe(keys.get(state), tr.getKey(), keys.get(to));
            }
        }

        NfaState initial2key, initial2res;
        initial2key = other.findStateByString(other.initialStateId);
        initial2res = result.findStateByString(keys.get(initial2key));

        for (var state : this.states) {
            if (state.isFinal()) {
                result.addDestinationState(result.findStateByString(keys.get(state)), NfaState.EPSILON, initial2res);
            }
        }
        return result;
    }

    /**
     * Transforms NFA to DFA and finds intersection of two automata. <br>
     * {@link Dfa#union(Dfa) Union}
     */
    public Dfa union(Nfa other) throws StateExistsException {
        Dfa dfa1, dfa2;
        try {
            dfa1 = this.toDfa();
            dfa2 = other.toDfa();
        } catch (StateExistsException e) {
            throw new RuntimeException(e);
        }
        return dfa1.union(dfa2);
    }

    /**
     * Transforms NFA to DFA and finds intersection of two automata. <br>
     * {@link Dfa#intersection(Dfa) Intersection}
     */
    public Dfa intersection(Nfa other) throws StateExistsException {
        Dfa dfa1, dfa2;
        try {
            dfa1 = this.toDfa();
            dfa2 = other.toDfa();
        } catch (StateExistsException e) {
            throw new RuntimeException(e);
        }
        return dfa1.intersection(dfa2);
    }

    /**
     * Transforms NFA to DFA and finds difference of two automata. <br>
     * {@link Dfa#difference(Dfa) Difference}
     */
    public Dfa difference(Nfa other) throws StateExistsException {
        Dfa dfa1, dfa2;
        try {
            dfa1 = this.toDfa();
            dfa2 = other.toDfa();
        } catch (StateExistsException e) {
            throw new RuntimeException(e);
        }
        return dfa1.difference(dfa2);
    }

    /**
     * Transforms NFA to DFA and finds complement of automaton. <br>
     * {@link Dfa#complement Complement}
     */
    public Dfa complement() {
        Dfa dfa;
        try {
            dfa = this.toDfa();
        } catch (StateExistsException e) {
            throw new RuntimeException(e);
        }
        return dfa.complement();
    }

    /**
     * Transforms NFA to DFA and finds length of the shortest word. <br>
     * {@link Dfa#shortestWord() ShortestWord}
     */
    public int shortestWord() throws StateExistsException, WordLengthException {
        Dfa dfa;
        try {
            dfa = this.toDfa();
        } catch (StateExistsException e) {
            throw new RuntimeException(e);
        }
        return dfa.shortestWord();
    }
    /**
     * Transforms NFA to DFA and finds length of the longest word. <br>
     * {@link Dfa#longestWord() LongestWord}
     */
    public int longestWord() throws StateExistsException, WordLengthException {
        Dfa dfa;
        try {
            dfa = this.toDfa();
        } catch (StateExistsException e) {
            throw new RuntimeException(e);
        }
        return dfa.longestWord();
    }

    /**
     * Checks if two automatons are equivalent. <br>
     * {@link Dfa#equivalent(Object) Equivalent}
     * @param o other automaton
     * @return true if they're equivalent
     * @throws StateExistsException
     */
    public boolean equivalent(Object o) throws StateExistsException {
        if (o == this)
            return true;

        if (!(o instanceof Nfa))
            return false;

        Dfa dfa1 = this.toDfa();
        Dfa dfa2 = ((Nfa) o).toDfa();
        return dfa1.equivalent(dfa2);
    }

}