package qu4lizz.automata.dfa_code_generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import qu4lizz.automata.automaton.*;
import qu4lizz.automata.loader.LanguageSpecification;
import qu4lizz.automata.state.DfaState;


/**
 * Code generating for Java.
 * @author Miroslav Bratic &lt;<a href="miroslavbratic00@gmail.com">miroslavbratic00@gmail.com</a>&gt;
 */
public class DfaCodeGenerator {
    private StringBuilder code = new StringBuilder();

    /**
     * Loads DFA specification from file and generates finite state
     * machine code for execution of it, where linking of actions
     * can be specified on exit, on enter of each state and on transition.
     * Complexity: O(n * m) where n is number of states and m is number of symbols in the alphabet
     * @param javaOut path of an output file (.java or .txt)
     * @param dfa DFA to be generated
     */
    public void generate(String javaOut, Dfa dfa) {
        if (dfa == null)
            return;
        code.append("import java.util.ArrayList;\n\n");

        code.append("public class Dfa {\n");
        code.append("\tpublic boolean run(String input, Action onExit, Action onEnter, ");
        for (int i = 0; i < dfa.getAlphabet().size(); i++) {
            var symbol = dfa.getAlphabet().get(i);
            if (i == dfa.getAlphabet().size() - 1)
                code.append("Action onTransition_").append(symbol).append(") throws Exception {\n");
            else
                code.append("Action onTransition_").append(symbol).append(", ");
        }

        code.append("\t\tString currentState = \"").append(dfa.getInitialStateId()).append("\";\n");
        code.append("\t\tfor (char symbol : input.toCharArray()) {\n");
        code.append("\t\t\tswitch(currentState) {\n");

        for (var state : dfa.getStates()) {
            code.append("\t\t\t\tcase \"").append(state.getId()).append("\":\n");
            code.append("\t\t\t\t\tonExit.action(currentState);\n");
            for (int i = 0; i < dfa.getAlphabet().size(); i++) {
                var symbol = dfa.getAlphabet().get(i);
                if (i == 0)
                    code.append("\t\t\t\t\tif (symbol == '").append(symbol).append("') {\n");
                else
                    code.append("\t\t\t\t\telse if (symbol == '").append(symbol).append("') {\n");
                code.append("\t\t\t\t\t\tonTransition_").append(symbol).append(".action(currentState);\n");
                code.append("\t\t\t\t\t\tcurrentState = \"").append(state.getTransition(symbol).getId()).append("\";\n");
                code.append("\t\t\t\t\t}\n");
            }
            code.append("\t\t\t\t\telse {\n");
            code.append("\t\t\t\t\t\tthrow new Exception();\n");
            code.append("\t\t\t\t\t}\n");

            code.append("\t\t\t\t\tonEnter.action(currentState);\n");
            code.append("\t\t\t\t\tbreak;\n");
        }
        code.append("\t\t\t}\n");
        code.append("\t\t}\n");

        Vector<DfaState> finalStates = dfa.finalStates();
        code.append("\t\treturn ");
        if (finalStates.isEmpty())
            code.append("false");
        else {
            for (var state : finalStates) {
                if (state == finalStates.lastElement())
                    code.append("currentState.equals(\"").append(state.getId()).append("\");\n");
                else
                    code.append("currentState.equals(\"").append(state.getId()).append("\") || ");
            }
        }
        code.append("\t}\n");
        code.append("}\n\n");
        // Action class
        code.append("class Action {\n");
        code.append("\tpublic ArrayList<Lambda<String>> actions = new ArrayList<>();\n");
        code.append("\tpublic void action (String state) {\n");
        code.append("\t\tactions.forEach(act -> { act.operate(state); });\n");
        code.append("\t}\n");

        code.append("}\n\n");

        // Interface
        code.append("interface Lambda<T> {\n");
        code.append("\tvoid operate(T t);\n");
        code.append("}");

        File file = new File(javaOut);
        FileWriter fw;
        PrintWriter pw;
        try {
            fw = new FileWriter(file);
            pw = new PrintWriter(fw);
            pw.println(code.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
