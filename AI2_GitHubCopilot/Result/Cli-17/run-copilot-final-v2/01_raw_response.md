TARGETS: burstToken(String,boolean) with stopAtNonOption flag; flatten() stop-at-non-option logic;
processOptionToken() handling after burst stop
ORACLES: verify args.getArgList().size() after parse; expected counts from manual reasoning based on
CLI-163 "extra arg: 2" vs expected 1
CASES: normal: burst short opts, --long opts, mixed; boundary: stopAtNonOption + burst token before
non-option; error: null Options, empty args, missing required opts
RISKS: only public args list observable; internal token list inaccessible; no spec beyond failing
test; correct expected may be ambiguous