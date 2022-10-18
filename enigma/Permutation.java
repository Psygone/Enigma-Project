package enigma;


import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author jordanta
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _clean = cycles.replace('(', ' ').replace(
                ')', ' ').replaceAll("\\s+", " ").trim();
        _cycles = _clean.split(" ");
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char letter = _alphabet.toChar(p % _alphabet.size());
        for (String cycle:_cycles) {
            if (cycle.indexOf(letter) > -1) {
                return _alphabet.toInt(cycle.charAt(
                        (cycle.indexOf(letter) + 1) % cycle.length()));
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char letter = _alphabet.toChar(c % _alphabet.size());
        for (String cycle : _cycles) {
            if (cycle.indexOf(letter) > -1) {
                if ((cycle.indexOf(letter) - 1) < 0) {
                    return _alphabet.toInt(cycle.charAt
                            (cycle.indexOf(letter) - 1 + cycle.length()));
                }
                return _alphabet.toInt(cycle.charAt(cycle.indexOf(letter) - 1));
            }
        }
        return c;
    }


    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        for (String cycle : _cycles) {
            if (cycle.indexOf(p) != -1) {
                return cycle.charAt((cycle.indexOf(p) + 1) % size());
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        for (String cycle : _cycles) {
            if (cycle.indexOf(c) != -1) {
                return cycle.charAt((cycle.indexOf(c) - 1) % size());
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (String cycle: _cycles) {
            if (cycle.length() == 1) {
                return false;
            }
        }
        for (int i = 0; i < _alphabet.size(); ++i) {
            if (_clean.indexOf(_alphabet.toChar(i)) == -1) {
                return false;
            }
        }
        return true;
    }

    /** Return the cycles. */
    String[] getCycles() {
        return _cycles;
    }

    /** Return the clean. */
    String getClean() {
        return _clean;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** List of the cycles. */
    private String[] _cycles;

    /** Clean version of cycles. */
    private String _clean;
}
