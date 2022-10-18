package enigma;

import java.util.HashMap;
import java.util.Collection;


/** Class that represents a complete enigma machine.
 *  @author jordanta
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _allRotorsMap = new HashMap<String, Rotor>();
        for (Rotor r:_allRotors) {
            String name = r.name();
            _allRotorsMap.put(name, r);
        }
        _slots = new Rotor[numRotors];
        _plugboard = null;
    }


    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _slots[k];
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; ++i) {
            if (_allRotorsMap.get(rotors[i]) == null) {
                throw new EnigmaException("Bad rotor name");
            }
            for (int j = 0; j < i; j++) {
                if (_allRotorsMap.get(rotors[i]).name()
                        == _allRotorsMap.get(rotors[j]).name()) {
                    throw new EnigmaException("Duplicate rotor names");
                }
            }
            _slots[i] = _allRotorsMap.get(rotors[i]);
        }
        if (!(_slots[0] instanceof Reflector)) {
            throw new EnigmaException("Reflector in wrong place");
        }

    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw new EnigmaException("Incorrect number of settings");
        }
        char[] etting = setting.toCharArray();
        String a = _alphabet.getChars();
        for (int i = 1; i < _numRotors; i++) {
            int index = a.indexOf(etting[i - 1]);
            _slots[i].set(index);
        }
        if (!(_slots[0] instanceof Reflector)) {
            throw new EnigmaException("First object not a reflector");
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        boolean[] moveRotors = new boolean[_numRotors];
        for (int i = 0; i < _numRotors; ++i) {
            if (_slots[i].atNotch() && _slots[i - 1].rotates()) {
                moveRotors[i] = true;
                moveRotors[i - 1] = true;
            } else if (i == _numRotors - 1) {
                moveRotors[i] = true;
            }
        }
        for (int i = 0; i < _numRotors; ++i) {
            if (moveRotors[i]) {
                _slots[i].advance();
            }
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        int result = _slots[_numRotors - 1].convertForward(c);
        for (int i = _numRotors - 2; i >= 0; --i) {
            result = _slots[i].convertForward(result);
        }
        for (int j = 1; j < _numRotors; ++j) {
            result = _slots[j].convertBackward(result);
        }
        return result;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        char letter;
        int index;
        for (int i = 0; i < msg.length(); ++i) {
            letter = msg.charAt(i);
            index = _alphabet.toInt(letter);
            index = convert(index);
            letter = _alphabet.toChar(index);
            result += letter;
        }
        return result;
    }

    /** Returns the number of rotors. */
    int getNumRotors() {
        return _numRotors;
    }

    /** Returns the number of pawls. */
    int getPawls() {
        return _pawls;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors. */
    private int _numRotors;

    /** Number of pawls. */
    private int _pawls;

    /** Collection of the rotors. */
    private Collection<Rotor> _allRotors;

    /** Hashmap version of the rotors. */
    private HashMap<String, Rotor> _allRotorsMap;

    /** Slots for the rotors. */
    private Rotor[] _slots;

    /** Plugboard cycles. */
    private Permutation _plugboard;
}
