TARGETS: GroupImpl parsing via optionMap.tailMap and anonymous argument handling
TARGETS: WriteableCommandLine.looksLikeOption(String) – critical for negative number detection
TARGETS: WriteableCommandLineImpl.addValue/getValues store parsed option-value pairs
ORACLES: CommandLine.hasOption(opt) && getValue(opt) returns expected negative, no OptionException
CASES: --num -42, --num=-42, --num -0, --num -3.14, multiple --num -1 -2, "-- -42" separator
CASES: Boundary: "-" alone; mixing option flags with negative numeric arguments
RISKS: optionMap.tailMap may falsely match "-42" as partial trigger; fix relies on looksLikeOption
RISKS: Unknown how newAnonymous list interacts with negative-value argument classification