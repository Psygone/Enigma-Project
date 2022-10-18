package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author jordanta
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        char letter =  super.alphabet().toChar(setting());
        return _notches.indexOf(letter)  > -1;
    }

    @Override
    void advance() {
        int size = super.size();
        set((getSetting() + 1) % size);
    }

    /** Returns the notches. */
    @Override
    String notches() {
        return _notches;
    }

    /** Gets the notches. */
    private String _notches;
}
