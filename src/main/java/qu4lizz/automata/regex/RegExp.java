package qu4lizz.automata.regex;

import qu4lizz.automata.automaton.Dfa;
import qu4lizz.automata.automaton.Nfa;
import qu4lizz.automata.exception.StateExistsException;
import qu4lizz.automata.exception.InvalidRegexException;
import qu4lizz.automata.exception.WordLengthException;
import qu4lizz.automata.state.NfaState;

import java.util.Stack;

public class RegExp {
    String regex;

    public RegExp(String str) {
        regex = str;
    }
    private static boolean alpha(char c){ return c >= 'a' && c <= 'z';}
    private static boolean validChar(char c){ return alpha(c) || c == NfaState.EPSILON || Character.isDigit(c);}
    private static boolean regexOperator(char c){
        return c == '(' || c == ')' || c == '*' || c == '|';
    }

    /**
     * Checks if character is valid for RegEx.
     * @param c character to check
     * @return true if character is valid
     */
    public static boolean validRegExChar(char c){
        return validChar(c) || regexOperator(c);
    }
    private boolean validRegEx(){
        if (regex.isEmpty())
            return false;
        for (char c : regex.toCharArray())
            if (!validRegExChar(c))
                return false;
        return true;
    }

    /**
     * Transforms RegEx to NFA.
     * Complexity: O(n * m * {@link Nfa#concatForRegex(Nfa, Nfa) Concat}) -
     * n is number of characters of expression, m number of operands, and it is
     * multiplied with complexity of function
     * @return NFA representation of RegEx
     * @throws InvalidRegexException if there is invalid character in regex
     */
    public Nfa toNfa() throws InvalidRegexException, StateExistsException {
        if (!validRegEx())
            throw new InvalidRegexException("Invalid regex");

        Stack<Character> operators = new Stack <> ();
        Stack<Nfa> operands = new Stack <> ();
        Stack<Nfa> concat_stack = new Stack <> ();
        boolean concatFlag = false;
        char op, c; // current character of string
        int para_count = 0;

        for (int i = 0; i < regex.length(); i++){
            c = regex.charAt(i);
            if (validChar(c)) {
                operands.push(new Nfa(c));
                if (concatFlag)
                    operators.push('.'); // '.' used to represent concat.
                else
                    concatFlag = true;
            }
            else{
                if (c == ')'){
                    concatFlag = false;
                    if (para_count == 0)
                        throw new InvalidRegexException("Not enough operands");
                    else{ para_count--;}
                    // process stuff on stack till '('
                    while (!operators.empty() && operators.peek() != '('){
                        process(operators, operands, concat_stack);
                    }
                }
                else if (c == '*') {
                    operands.push(Nfa.kleeneForRegex(operands.pop()));
                    concatFlag = true;
                }
                else if (c == '(') { // if any other operator: push
                    if(!operators.empty())
                        operators.push('.');
                    operators.push(c);
                    para_count++;
                    concatFlag = false;
                }
                else if (c == '|') {
                    operators.push(c);
                    concatFlag = false;
                }
            }
        }
        while (operators.size() > 0) {
            if (operands.empty())
                throw new InvalidRegexException("Not enough operands");
            process(operators, operands, concat_stack);
        }
        return operands.pop();
    }

    private void process(Stack<Character> operators, Stack<Nfa> operands, Stack<Nfa> concat_stack) throws StateExistsException {
        char op;
        Nfa nfa2;
        Nfa nfa1;
        op = operators.pop();
        if (op == '.'){
            nfa2 = operands.pop();
            nfa1 = operands.pop();
            operands.push(Nfa.concatForRegex(nfa1, nfa2));
        }
        else if (op == '|'){
            nfa2 = operands.pop();

            if(!operators.empty() && operators.peek() == '.') {
                concat_stack.push(operands.pop());
                while (!operators.empty() && operators.peek() == '.'){
                    concat_stack.push(operands.pop());
                    operators.pop();
                }
                nfa1 = Nfa.concatForRegex(concat_stack.pop(),
                        concat_stack.pop());
                while (concat_stack.size() > 0){
                    nfa1 =  Nfa.concatForRegex(nfa1, concat_stack.pop());
                }
            }
            else {
                nfa1 = operands.pop();
            }
            operands.push(Nfa.unionForRegex(nfa1, nfa2));
        }
    }

    public Nfa concatenation(RegExp other) throws StateExistsException, InvalidRegexException {
        Nfa nfa1, nfa2;
        nfa1 = toNfa();
        nfa2 = other.toNfa();
        return nfa1.concatenation(nfa2);
    }

    public Dfa union(RegExp other) throws StateExistsException, InvalidRegexException {
        Nfa nfa1, nfa2;
        nfa1 = toNfa();
        nfa2 = other.toNfa();
        return nfa1.union(nfa2);
    }

    public Dfa intersection(RegExp other) throws StateExistsException, InvalidRegexException {
        Nfa nfa1, nfa2;
        nfa1 = toNfa();
        nfa2 = other.toNfa();
        return nfa1.intersection(nfa2);
    }

    public Dfa difference(RegExp other) throws StateExistsException, InvalidRegexException {
        Nfa nfa1, nfa2;
        nfa1 = toNfa();
        nfa2 = other.toNfa();
        return nfa1.difference(nfa2);
    }

    public Dfa complement() throws InvalidRegexException, StateExistsException {
        Nfa nfa;
        nfa = toNfa();
        return nfa.complement();
    }

    public int shortestWord() throws StateExistsException, WordLengthException, InvalidRegexException {
        Nfa nfa;
        nfa = toNfa();
        return nfa.shortestWord();
    }

    public int longestWord() throws StateExistsException, WordLengthException, InvalidRegexException {
        Nfa nfa;
        nfa = toNfa();
        return nfa.longestWord();
    }
    public boolean equivalent(Object o) throws StateExistsException, InvalidRegexException {
        if (o == this)
            return true;

        if (!(o instanceof RegExp))
            return false;

        Nfa nfa1 = this.toNfa();
        Nfa nfa2 = ((RegExp)o).toNfa();
        return nfa1.equivalent(nfa2);
    }
}
