package qu4lizz.automata.automaton;

import qu4lizz.automata.exception.*;
import qu4lizz.automata.state.DfaState;
import qu4lizz.automata.state.NfaState;
import qu4lizz.automata.util.*;

import java.util.*;

/**
 * Deterministic finite automata.
 * @author Miroslav Bratic &lt;<a href="miroslavbratic00@gmail.com">miroslavbratic00@gmail.com</a>&gt;
 */
public class Dfa extends Fa {
    private Vector<DfaState> states = new Vector<>();

    public Vector<DfaState> getStates() {
        return states;
    }

    /**
     * Adds state to vector with setting its name and acceptance.
     * @param state id of the state
     * @param isFinal acceptance of the state
     */
    public void addState(String state, Boolean isFinal) throws StateExistsException {
        DfaState newState = new DfaState(state, isFinal);
        if (states.contains(newState)) {
            throw new StateExistsException("State " + state + " already exists.");
        }
        states.add(newState);
    }

    /**
     * Adds state to vector.
     * @param state existing state
     */
    public void addState(DfaState state) {
        states.add(state);
    }

    /**
     * Adds transition from source state to destination state by character that leads to it.
     * @param currentState id of source state
     * @param symbol transition character
     * @param nextState id of destination state
     */
    public void addTransition(String currentState, Character symbol, String nextState) throws StateExistsException {
        DfaState curr = findStateByString(currentState);
        DfaState next = findStateByString(nextState);
        curr.addTransition(symbol, next);
    }
    /**
     * Adds transition from source state to destination state by character that leads to it.
     * @param currentState object of source state
     * @param symbol transition character
     * @param nextState object of destination state
     */
    public void addTransition(DfaState currentState, Character symbol, DfaState nextState) {
        currentState.addTransition(symbol, nextState);
    }
    /**
     * Runs automata on word and returns true if automata accept given word.
     * Complexity: O(n) - linear in characters of input
     * @param input string on which will automata be executed
     * @return true if automata accept word
     * @throws StateExistsException if automaton goes to non-existing state for unsupported symbol
     */
    @Override
    public boolean run(String input) throws StateExistsException, WrongAlphabetException {
        DfaState currentState = findStateByString(initialStateId);
        for (var symbol : input.toCharArray()) {
            currentState = currentState.getTransition(symbol);
            if (currentState == null)
                throw new WrongAlphabetException("Wrong alphabet in input.");
        }
        return currentState.isFinal();
    }



    /**
     * Finds state in vector of states by its id.
     * Complexity: O(n) - linear of states
     * @param state id of state to find
     * @return state
     * @throws StateExistsException if the state doesn't exist
     */
    DfaState findStateByString(String state) throws StateExistsException {
        for (var s : states) {
            if (s.getId().equals(state))
                return s;
        }
        throw new StateExistsException("State '" + state + "' does not exist.");
    }

    /**
     * Forms a state name from multiple states.
     * Complexity: O(n) - linear of states passed as parameter
     * @param states set of states from which new state name will be made
     * @return new state name
     */
    public String stateName(Set<DfaState> states) {
        StringBuilder name = new StringBuilder();
        for (var state : states) {
            name.append(state.getId());
        }
        return name.toString();
    }

    /**
     * Finds and returns all final states of automaton.
     * Complexity: O(n) - linear of states
     * @return vector of final states
     */
    public Vector<DfaState> finalStates() {
        Vector<DfaState> fStates = new Vector<>();
        for (var state : states) {
            if (state.isFinal())
                fStates.add(state);
        }
        return fStates;
    }
    /**
     * Minimizes dfa by removing its unreachable states and combining those non-distinguishable.
     * Complexity: O(m * n * k) - {@link #nonDistinguishableStates()}  NonDistinguishableStates}
     * @return minimized DFA
     */
    public Dfa minimize() throws StateExistsException{
        Dfa minimized;
        minimized = reachableStates();

        return minimized.nonDistinguishableStates();
    }

    /**
     * Checks if every state has transition for every symbol of the alphabet.
     * Complexity: linear in number of states - O(n)
     * @return true if every state has all transitions
     */
    public boolean validAutomata() {
        var alphabet = getAlphabet();
        for (var state : states) {
            if (state.getTransitions().size() != alphabet.size())
                return false;
        }
        return true;
    }

    /**
     * Removes all unreachable states from copy of DFA (no side effects).
     * Complexity: O(n * m) where n is number of states and m is number of symbols in the alphabet
     * @return DFA with removed unreachable states
     * @throws StateExistsException if the state doesn't exist
     */
    private Dfa reachableStates() throws StateExistsException {
        Dfa minimized = new Dfa();
        Set<DfaState> reachable = new HashSet<>();
        Set<DfaState> newStates = new HashSet<>();

        reachable.add(findStateByString(initialStateId));
        newStates.add(findStateByString(initialStateId));

        do {
            Set<DfaState> tmp = new HashSet<>();
            for(DfaState state : newStates) {
                for(var symbol : getAlphabet())
                    tmp.add(state.getTransition(symbol));
            }
            tmp.removeAll(reachable);
            newStates = tmp;
            reachable.addAll(newStates);
        } while (!newStates.isEmpty());

        for (var state : reachable) {
            minimized.addState(state);
        }
        minimized.initialStateId = initialStateId;
        return minimized;
    }

    /**
     * Merge all states that have identical transitions into one (no side effects).
     * Complexity: O(m * n * k) where m is cardinality of alphabet, n is number of
     * states in partition and k is cardinality of a set
     * @return DFA with removed non-distinguishable states
     * @throws StateExistsException if the state doesn't exist
     */
    private Dfa nonDistinguishableStates() throws StateExistsException {
        Dfa minimized = new Dfa();
        List<Set<DfaState>> partition = splitStates();
        List<Set<DfaState>> sets = splitStates();
        List<Character> alphabet = getAlphabet();

        while (!sets.isEmpty()) { // O(1)
            Set<DfaState> currSet;
            currSet = sets.get(0);
            sets.remove(currSet);
            for (var symbol : alphabet) { // O(m)
                Set<DfaState> forSymbolToCurrSet = new HashSet<>();
                for (var state : states) { // O(n)
                    if (currSet.contains(state.getTransition(symbol))) // O(logn)
                        forSymbolToCurrSet.add(state);
                }
                for (int i = 0; i < partition.size(); i++) { // O(n)
                    Set<DfaState> y = partition.get(i);
                    Set<DfaState> intersection = new HashSet<>(forSymbolToCurrSet);
                    intersection.retainAll(y); // O(k)
                    Set<DfaState> difference = new HashSet<>(y);
                    difference.removeAll(forSymbolToCurrSet);
                    if (!intersection.isEmpty() && !difference.isEmpty()) {
                        partition.remove(y);
                        partition.add(intersection);
                        partition.add(difference);
                        if (sets.contains(y)) {
                            sets.remove(y);
                            sets.add(intersection);
                            sets.add(difference);
                        } else {
                            if (intersection.size() <= difference.size())
                                sets.add(intersection);
                            else
                                sets.add(difference);
                        }
                    }

                }
            }
        }
        // creating new automata
        for (var setOfStates : partition) {
            String newSet = stateName(setOfStates);
            minimized.addState(newSet, hasFinalState(setOfStates));
        }
        for (var setOfStates : partition) {
            for (var otherStates : partition) {
                String from = stateName(setOfStates);
                String to = stateName(otherStates);
                DfaState one = setOfStates.iterator().next();
                DfaState two = otherStates.iterator().next();
                for (var symbol : alphabet) {
                    if (otherStates.contains(one.getTransition(symbol)))
                        minimized.addTransition(from, symbol, to);
                }
            }
            if (setOfStates.contains(findStateByString(initialStateId)))
                minimized.initialStateId = stateName(setOfStates);
        }
        return minimized;
    }

    /**
     * Checks if at least one state in the set is final.
     * @param states set of states
     * @return true if at least one state is final
     */
    private boolean hasFinalState(Set<DfaState> states) {
        for (var state : states) {
            if (state.isFinal())
                return true;
        }
        return false;
    }

    /**
     * Splits final and non-final states into two sets.
     * @return the list of two sets of which one contains final states and the other one contains non-final states
     */
    private List<Set<DfaState>> splitStates() {
        List<Set<DfaState>> allStates = new LinkedList<>();
        Set<DfaState> finalStates = new HashSet<>();
        Set<DfaState> nonFinalStates = new HashSet<>();

        for (var state : states) {
            if (state.isFinal())
                finalStates.add(state);
            else
                nonFinalStates.add(state);
        }
        allStates.add(nonFinalStates);
        allStates.add(finalStates);
        return allStates;
    }

    /**
     * Finds the alphabet of the given automaton.
     * @return list of symbols used in alphabet of automaton
     */
    public List<Character> getAlphabet() {
        List<Character> alphabet = new LinkedList<>();
        for (var state : states) {
            for (var key : state.getTransitions().keySet()) {
                if (!alphabet.contains(key))
                    alphabet.add(key);
            }
        }
        return alphabet;
    }

    /**
     * Creates the union of two automata and returns new automaton (no side effects).
     * Complexity: O(n * m * k) - {@link #createProduct CreateProduct}
     * @param other second operand automaton
     * @return automaton that is union of two automatas
     * @throws StateExistsException if the state doesn't exist
     */
    public Dfa union(Dfa other) throws StateExistsException {
        Callback lambda = (boolean a, boolean b) -> a || b;
        return createProduct(other, lambda);
    }
    /**
     * Creates the intersection of two automata and returns new automaton (no side effects).
     * Complexity: O(n * m * k) - {@link #createProduct CreateProduct}
     * @param other second operand automaton
     * @return automaton that is intersection of two automatas
     * @throws StateExistsException if the state doesn't exist
     */
    public Dfa intersection(Dfa other) throws StateExistsException {
        Callback lambda = (boolean a, boolean b) -> a && b;
        return createProduct(other, lambda);
    }
    /**
     * Creates the difference of two automata and returns new automaton (no side effects).
     * Complexity: O(n * m * k) - {@link #createProduct CreateProduct}
     * @param other second operand automaton
     * @return automaton that is difference of two automatas
     * @throws StateExistsException if the state doesn't exist
     */
    public Dfa difference(Dfa other) throws StateExistsException {
        Callback lambda = (boolean a, boolean b) -> a && !b;
        return createProduct(other, lambda);
    }

    /**
     * Interface which has a method that is used as lambda.
     */
    private interface Callback {
        boolean call(boolean a, boolean b);
    }

    /**
     * Creates product of two automata and chooses final states based on
     * specified lambda function.
     * Complexity: O(n * m * k) where n is number of states of first automaton,
     * m of second and k is the cardinality of the alphabet
     * @param other second automaton
     * @param lambda logical function that specifies final states
     * @return automaton that is product of two automatons
     * @throws StateExistsException if the state doesn't exist
     */
    private Dfa createProduct(Dfa other, Callback lambda) throws StateExistsException {
        Dfa newDfa = new Dfa();
        List<Character> alphabet = getAlphabet();

        newDfa.initialStateId = this.initialStateId + other.initialStateId;
        for (var state1 : states) {
            for (var state2 : other.states) {
                newDfa.addState(state1.getId() + state2.getId(), lambda.call(state1.isFinal(), state2.isFinal()));
            }
        }
        for (var state1 : states) {
            for (var state2 : other.states) {
                for (var symbol : alphabet) {
                    newDfa.addTransition(state1.getId() + state2.getId(), symbol,
                            state1.getTransition(symbol).getId() + state2.getTransition(symbol).getId());
                }

            }
        }
        return newDfa.reachableStates();

    }

    /**
     * Does the logical negation of each state. Final states become non-final
     * and vice versa (no side effects).
     * Complexity: O(n * m) where n is number of states and m is number of transitions of that state
     * @return new automaton that has inverted value of final state
     */
    public Dfa complement() {
        Dfa dfa = new Dfa();
        dfa.initialStateId = this.initialStateId;
        for (var state : states) {
            try {
                dfa.addState(state.getId(), !state.isFinal());
            } catch (StateExistsException e) {
                throw new RuntimeException(e);
            }
        }
        for (var state : states) {
            for (var tr : state.getTransitions().entrySet()) {
                try {
                    dfa.addTransition(state.getId(), tr.getKey(), tr.getValue().getId());
                } catch (StateExistsException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return dfa;
    }

    /**
     * Concatenates two automata by transforming them to NFAs and adding epsilon transition from
     * final states of first automaton to beginning state of second automaton (no side effects).
     * Final states of first automaton become non-final.
     * {@link Nfa#concatenation(Nfa) Concatenation}
     * @param other second operand automaton
     * @return automaton that is concatenation of two automata
     * @throws StateExistsException if the state doesn't exist
     */
    public Dfa concatenation(Dfa other) throws StateExistsException {
        Nfa nfa1 = this.toNfa();
        Nfa nfa2 = other.toNfa();
        Nfa resNfa = nfa1.concatenation(nfa2);
        return resNfa.toDfa();
    }

    /**
     * Transforms DFA to NFA.
     * Complexity: O(n * m) where n is number of states and m is number of transitions of that state
     * @return NFA version of DFA
     */
    private Nfa toNfa() {
        Nfa nfa = new Nfa();
        for (var state : this.states) {
            try {
                nfa.addState(state.getId(), state.isFinal());
            } catch (StateExistsException e) {
                throw new RuntimeException(e);
            }
        }
        for (var state : this.states) {
            for (var tr : state.getTransitions().entrySet()) {
                nfa.addTransitionSafe(state.getId(), tr.getKey(), tr.getValue().getId());
            }
        }
        nfa.initialStateId = this.initialStateId;
        return nfa;
    }

    /**
     * Does the Kleene operation on automaton (no side effects).
     * Complexity: O(n * m) where n is number of states and m is cardinality of alphabet
     * @return NFA automaton that accepts Kleene star (zero or more
     * concatenated repetitions) in the language of the given automaton.
     */
    public Nfa kleeneStar() {
        Nfa nfa = new Nfa();
        nfa.initialStateId = "q0";
        NfaState startState = new NfaState("q0", true);
        nfa.addState(startState);
        int i = 1;
        Map<DfaState, String> info = new HashMap<>();
        List<Character> alphabet = getAlphabet();

        for (var state : states) {
            info.put(state, "q"+i);
            try {
                nfa.addState("q" + i, state.isFinal());
            } catch (StateExistsException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
        for (var state : states) {
            for (var symbol : alphabet) {
                List<String> transitions = new LinkedList<>();
                transitions.add(info.get(state.getTransition(symbol)));
                try {
                    nfa.addTransition(info.get(state), symbol, transitions);
                } catch (StateExistsException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        NfaState oldInitial;
        try {
            oldInitial = nfa.findStateByString(info.get(findStateByString(initialStateId)));
        }
        catch (StateExistsException e) {
            throw new RuntimeException();
        }
        List<String> list = new ArrayList<>();
        list.add(oldInitial.getId());
        try {
            nfa.addEpsilon(nfa.initialStateId, list);
        } catch (StateExistsException e) {
            throw new RuntimeException(e);
        }

        List<String> to = new ArrayList<>();
        to.add(nfa.initialStateId);
        for (var state : nfa.getStates()) {
            if (state != oldInitial && state.isFinal() && state != startState) {
                try {
                    nfa.addEpsilon(state.getId(), to);
                } catch (StateExistsException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return nfa;
    }

    /**
     * Finds a length of the shortest word accepted by automaton. Does the BFS
     * Complexity: O(n^2 * m) where n is number of states and m is cardinality of alphabet
     * search until it comes to a first final state.
     * @return length of the shortest word accepted by automaton
     * @throws StateExistsException if the state doesn't exist
     * @throws WordLengthException if the automaton doesn't accept any word
     */
    public int shortestWord() throws StateExistsException, WordLengthException {
        Queue<DfaState> queueStates = new LinkedList<>();
        List<Character> alphabet = getAlphabet();
        Set<DfaState> checkedStates = new HashSet<>();
        DfaState initial = findStateByString(initialStateId);
        queueStates.add(initial);
        checkedStates.add(initial);

        queueStates.add(null);
        int length = 0;
        boolean found = false;
        while (!queueStates.isEmpty()) { // O(n)
            if (queueStates.peek() == null) {
                length++;
                queueStates.remove();
                continue;
            }
            while (queueStates.peek() != null) { // O(n)
                DfaState state = queueStates.peek();
                if (state.isFinal()) {
                    found = true;
                    break;
                }
                for (var symbol : alphabet) { // O(m)
                    DfaState next = state.getTransition(symbol);
                    if (!checkedStates.contains(next)) {
                        queueStates.add(next);
                        checkedStates.add(next);
                    }
                }
                queueStates.remove();
            }
            if (found)
                break;
            queueStates.add(null);

        }
        if (found)
            return length;
        else
            throw new WordLengthException("Automata doesn't accept any words");
    }

    /**
     * Checks if automaton is finite and finds the longest word accepted by automaton.
     * Complexity: O(n*m*k) - {@link #minimize()}  Minimize}
     * @return length of the shortest word accepted by automaton
     * @throws StateExistsException if the state doesn't exist
     * @throws WordLengthException if automaton is not finite
     */
    public int longestWord() throws StateExistsException, WordLengthException {
        Dfa dfa = this.minimize(); // O(n*m*k)
        DfaState deadState = null;
        mainLoop:
        for (var state : dfa.states) {
            if (!state.isFinal()) { // remove each state that is not final and that has only auto-transitions
                for (var tr : state.getTransitions().entrySet()) {
                    if (!tr.getValue().getId().equals(state.getId()))
                        continue mainLoop;
                }
                deadState = state;
                dfa.states.remove(state);
                break;
            }
        }
        List<Pair<DfaState, Character>> toRemove = new ArrayList<>();
        if (deadState != null) {
            for (var state : dfa.states) {
                for (var tr : state.getTransitions().entrySet()) {
                    if (tr.getValue() == deadState)
                        toRemove.add(Pair.of(state, tr.getKey()));
                }
            }
        }
        for (var pair : toRemove)
            pair.first.getTransitions().remove(pair.second);

        if (isCyclic(dfa))
            throw new WordLengthException("There is no longest word (Language has infinite words)");

        return longestStringInTree(dfa.findStateByString(dfa.initialStateId)) - 1;
    }

    private static int longestStringInTree(DfaState root) {
        int max = 0;
        for (var childNode  : root.getTransitions().entrySet()) {
            int height = longestStringInTree(childNode.getValue());
            if (height > max)
                max = height;
        }
        return max + 1;
    }
    private static boolean isCyclicUtil(DfaState curr, List<DfaState> visited, List<DfaState> recStack)
    {
        if (recStack.contains(curr))
            return true;

        if (visited.contains(curr))
            return false;

        visited.add(curr);
        recStack.add(curr);

        for (var tr : curr.getTransitions().entrySet())
            if (isCyclicUtil(tr.getValue(), visited, recStack))
                return true;

        recStack.remove(curr);

        return false;
    }

    private static boolean isCyclic(Dfa dfa) throws StateExistsException {
        List<DfaState> visited = new ArrayList<>();
        List<DfaState> recStack = new ArrayList<>();

        List<DfaState> allStates = new ArrayList<>();
        DfaState initial = dfa.findStateByString(dfa.initialStateId);
        allStates.add(initial);
        for (var state : dfa.states) {
            if (state != initial)
                allStates.add(state);
        }

        for (var state : allStates)
            if (isCyclicUtil(state, visited, recStack))
                return true;

        return false;
    }

    /**
     * Checks if two automatons are equivalent
     * Complexity: O(n*m*k) - {@link #minimize()}  Minimize}
     * @param o other automaton
     * @return true if they're equivalent
     * @throws StateExistsException if the state doesn't exist
     */
    public boolean equivalent(Object o) throws StateExistsException {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Dfa)) {
            return false;
        }

        Dfa dfa1 = this.minimize();
        Dfa dfa2 = ((Dfa)o).minimize();
        if (!equalAlphabets(dfa1, dfa2) || dfa1.states.size() != dfa2.states.size())
            return false;

        DfaState initial1;
        DfaState initial2;
        initial1 = dfa1.findStateByString(dfa1.initialStateId);
        initial2 = dfa2.findStateByString(dfa2.initialStateId);


        Set<DfaState> checkedStates1 = new HashSet<>();
        Set<DfaState> checkedStates2 = new HashSet<>();
        checkedStates1.add(initial1);
        checkedStates2.add(initial2);
        Queue<DfaState> queueStates1 = new LinkedList<>();
        Queue<DfaState> queueStates2 = new LinkedList<>();
        List<Character> alphabet = getAlphabet();
        while (!queueStates1.isEmpty() && !queueStates2.isEmpty()) {
            DfaState state1 = queueStates1.peek();
            DfaState state2 = queueStates2.peek();

            if (state1.isFinal() != state2.isFinal())
                return false;

            for (var symbol : alphabet) {
                DfaState next1 = state1.getTransition(symbol);
                DfaState next2 = state2.getTransition(symbol);

                if (!checkedStates1.contains(next1) && !checkedStates2.contains(next2)) {
                    queueStates1.add(next1);
                    queueStates2.add(next2);
                    checkedStates1.add(next1);
                    checkedStates2.add(next2);
                }
            }

            queueStates1.remove();
            queueStates2.remove();
        }
        return true;
    }
    private static boolean equalAlphabets(Dfa dfa1, Dfa dfa2) {
        List<Character> a1 = dfa1.getAlphabet();
        List<Character> a2 = dfa2.getAlphabet();
        return a1.size() == a2.size() && new HashSet<>(a1).containsAll(a2);
    }

}

