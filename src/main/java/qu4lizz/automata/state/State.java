package qu4lizz.automata.state;

/**
 * Base class for <code>DfaState</code> and <code>NfaState</code>.
 * @author Miroslav Bratic &lt;<a href="miroslavbratic00@gmail.com">miroslavbratic00@gmail.com</a>&gt;
 */
public abstract class State {
    protected String id;
    protected Boolean isFinal;

    /**
     * Constructs a new state by id.
     * @param id name of the state
     */
    public State(String id) {
        this.id = id;
    }
    /**
     * Constructs a new state by id and state acceptance.
     * @param id name of the state
     * @param isFinal acceptance of the state
     */
    public State(String id, Boolean isFinal) {
        this.id = id;
        this.isFinal = isFinal;
    }

    /**
     * Returns acceptance status
     * @return true if the state is final
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     *
     * @param aFinal if true, this state is an accept state
     */
    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    /**
     * Gets name of the state string.
     * @return name of the state
     */
    public String getId() { return id; }

    /**
     * Sets name of the state.
     * @param id name of the state
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * See {@link Object#equals(Object)}.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof State)) {
            return false;
        }

        return id.equals(((State) o).id);
    }
}
