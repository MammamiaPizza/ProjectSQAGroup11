TARGETS: HelpFormatter: printUsage/appendOption usage-string construction; Option.getArgName;
OptionBuilder.withArgName stores custom argName
ORACLES: HelpFormatterTest.testDefaultArgName expects "-f <argument>" for Option built with
withArgName("argument")
CASES: custom argName vs default "arg"; hasArg=false options omit arg placeholder; optionalArg
display; null/empty argName fallback to default
RISKS: only single Options path tested; other render/print methods may still use hardcoded "arg";
argName propagation to long-opt usage untested