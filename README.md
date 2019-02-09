# iMatic Relayboard Controller
Java API for the SainSmart iMatic 16Ch RelayBoard

### Commandline Usage
For command line usage start the jar with the `java -jar` option.
Example: `java -jar webboard.jar -a get-all`.

This should give you output like this:
1. ON
2. OFF
3. OFF
... 

##### CLI Options

 --debug                  : Sets to debugmode and prints full stacktraces.
                            (default: false)
                            
 --timeout N              : Set connect timeout in milliseconds. (Vorgabe: 0)
 -a (--action) WERT       : Sets the action. Available actions:
                             set-all: set <state> to ALL relays on the board
                            (ignores -r)
                             get-all: print states of all relays to standard
                            output (ignores -r)
                             toggle-all: set the opposite state of the current
                            of specified relay(s) (ignores -r) (FUTURE VERSION)
                             get-state: get state of relay(s) specified in
                            --relay option (requires -r)
                             set-state: get state of relay(s) specified in
                            --relay option (requires -r & -s)
                             toggle|toggle-state: set the opposite state of the
                            current of specified relay(s) (requires -r)
                            (Vorgabe: get-all)
 -h (--host) WERT         : Sets the target host (IP, or qualified name).
                            (Vorgabe: 192.168.178.1)
 -p (--port) N            : Sets the target port of the board. (Vorgabe: 3000)
 -r (--relay, --relays) N : Set the number of relay(s) to interact with. Like
                            '1,4,6' (Vorgabe: 0)
 -s (--state) [ON | OFF]  : Set the desired state for targeted relays.
                            (Vorgabe: ON)