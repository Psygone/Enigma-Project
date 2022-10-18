package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Scanner;
import java.util.List;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.ArrayList;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author jordanta
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        String message = "";
        Machine mach = readConfig();
        try {
            while (_input.hasNext()) {
                String input = _input.nextLine();
                while (input == "") {
                    _output.print("\n");
                    input = _input.nextLine();
                }
                setUp(mach, input);
                try {
                    while (_input.hasNext(
                            "[" + _alphabet.getChars() + "]+")) {
                        message = mach.convert(_input.nextLine()
                                .replaceAll("\\s+", ""));
                        printMessageLine(message);
                    }
                } catch (NoSuchElementException e) {
                    throw error("Throw exception");
                }
            }
        } catch (EnigmaException e) {
            throw new EnigmaException("No config");
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            Collection<Rotor> allRotors = new ArrayList<>();
            _alphabet = new Alphabet(_config.nextLine());
            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();
            while (_config.hasNext("[^*]+")) {
                Rotor r = readRotor();
                allRotors.add(r);
            }
            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next("[^*()]+");
            String typeNotches = _config.next("[A-Za-z0-9]+");
            char type = typeNotches.charAt(0);
            String notches = typeNotches.substring(1);
            String perm = "";
            while (_config.hasNext("\\([^*]*\\)")) {
                perm += _config.next("\\([^*]*\\)");
            }
            Permutation p = new Permutation(perm, _alphabet);
            if (type == 'M') {
                return new MovingRotor(name, p, notches);
            } else if (type == 'R') {
                return new Reflector(name, p);
            } else {
                return new FixedRotor(name, p);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner s = new Scanner(settings);
        String[] rotors = new String[M.getNumRotors()];
        try {
            s.next("\\*");
        } catch (NoSuchElementException e) {
            throw new EnigmaException("No config");
        }
        for (int i = 0; i < M.getNumRotors(); ++i) {
            String temp = s.next("[^()\\s]+");
            rotors[i] =  temp;
        }
        M.insertRotors(rotors);
        String setting = s.next("[^*()]+");
        String cycle = "";
        if (s.hasNext()) {
            try {
                while (s.hasNext("\\(.*\\)")) {
                    cycle += s.next("\\(.*\\)");
                }
            } catch (NoSuchElementException e) {
                throw new EnigmaException("Wrong settings");
            }
        }
        Permutation plug = new Permutation(cycle, _alphabet);
        M.setRotors(setting);
        M.setPlugboard(plug);
    }


    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String newMsg = "";
        for (int i = 0; i < msg.length(); ++i) {
            if (i % 5 == 0 && i != 0) {
                newMsg += " ";
            }
            newMsg += msg.charAt(i);
        }
        newMsg += "\n";
        _output.print(newMsg);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
