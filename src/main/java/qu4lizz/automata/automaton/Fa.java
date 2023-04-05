package qu4lizz.automata.automaton;

import qu4lizz.automata.exception.StateExistsException;
import qu4lizz.automata.exception.WrongAlphabetException;
import qu4lizz.automata.state.State;

/**
 * Base class for <code>Dfa</code> and <code>Nfa</code>.
 * @author Miroslav Bratic &lt;<a href="miroslavbratic00@gmail.com">miroslavbratic00@gmail.com</a>&gt;
 */

public abstract class Fa {
    protected String initialStateId;

    public void setInitialState(String initialState) {
        this.initialStateId = initialState;
    }

    public String getInitialStateId() {
        return initialStateId;
    }

    public abstract void addState(String state, Boolean isFinal) throws StateExistsException;

    /**
     * Abstract method which has to be implemented by <code>Dfa</code> and <code>Nfa</code> classes.
     * @param input string on which will automata be executed
     * @return true if the string is accepted by the automata
     */
    public abstract boolean run(String input) throws WrongAlphabetException, StateExistsException;


    abstract State findStateByString(String state) throws StateExistsException;

}
