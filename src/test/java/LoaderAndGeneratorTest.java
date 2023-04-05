import org.junit.jupiter.api.Test;
import qu4lizz.automata.automaton.*;
import qu4lizz.automata.dfa_code_generator.DfaCodeGenerator;
import qu4lizz.automata.exception.*;
import qu4lizz.automata.loader.LanguageSpecification;
import qu4lizz.automata.regex.RegExp;

import static org.junit.jupiter.api.Assertions.*;


public class LoaderAndGeneratorTest {
    @Test
    void dfaGeneratorAndLoaderTest() throws StateExistsException, WrongAlphabetException {
        String resourcePath = getClass().getClassLoader().getResource("dfa.txt").getPath();

        Dfa dfa = LanguageSpecification.loadSpecification(resourcePath);

        DfaCodeGenerator dfaCodeGenerator = new DfaCodeGenerator();
        dfaCodeGenerator.generate("Output.java", dfa);

        assertTrue(dfa.run("aaaaaaa"));
        assertTrue(dfa.run("baaaabab"));
    }

    @Test
    void nfaLoaderTest() throws StateExistsException {
        String resourcePath = getClass().getClassLoader().getResource("nfa.txt").getPath();

        Nfa nfa = LanguageSpecification.loadSpecification(resourcePath);

        assertTrue(nfa.run("a"));
        assertFalse(nfa.run("baaaabab"));
    }

    @Test
    void regexLoaderTest() throws StateExistsException, InvalidRegexException {
        String resourcePath = getClass().getClassLoader().getResource("regex.txt").getPath();

        RegExp regExp = LanguageSpecification.loadSpecification(resourcePath);

        Nfa fromReg = regExp.toNfa();

        assertTrue(fromReg.run("011010"));
        assertTrue(fromReg.run("01"));
    }
}
