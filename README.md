# iMatic Relayboard Controller
Java API for the SainSmart iMatic 16 Channel RelayBoard.

### BUGS
Feel free to create issues and open tickets, if something wents wrong, you have a feature Request, or need help.

### Commandline Usage
To use this API from Command line, you need Java. Download the jar-file from /bin/

For command line usage start the jar with the `java -jar` option in the folder where you put the Jar file.
The CLI is working with relay numbers from 1-16. Internally the API is working with typical numbers from 0-15.

##### Example set States
```
$ java -jar Relayboard-Controller.jar --action set-all --state ON
1. ON
2. ON
3. ON
4. etc.
16. ON
```
##### Example set State
```
$ java -jar Relayboard-Controller.jar --action set-state --state OFF -r 1
1. OFF  
```
##### Example set States
```
$ java -jar Relayboard-Controller.jar --action set-state --state ON -r 1,4
1. ON  
4. ON
```
##### Example toggle States
```
$ java -jar Relayboard-Controller.jar --action get-state -r 1,4
1. ON  
4. OFF
$ java -jar Relayboard-Controller.jar --action toggle -r 1,4
1. OFF  
4. ON
```

### CLI Options

    --debug                  : Sets to debugmode and prints full stacktraces. (default: false)
    --timeout N              : Set connect timeout in milliseconds. (Vorgabe: 0)
    -a (--action) WERT       : Sets the action. Available actions: (default: get-all)
          -      set-all: set <state> to ALL relays on the board  (ignores -r)
          -      get-all: print states of all relays to standard output (ignores -r)
          -      get-state: get state of relay(s) specified in --relay option (requires -r)
          -      set-state: get state of relay(s) specified in --relay option (requires -r & -s)
          -      toggle|toggle-state: set the opposite state of the specified relay(s) (requires -r)
          -      toggle-all: set the opposite state of the specified relay(s) (ignores -r) (FUTURE VERSION)
          -      touch: sets the relay(-r) to the given state(-s) for a given time(-t) (requires -r|s|t)
                            
    -h (--host) WERT         : Sets the target host (IP, or qualified name). (default: 192.168.178.1)
    -p (--port) N            : Sets the target port of the board. (default: 3000)
    -r (--relay, --relays) N : Set the number of relay(s) to interact with. Like '1,4,6' (Vorgabe: 0)                        
    -s (--state) [ON | OFF]  : Set the desired state for targeted relays. (default: ON)
