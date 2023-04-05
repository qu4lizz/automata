package qu4lizz.automata.state;

import java.util.HashMap;
import java.util.Map;

/**
 * State for DFA.
 * @author Miroslav Bratic &lt;<a href="miroslavbratic00@gmail.com">miroslavbratic00@gmail.com</a>&gt;
 */
public class DfaState extends State {
    private Map<Character, DfaState> transitions = new HashMap<>();
    public DfaState(String id) {
        super(id);
    }
    public DfaState(String id, Boolean isFinal) {
        super(id, isFinal);
    }


    /**
     * Gets a map of keys (symbols from alphabet) and values (to which state).
     * @return map of transitions
     */
    public Map<Character, DfaState> getTransitions() {
        return transitions;
    }

    /**
     * Gets destination state for given symbol.
     * @param symbol character to get to destination state
     * @return destination state
     */
    public DfaState getTransition(Character symbol) {
        return transitions.get(symbol);
    }
    /**
     * Adds transition from state.
     * @param symbol transition symbol
     * @param nextState destination state
     */
    public void addTransition(Character symbol, DfaState nextState) {
        transitions.put(symbol, nextState);
    }
}
