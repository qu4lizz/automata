import qu4lizz.automata.exception.*;
import qu4lizz.automata.automaton.*;
import qu4lizz.automata.state.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DfaTest {

    @Test
    void run() throws StateExistsException, WrongAlphabetException {
        Dfa dfa = new Dfa();
        dfa.setInitialState("q0");
        dfa.addState("q0", false);
        dfa.addState("q1", false);
        dfa.addState("q2", true);
        dfa.addTransition("q0", 'a', "q1");
        dfa.addTransition("q0", 'b', "q2");
        dfa.addTransition("q1", 'a', "q1");
        dfa.addTransition("q1", 'b', "q2");
        dfa.addTransition("q2", 'a', "q2");
        dfa.addTransition("q2", 'b', "q1");
        assertTrue(dfa.run("ababb"));

    }

    @Test
    void minimize() throws StateExistsException {
        Dfa dfa = new Dfa();
        dfa.setInitialState("q0");
        dfa.addState("q0", false);
        dfa.addState("q1", false);
        dfa.addState("q2", true);
        dfa.addState("q3", true);
        dfa.addState("q4", true);
        dfa.addState("q5", false);
        dfa.addTransition("q0", '0', "q1");
        dfa.addTransition("q0", '1', "q2");
        dfa.addTransition("q1", '0', "q0");
        dfa.addTransition("q1", '1', "q3");
        dfa.addTransition("q2", '0', "q4");
        dfa.addTransition("q2", '1', "q5");
        dfa.addTransition("q3", '0', "q4");
        dfa.addTransition("q3", '1', "q5");
        dfa.addTransition("q4", '0', "q4");
        dfa.addTransition("q4", '1', "q5");
        dfa.addTransition("q5", '0', "q5");
        dfa.addTransition("q5", '1', "q5");

        Dfa dfaMin = new Dfa();
        dfaMin.setInitialState("p0");
        dfaMin.addState("p0", false);
        dfaMin.addState("p1", true);
        dfaMin.addState("p2", false);
        dfaMin.addTransition("p0", '0', "p0");
        dfaMin.addTransition("p0", '1', "p1");
        dfaMin.addTransition("p1", '0', "p1");
        dfaMin.addTransition("p1", '1', "p2");
        dfaMin.addTransition("p2", '0', "p2");
        dfaMin.addTransition("p2", '1', "p2");

        assertTrue(dfa.equivalent(dfaMin));
    }

    @Test
    void union() throws StateExistsException {
        Dfa dfa1 = new Dfa();
        dfa1.setInitialState("q0");
        dfa1.addState("q0", false);
        dfa1.addState("q1", false);
        dfa1.addState("q2", false);
        dfa1.addState("q3", true);
        dfa1.addState("q4", true);
        dfa1.addTransition("q0", 'a', "q1");
        dfa1.addTransition("q0", 'b', "q2");
        dfa1.addTransition("q1", 'a', "q3");
        dfa1.addTransition("q1", 'b', "q1");
        dfa1.addTransition("q2", 'a', "q2");
        dfa1.addTransition("q2", 'b', "q4");
        dfa1.addTransition("q3", 'a', "q3");
        dfa1.addTransition("q3", 'b', "q1");
        dfa1.addTransition("q4", 'a', "q2");
        dfa1.addTransition("q4", 'b', "q4");
        Dfa dfa2 = new Dfa();
        dfa2.setInitialState("p0");
        dfa2.addState("p0", true);
        dfa2.addState("p1", true);
        dfa2.addState("p2", true);
        dfa2.addState("p3", false);
        dfa2.addTransition("p0", 'a', "p0");
        dfa2.addTransition("p0", 'b', "p1");
        dfa2.addTransition("p1", 'a', "p0");
        dfa2.addTransition("p1", 'b', "p2");
        dfa2.addTransition("p2", 'a', "p0");
        dfa2.addTransition("p2", 'b', "p3");
        dfa2.addTransition("p3", 'a', "p3");
        dfa2.addTransition("p3", 'b', "p3");

        Dfa dfaRes = new Dfa();
        dfaRes.setInitialState("q00");
        dfaRes.addState("q00", true);
        dfaRes.addState("q10", true);
        dfaRes.addState("q30", true);
        dfaRes.addState("q11", true);
        dfaRes.addState("q12", true);
        dfaRes.addState("q13", false);
        dfaRes.addState("q33", true);
        dfaRes.addState("q21", true);
        dfaRes.addState("q20", true);
        dfaRes.addState("q42", true);
        dfaRes.addState("q41", true);
        dfaRes.addState("q43", true);
        dfaRes.addState("q23", false);
        dfaRes.addTransition("q00", 'a', "q10");
        dfaRes.addTransition("q00", 'b', "q21");
        dfaRes.addTransition("q10", 'a', "q30");
        dfaRes.addTransition("q10", 'b', "q11");
        dfaRes.addTransition("q30", 'a', "q30");
        dfaRes.addTransition("q30", 'b', "q11");
        dfaRes.addTransition("q11", 'a', "q30");
        dfaRes.addTransition("q11", 'b', "q12");
        dfaRes.addTransition("q12", 'a', "q30");
        dfaRes.addTransition("q12", 'b', "q13");
        dfaRes.addTransition("q13", 'a', "q33");
        dfaRes.addTransition("q13", 'b', "q13");
        dfaRes.addTransition("q33", 'a', "q33");
        dfaRes.addTransition("q33", 'b', "q13");
        dfaRes.addTransition("q21", 'a', "q20");
        dfaRes.addTransition("q21", 'b', "q42");
        dfaRes.addTransition("q20", 'a', "q20");
        dfaRes.addTransition("q20", 'b', "q41");
        dfaRes.addTransition("q42", 'a', "q20");
        dfaRes.addTransition("q42", 'b', "q43");
        dfaRes.addTransition("q41", 'a', "q20");
        dfaRes.addTransition("q41", 'b', "q42");
        dfaRes.addTransition("q43", 'a', "q23");
        dfaRes.addTransition("q43", 'b', "q43");
        dfaRes.addTransition("q23", 'a', "q23");
        dfaRes.addTransition("q23", 'b', "q43");
        assertTrue(dfa1.union(dfa2).equivalent(dfaRes));
    }

    @Test
    void intersection() throws StateExistsException {
        Dfa dfa1 = new Dfa();
        dfa1.setInitialState("q0");
        dfa1.addState("q0", false);
        dfa1.addState("q1", false);
        dfa1.addState("q2", false);
        dfa1.addState("q3", true);
        dfa1.addState("q4", true);
        dfa1.addTransition("q0", 'a', "q1");
        dfa1.addTransition("q0", 'b', "q2");
        dfa1.addTransition("q1", 'a', "q3");
        dfa1.addTransition("q1", 'b', "q1");
        dfa1.addTransition("q2", 'a', "q2");
        dfa1.addTransition("q2", 'b', "q4");
        dfa1.addTransition("q3", 'a', "q3");
        dfa1.addTransition("q3", 'b', "q1");
        dfa1.addTransition("q4", 'a', "q2");
        dfa1.addTransition("q4", 'b', "q4");
        Dfa dfa2 = new Dfa();
        dfa2.setInitialState("p0");
        dfa2.addState("p0", true);
        dfa2.addState("p1", true);
        dfa2.addState("p2", true);
        dfa2.addState("p3", false);
        dfa2.addTransition("p0", 'a', "p0");
        dfa2.addTransition("p0", 'b', "p1");
        dfa2.addTransition("p1", 'a', "p0");
        dfa2.addTransition("p1", 'b', "p2");
        dfa2.addTransition("p2", 'a', "p0");
        dfa2.addTransition("p2", 'b', "p3");
        dfa2.addTransition("p3", 'a', "p3");
        dfa2.addTransition("p3", 'b', "p3");

        Dfa dfaRes = new Dfa();
        dfaRes.setInitialState("q00");
        dfaRes.addState("q00", false);
        dfaRes.addState("q10", false);
        dfaRes.addState("q30", true);
        dfaRes.addState("q11", false);
        dfaRes.addState("q12", true);
        dfaRes.addState("q13", false);
        dfaRes.addState("q33", false);
        dfaRes.addState("q21", false);
        dfaRes.addState("q20", false);
        dfaRes.addState("q42", false);
        dfaRes.addState("q41", true);
        dfaRes.addState("q43", false);
        dfaRes.addState("q23", false);
        dfaRes.addTransition("q00", 'a', "q10");
        dfaRes.addTransition("q00", 'b', "q21");
        dfaRes.addTransition("q10", 'a', "q30");
        dfaRes.addTransition("q10", 'b', "q11");
        dfaRes.addTransition("q30", 'a', "q30");
        dfaRes.addTransition("q30", 'b', "q11");
        dfaRes.addTransition("q11", 'a', "q30");
        dfaRes.addTransition("q11", 'b', "q12");
        dfaRes.addTransition("q12", 'a', "q30");
        dfaRes.addTransition("q12", 'b', "q13");
        dfaRes.addTransition("q13", 'a', "q33");
        dfaRes.addTransition("q13", 'b', "q13");
        dfaRes.addTransition("q33", 'a', "q33");
        dfaRes.addTransition("q33", 'b', "q13");
        dfaRes.addTransition("q21", 'a', "q20");
        dfaRes.addTransition("q21", 'b', "q42");
        dfaRes.addTransition("q20", 'a', "q20");
        dfaRes.addTransition("q20", 'b', "q41");
        dfaRes.addTransition("q42", 'a', "q20");
        dfaRes.addTransition("q42", 'b', "q43");
        dfaRes.addTransition("q41", 'a', "q20");
        dfaRes.addTransition("q41", 'b', "q42");
        dfaRes.addTransition("q43", 'a', "q23");
        dfaRes.addTransition("q43", 'b', "q43");
        dfaRes.addTransition("q23", 'a', "q23");
        dfaRes.addTransition("q23", 'b', "q43");
        assertTrue(dfa1.intersection(dfa2).equivalent(dfaRes));
    }

    @Test
    void difference() throws StateExistsException {
        Dfa dfa1 = new Dfa();
        dfa1.setInitialState("q0");
        dfa1.addState("q0", false);
        dfa1.addState("q1", false);
        dfa1.addState("q2", false);
        dfa1.addState("q3", true);
        dfa1.addState("q4", true);
        dfa1.addTransition("q0", 'a', "q1");
        dfa1.addTransition("q0", 'b', "q2");
        dfa1.addTransition("q1", 'a', "q3");
        dfa1.addTransition("q1", 'b', "q1");
        dfa1.addTransition("q2", 'a', "q2");
        dfa1.addTransition("q2", 'b', "q4");
        dfa1.addTransition("q3", 'a', "q3");
        dfa1.addTransition("q3", 'b', "q1");
        dfa1.addTransition("q4", 'a', "q2");
        dfa1.addTransition("q4", 'b', "q4");
        Dfa dfa2 = new Dfa();
        dfa2.setInitialState("p0");
        dfa2.addState("p0", true);
        dfa2.addState("p1", true);
        dfa2.addState("p2", true);
        dfa2.addState("p3", false);
        dfa2.addTransition("p0", 'a', "p0");
        dfa2.addTransition("p0", 'b', "p1");
        dfa2.addTransition("p1", 'a', "p0");
        dfa2.addTransition("p1", 'b', "p2");
        dfa2.addTransition("p2", 'a', "p0");
        dfa2.addTransition("p2", 'b', "p3");
        dfa2.addTransition("p3", 'a', "p3");
        dfa2.addTransition("p3", 'b', "p3");

        Dfa dfaRes = new Dfa();
        dfaRes.setInitialState("q00");
        dfaRes.addState("q00", false);
        dfaRes.addState("q10", false);
        dfaRes.addState("q30", false);
        dfaRes.addState("q11", false);
        dfaRes.addState("q12", false);
        dfaRes.addState("q13", false);
        dfaRes.addState("q33", true);
        dfaRes.addState("q21", false);
        dfaRes.addState("q20", false);
        dfaRes.addState("q42", false);
        dfaRes.addState("q41", false);
        dfaRes.addState("q43", true);
        dfaRes.addState("q23", false);
        dfaRes.addTransition("q00", 'a', "q10");
        dfaRes.addTransition("q00", 'b', "q21");
        dfaRes.addTransition("q10", 'a', "q30");
        dfaRes.addTransition("q10", 'b', "q11");
        dfaRes.addTransition("q30", 'a', "q30");
        dfaRes.addTransition("q30", 'b', "q11");
        dfaRes.addTransition("q11", 'a', "q30");
        dfaRes.addTransition("q11", 'b', "q12");
        dfaRes.addTransition("q12", 'a', "q30");
        dfaRes.addTransition("q12", 'b', "q13");
        dfaRes.addTransition("q13", 'a', "q33");
        dfaRes.addTransition("q13", 'b', "q13");
        dfaRes.addTransition("q33", 'a', "q33");
        dfaRes.addTransition("q33", 'b', "q13");
        dfaRes.addTransition("q21", 'a', "q20");
        dfaRes.addTransition("q21", 'b', "q42");
        dfaRes.addTransition("q20", 'a', "q20");
        dfaRes.addTransition("q20", 'b', "q41");
        dfaRes.addTransition("q42", 'a', "q20");
        dfaRes.addTransition("q42", 'b', "q43");
        dfaRes.addTransition("q41", 'a', "q20");
        dfaRes.addTransition("q41", 'b', "q42");
        dfaRes.addTransition("q43", 'a', "q23");
        dfaRes.addTransition("q43", 'b', "q43");
        dfaRes.addTransition("q23", 'a', "q23");
        dfaRes.addTransition("q23", 'b', "q43");
        assertTrue(dfa1.difference(dfa2).equivalent(dfaRes));
    }

    @Test
    void complement() throws StateExistsException {
        Dfa dfa1 = new Dfa();
        dfa1.setInitialState("q0");
        dfa1.addState("q0", false);
        dfa1.addState("q1", false);
        dfa1.addState("q2", false);
        dfa1.addState("q3", true);
        dfa1.addTransition("q0", 'a', "q0");
        dfa1.addTransition("q0", 'b', "q1");
        dfa1.addTransition("q1", 'a', "q0");
        dfa1.addTransition("q1", 'b', "q2");
        dfa1.addTransition("q2", 'a', "q0");
        dfa1.addTransition("q2", 'b', "q3");
        dfa1.addTransition("q3", 'a', "q3");
        dfa1.addTransition("q3", 'b', "q3");
        Dfa dfa2 = new Dfa();
        dfa2.setInitialState("p0");
        dfa2.addState("p0", true);
        dfa2.addState("p1", true);
        dfa2.addState("p2", true);
        dfa2.addState("p3", false);
        dfa2.addTransition("p0", 'a', "p0");
        dfa2.addTransition("p0", 'b', "p1");
        dfa2.addTransition("p1", 'a', "p0");
        dfa2.addTransition("p1", 'b', "p2");
        dfa2.addTransition("p2", 'a', "p0");
        dfa2.addTransition("p2", 'b', "p3");
        dfa2.addTransition("p3", 'a', "p3");
        dfa2.addTransition("p3", 'b', "p3");
        assertTrue(dfa1.complement().equivalent(dfa2));
    }

    @Test
    void concatenation() throws StateExistsException {
        Dfa dfa1 = new Dfa();
        dfa1.setInitialState("q0");
        dfa1.addState("q0", false);
        dfa1.addState("q1", false);
        dfa1.addState("q2", false);
        dfa1.addState("q3", true);
        dfa1.addState("q4", true);
        dfa1.addTransition("q0", 'a', "q1");
        dfa1.addTransition("q0", 'b', "q2");
        dfa1.addTransition("q1", 'a', "q3");
        dfa1.addTransition("q1", 'b', "q1");
        dfa1.addTransition("q2", 'a', "q2");
        dfa1.addTransition("q2", 'b', "q4");
        dfa1.addTransition("q3", 'a', "q3");
        dfa1.addTransition("q3", 'b', "q1");
        dfa1.addTransition("q4", 'a', "q2");
        dfa1.addTransition("q4", 'b', "q4");
        Dfa dfa2 = new Dfa();
        dfa2.setInitialState("p0");
        dfa2.addState("p0", true);
        dfa2.addState("p1", true);
        dfa2.addState("p2", true);
        dfa2.addState("p3", false);
        dfa2.addTransition("p0", 'a', "p0");
        dfa2.addTransition("p0", 'b', "p1");
        dfa2.addTransition("p1", 'a', "p0");
        dfa2.addTransition("p1", 'b', "p2");
        dfa2.addTransition("p2", 'a', "p0");
        dfa2.addTransition("p2", 'b', "p3");
        dfa2.addTransition("p3", 'a', "p3");
        dfa2.addTransition("p3", 'b', "p3");

        Nfa nfaRes = new Nfa();
        nfaRes.setInitialState("q0");
        nfaRes.addState("q0", false);
        nfaRes.addState("q1", false);
        nfaRes.addState("q2", false);
        nfaRes.addState("q3", false);
        nfaRes.addState("q4", false);
        nfaRes.addState("p0", true);
        nfaRes.addState("p1", true);
        nfaRes.addState("p2", true);
        nfaRes.addState("p3", false);
        nfaRes.addTransition("q0", 'a', "q1");
        nfaRes.addTransition("q0", 'b', "q2");
        nfaRes.addTransition("q1", 'a', "q3");
        nfaRes.addTransition("q1", 'b', "q1");
        nfaRes.addTransition("q2", 'a', "q2");
        nfaRes.addTransition("q2", 'b', "q4");
        nfaRes.addTransition("q3", 'a', "q3");
        nfaRes.addTransition("q3", 'b', "q1");
        nfaRes.addTransition("q4", 'a', "q2");
        nfaRes.addTransition("q4", 'b', "q4");
        nfaRes.addTransition("q4", NfaState.EPSILON,"p0");
        nfaRes.addTransition("q3", NfaState.EPSILON,"p0");
        nfaRes.addTransition("p0", 'a', "p0");
        nfaRes.addTransition("p0", 'b', "p1");
        nfaRes.addTransition("p1", 'a', "p0");
        nfaRes.addTransition("p1", 'b', "p2");
        nfaRes.addTransition("p2", 'a', "p0");
        nfaRes.addTransition("p2", 'b', "p3");
        nfaRes.addTransition("p3", 'a', "p3");
        nfaRes.addTransition("p3", 'b', "p3");
        assertTrue(nfaRes.toDfa().equivalent(dfa1.concatenation(dfa2)));
    }

    @Test
    void kleeneStar() throws StateExistsException {
        Dfa dfa1 = new Dfa();
        dfa1.setInitialState("q0");
        dfa1.addState("q0", false);
        dfa1.addState("q1", false);
        dfa1.addState("q2", false);
        dfa1.addState("q3", true);
        dfa1.addState("q4", true);
        dfa1.addTransition("q0", 'a', "q1");
        dfa1.addTransition("q0", 'b', "q2");
        dfa1.addTransition("q1", 'a', "q3");
        dfa1.addTransition("q1", 'b', "q1");
        dfa1.addTransition("q2", 'a', "q2");
        dfa1.addTransition("q2", 'b', "q4");
        dfa1.addTransition("q3", 'a', "q3");
        dfa1.addTransition("q3", 'b', "q1");
        dfa1.addTransition("q4", 'a', "q2");
        dfa1.addTransition("q4", 'b', "q4");

        Nfa nfa = new Nfa();
        nfa.setInitialState("q00");
        nfa.addState("q00", true);
        nfa.addState("q0", false);
        nfa.addState("q1", false);
        nfa.addState("q2", false);
        nfa.addState("q3", true);
        nfa.addState("q4", true);
        nfa.addTransition("q00", NfaState.EPSILON, "q0");
        nfa.addTransition("q0", 'a', "q1");
        nfa.addTransition("q0", 'b', "q2");
        nfa.addTransition("q1", 'a', "q3");
        nfa.addTransition("q1", 'b', "q1");
        nfa.addTransition("q2", 'a', "q2");
        nfa.addTransition("q2", 'b', "q4");
        nfa.addTransition("q3", 'a', "q3");
        nfa.addTransition("q3", 'b', "q1");
        nfa.addTransition("q4", 'a', "q2");
        nfa.addTransition("q4", 'b', "q4");
        nfa.addTransition("q4", NfaState.EPSILON, "q00");
        nfa.addTransition("q4", NfaState.EPSILON, "q00");

        assertTrue(dfa1.kleeneStar().equivalent(nfa));
    }

    @Test
    void shortestWord() throws StateExistsException, WordLengthException {
        Dfa dfa = new Dfa();
        dfa.setInitialState("q0");
        dfa.addState("q0", false);
        dfa.addState("q1", false);
        dfa.addState("q2", false);
        dfa.addState("q3", true);
        dfa.addState("q4", true);
        dfa.addTransition("q0", 'a', "q1");
        dfa.addTransition("q0", 'b', "q2");
        dfa.addTransition("q1", 'a', "q3");
        dfa.addTransition("q1", 'b', "q1");
        dfa.addTransition("q2", 'a', "q2");
        dfa.addTransition("q2", 'b', "q4");
        dfa.addTransition("q3", 'a', "q3");
        dfa.addTransition("q3", 'b', "q1");
        dfa.addTransition("q4", 'a', "q2");
        dfa.addTransition("q4", 'b', "q4");

        assertEquals(2, dfa.shortestWord());
    }

    @Test
    void longestWord() throws StateExistsException, WordLengthException {
        Dfa dfa = new Dfa();
        dfa.setInitialState("q0");
        dfa.addState("q0", false);
        dfa.addState("q1", false);
        dfa.addState("q2", false);
        dfa.addState("q3", true);
        dfa.addState("q4", false);
        dfa.addTransition("q0", 'a', "q1");
        dfa.addTransition("q1", 'a', "q2");
        dfa.addTransition("q2", 'a', "q3");
        dfa.addTransition("q3", 'a', "q4");
        dfa.addTransition("q4", 'a', "q4");

        assertEquals(3, dfa.longestWord());

    }

    @Test
    void equivalent() throws StateExistsException {
        Dfa equiv1 = new Dfa();
        equiv1.setInitialState("q1");
        equiv1.addState("q1", true);
        equiv1.addState("q2", false);
        equiv1.addState("q3", false);
        equiv1.addTransition("q1", 'c', "q1");
        equiv1.addTransition("q1", 'd', "q2");
        equiv1.addTransition("q2", 'c', "q3");
        equiv1.addTransition("q2", 'd', "q1");
        equiv1.addTransition("q3", 'c', "q2");
        equiv1.addTransition("q3", 'd', "q3");
        Dfa equiv2 = new Dfa();
        equiv2.setInitialState("q4");
        equiv2.addState("q4", true);
        equiv2.addState("q5", false);
        equiv2.addState("q6", false);
        equiv2.addState("q7", false);
        equiv2.addTransition("q4", 'c', "q4");
        equiv2.addTransition("q4", 'd', "q5");
        equiv2.addTransition("q5", 'c', "q6");
        equiv2.addTransition("q5", 'd', "q4");
        equiv2.addTransition("q6", 'c', "q7");
        equiv2.addTransition("q6", 'd', "q6");
        equiv2.addTransition("q7", 'c', "q6");
        equiv2.addTransition("q7", 'd', "q4");

        assertTrue(equiv1.equivalent(equiv2));
    }
}