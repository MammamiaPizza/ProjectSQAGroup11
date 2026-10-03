TARGETS: flatten(Options,String[],boolean) – "--" delimiter handling when stopAtNonOption=true
TARGETS: burstToken(String,boolean) – splitting token that contains "--"
ORACLES: POSIX convention: "--" ends options; testGroovy expects remaining args, not "--"
ORACLES: testStopAtExpectedArg expects args after "--" returned as non-options
CASES: "--" mid-args (options before, non-options after), stopAtNonOption=true
CASES: "--" alone with stopAtNonOption=true; "--" at start of args; no "--" and stopAtNonOption=true
CASES: options before "--" with trailing non-options
RISKS: Only PosixParser context; internal gobble/processNonOptionToken logic unknown from supplied
summary
RISKS: Expected behavior derived only from trigger test failures; no spec beyond POSIX convention