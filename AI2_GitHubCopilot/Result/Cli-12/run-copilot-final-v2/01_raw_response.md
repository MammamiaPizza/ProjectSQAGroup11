TARGETS: GnuParser.flatten(Options,String[],boolean) — token splitting, '=' handling.
ORACLES: Expected CLI behavior: '=' separates option name from value; value must not include leading
'=' nor option name; test expectations match that.
CASES: short=value (e.g., "-D=bar"), long=value with single dash ("-foo=bar"), long=value with
double dash ("--foo=bar").
CASES: boundary: empty value after '=', equals at end, multiple '=' in argument, option missing
required value.
CASES: stopAtNonOption=true with '=' arguments; unrecognized option with '='.
RISKS: Cannot inspect actual flatten logic; interaction of '=' within quoted arguments or combined
short options unknown.