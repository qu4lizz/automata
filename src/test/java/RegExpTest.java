import qu4lizz.automata.exception.*;
import org.junit.jupiter.api.Test;
import qu4lizz.automata.regex.RegExp;

import static org.junit.jupiter.api.Assertions.*;

class RegExpTest {

    @Test
    void equivalent() throws InvalidRegexException, StateExistsException {
        RegExp equivReg11 = new RegExp("(0110|01)(10)*");
        RegExp equivReg12 = new RegExp("01(10)*");
        assertTrue(equivReg11.equivalent(equivReg12));

        RegExp equivReg21 = new RegExp("(ε|b)a*(b|bba*)*");
        RegExp equivReg22 = new RegExp("b*(a|bb|bbb)*b*");
        assertTrue(equivReg21.equivalent(equivReg22));

        RegExp equivReg31 = new RegExp("(ab|b*a*)");
        RegExp equivReg32 = new RegExp("(a|b*)(a*|b)");
        assertTrue(equivReg31.equivalent(equivReg32));
    }
}