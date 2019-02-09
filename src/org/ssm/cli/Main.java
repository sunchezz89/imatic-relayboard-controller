package org.ssm.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.ssm.control.BoardController;
import org.ssm.control.RelayState;

import java.io.IOException;

/**
 * Main-entrypoint for command line usage.
 * Interprets command line arguments and instantiate the @{@link BoardController} with given parameters.
 * Also responsible for commandline result output.
 */
public class Main {

    private static String version = "1.0";

    public static void main(String[] args) {
        PropertyHolder parameter = new PropertyHolder();
        CmdLineParser parser = new CmdLineParser(parameter);
        try {
            // Get Parameter and put into parameter
            parser.parseArgument(args);
            runApi(parameter);
        } catch (CmdLineException e) {
            // handling of wrong arguments
            System.err.println(e.getMessage());
            System.out.println("\n----- Relay Controller v" + version + "-----\n");
            parser.printUsage(System.err);
        } catch (Exception e) {
            if (parameter.debug) {
                e.printStackTrace();
            } else {
                System.err.println("Error: " + e.getMessage());
            }
            System.exit(1);
        }
    }

    private static void runApi(PropertyHolder properties) throws IOException {

        // Create the instance of the BoardController with the given parameter
        BoardController controller = new BoardController(properties.host, properties.port);

        // connect to the board, with timeout given.
        controller.connect(properties.timeout);

        RelayState[] result;
        switch (properties.action) {
            case "set-all":
                // sets and retrieve the states for all relays and print
                result = controller.setAllRelay(properties.state);
                printRelayStates(result);
                break;
            case "get-all":
                // retrieve the states and print
                result = controller.getAllStates();
                printRelayStates(result);
                break;
            case "set-state":
                // Set the relays to the given state
                controller.setRelay(properties.relaynumber, properties.state);

                // retrieve the states for given relays and print
                result = controller.getRelayStates(properties.relaynumber);
                printRelayStates(properties.relaynumber, result);
                break;
            case "get-state":
                // retrieve the states for given relays and print
                result = controller.getRelayStates(properties.relaynumber);
                printRelayStates(properties.relaynumber, result);
                break;
            case "toggle-state":
            case "toggle":
                // retrieve the state of every given relay, and sets the opposite.
                result = controller.toggleRelay(properties.relaynumber);
                printRelayStates(properties.relaynumber, result);
                break;

        }
    }

    private static void printRelayStates(RelayState[] states) {
        for (int i = 0; i < states.length; i++) {
            printRelayState((i + 1), states[i]);
        }
    }

    private static void printRelayStates(int[] numbers, RelayState[] states) {
        for (int i = 0; i < states.length; i++) {
            printRelayState((numbers[i] + 1), states[i]);
        }
    }

    private static void printRelayState(int number, RelayState state) {
        System.out.println(number + ": " + state);
    }
}