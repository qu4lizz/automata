import qu4lizz.automata.exception.*;
import qu4lizz.automata.automaton.*;
import org.junit.jupiter.api.Test;
import java.util.Vector;
import static org.junit.jupiter.api.Assertions.*;

class NfaTest {

    @Test
    void run() throws StateExistsException {
        Nfa nfa = new Nfa();
        nfa.setInitialState("q0");
        nfa.addState("q0", false);
        nfa.addState("q1", false);
        nfa.addState("q2", true);
        nfa.addState("q3", false);
        nfa.addState("q4", false);
        nfa.addState("q5", true);
        Vector<String> q0tra = new Vector<>() {{ add(new String("q1")); add(new String("q3"));
            add(new String("q5")); }};
        nfa.addTransition("q0", 'a', q0tra);
        Vector<String> q0trb = new Vector<>() {{ add(new String("q4")); }};
        nfa.addTransition("q0", 'b', q0trb);
        Vector<String> q1trb = new Vector<>() {{ add(new String("q1")); }};
        nfa.addTransition("q1", 'b', q1trb);
        Vector<String> q2tre = new Vector<>() {{ add(new String("q1")); add(new String("q5"));}};
        nfa.addEpsilon("q2", q2tre);
        Vector<String> q3tre = new Vector<>() {{ add(new String("q5"));}};
        nfa.addEpsilon("q3", q3tre);
        Vector<String> q4tre = new Vector<>() {{ add(new String("q3"));}};
        nfa.addEpsilon("q4", q4tre);
        Vector<String> q5tre = new Vector<>() {{ add(new String("q2"));}};
        nfa.addEpsilon("q5", q5tre);
        assertTrue(nfa.run("a"));
    }

    @Test
    void toDfa() throws StateExistsException {
        Nfa nfa = new Nfa();
        nfa.setInitialState("q0");
        nfa.addState("q0", false);
        nfa.addState("q1", false);
        nfa.addState("q2", true);
        nfa.addState("q3", false);
        nfa.addState("q4", false);
        nfa.addState("q5", true);
        Vector<String> q0tra = new Vector<>() {{ add(new String("q1")); add(new String("q3"));
            add(new String("q5")); }};
        nfa.addTransition("q0", 'a', q0tra);
        Vector<String> q0trb = new Vector<>() {{ add(new String("q4")); }};
        nfa.addTransition("q0", 'b', q0trb);
        Vector<String> q1trb = new Vector<>() {{ add(new String("q1")); }};
        nfa.addTransition("q1", 'b', q1trb);
        Vector<String> q2tre = new Vector<>() {{ add(new String("q1")); add(new String("q5"));}};
        nfa.addEpsilon("q2", q2tre);
        Vector<String> q3tre = new Vector<>() {{ add(new String("q5"));}};
        nfa.addEpsilon("q3", q3tre);
        Vector<String> q4tre = new Vector<>() {{ add(new String("q3"));}};
        nfa.addEpsilon("q4", q4tre);
        Vector<String> q5tre = new Vector<>() {{ add(new String("q2"));}};
        nfa.addEpsilon("q5", q5tre);

        Dfa dfa = new Dfa();
        dfa.setInitialState("q0");
        dfa.addState("q0", false);
        dfa.addState("q1", true);
        dfa.addState("q2", true);
        dfa.addState("q3", false);
        dfa.addState("empty", false);
        dfa.addTransition("q0", 'a', "q1");
        dfa.addTransition("q0", 'b', "q2");
        dfa.addTransition("q1", 'a', "empty");
        dfa.addTransition("q1", 'b', "q3");
        dfa.addTransition("q2", 'a', "empty");
        dfa.addTransition("q2", 'b', "q3");
        dfa.addTransition("q3", 'a', "empty");
        dfa.addTransition("q3", 'b', "q3");
        dfa.addTransition("empty", 'a', "empty");
        dfa.addTransition("empty", 'b', "empty");

        assertTrue(nfa.toDfa().equivalent(dfa));
    }


}