package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void testSize() {
        Alphabet alpha1 = new Alphabet(
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        Alphabet alpha2 = new Alphabet("");
        Permutation perm1 = new Permutation(
                "(QWERTYUIOPASDFGHJKLZXCVBNM)", alpha1);
        Permutation perm2 = new Permutation("", alpha2);
        assertEquals(26, perm1.size());
        assertEquals(0, perm2.size());
    }

    @Test
    public void testPermInt() {
        Alphabet alpha1 = new Alphabet(
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        Alphabet alpha2 = new Alphabet("A");
        Alphabet alpha3 = new Alphabet("NEWPHO");
        Permutation perm1 = new Permutation(
                "(QWERTYUIOPASDFGHJKLZXCVBNM)", alpha1);
        Permutation perm2 = new Permutation("(A)", alpha2);
        Permutation perm3 = new Permutation("(NEW) (PHO)", alpha3);
        assertEquals(18, perm1.permute(0));
        assertEquals(23, perm1.permute(25));
        assertEquals(0, perm2.permute(0));
        assertEquals(4, perm3.permute(3));
    }

    @Test
    public void testPermChar() {
        Alphabet alpha1 = new Alphabet();
        Permutation perm1 = new Permutation(
                "(QWERTYUIOPASDFGHJKLZXCVBNM)", alpha1);
        Permutation perm2 = new Permutation(
                "(ABCD)", new Alphabet("RTAV"));
        assertEquals('S', perm1.permute('A'));
        assertEquals('Q', perm1.permute('M'));
        assertEquals('R', perm2.permute('R'));
    }

    @Test
    public void testInvertInt() {
        Alphabet alpha1 = new Alphabet();
        Permutation perm1 = new Permutation(
                "(QWERTYUIOPASDFGHJKLZXCVBNM)", alpha1);
        assertEquals(15, perm1.invert(0));
        assertEquals(11, perm1.invert(25));
    }

    @Test
    public void testInvertChar() {
        Alphabet alpha1 = new Alphabet();
        Permutation perm1 = new Permutation(
                "(QWERTYUIOPASDFGHJKLZXCVBNM)", alpha1);
        assertEquals('P', perm1.invert('A'));
    }

    @Test
    public void testAlpha() {
        Alphabet alpha1 = new Alphabet();
        Permutation perm1 =  new Permutation(
                "(QWERTYUIOPASDFGHJKLZXCVBNM)", alpha1);
        String str = "";
        for (int i = 0; i < alpha1.size(); i++) {
            str += alpha1.toChar(i);
        }
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", str);
    }

    @Test
    public void testDerangement() {
        Alphabet alpha1 =  new Alphabet();
        Permutation perm1 = new Permutation(
                "(QWERTYUIOPASDFGHJKLZXCVBNM)", alpha1);
        Permutation perm2 =  new Permutation(
                "(W) (NE)", new Alphabet("NEW"));
        assertTrue(perm1.derangement());
        assertFalse(perm2.derangement());
    }

}
