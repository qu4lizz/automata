package qu4lizz.automata.state;

import java.util.*;

/**
 * State for NFA.
 * @author Miroslav Bratic &lt;<a href="miroslavbratic00@gmail.com">miroslavbratic00@gmail.com</a>&gt;
 */
public class NfaState extends State {
    public static final Character EPSILON = 'ε';

    private Map<Character, Set<NfaState>> transitions = new HashMap<>();

    public NfaState(String id) {
        super(id);
    }
    public NfaState(String id, Boolean isFinal) {
        super(id, isFinal);
    }

    public Map<Character, Set<NfaState>> getTransitions() {
        return transitions;
    }

    public void addTransition(Character symbol, Set<NfaState> nextStates) {
        transitions.put(symbol, nextStates);
    }

    /**
     * Does the closure from source states, then from that set of states it transitions for each
     * symbol to another set of states and does closure on that set.
     * Complexity: O(n) - linear of states
     * @param symbol transition symbol
     * @param setOfStates source set of states
     * @return states computed by transitions
     */
    public static Set<NfaState> getTransitionsSet(Character symbol, Set<NfaState> setOfStates) {
        Set<NfaState> resultStates = new HashSet<>();
        Set<NfaState> nextStates = new HashSet<>();
        resultStates.addAll(setOfStates);
        resultStates = closure(resultStates);
        for (var state : resultStates)
            if (state.transitions.get(symbol) != null)
                nextStates.addAll(state.transitions.get(symbol));

        resultStates = closure(nextStates);

        return resultStates;
    }

    /**
     * Returns states that are reachable for epsilon transitions for a given set of states.
     * Complexity: O(n) - linear of states
     * @param states source states
     * @return states computed by closure
     */
    public static Set<NfaState> closure(Set<NfaState> states) {
        List<NfaState> checkedStates = new ArrayList<>();
        Set<NfaState> resultStates = new HashSet<>();

        while(true) {
            boolean hasUncheckedState = false;
            Set<NfaState> tmpCurrentStates = new HashSet<>();
            for(NfaState state : states) {
                if(!checkedStates.contains(state)) {
                    checkedStates.add(state);
                    if (state.transitions.get(EPSILON) != null)
                        tmpCurrentStates.addAll(state.transitions.get(EPSILON));
                    tmpCurrentStates.add(state);
                    hasUncheckedState = true;
                }
            }
            states = tmpCurrentStates;
            resultStates.addAll(states);

            if(!hasUncheckedState)
                break;
        }

        return resultStates;
    }

}
