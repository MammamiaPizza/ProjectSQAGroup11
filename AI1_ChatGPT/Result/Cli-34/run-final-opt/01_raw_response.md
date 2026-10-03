TARGETS: Option.setType/getType; OptionBuilder.withType/create state transfer and reset  
ORACLES: Trigger expectations: parsed option value "foo"; consecutive built option type is String.class  
CASES: Build typed option via withType(String.class), create it, then build/create a second complete option  
CASES: Parse an option typed String with value "foo"; verify parsed value is non-null and equals "foo"  
RISKS: OptionBuilder is static/stateful; test ordering or incomplete create/reset can leak configuration  
RISKS: Context lacks parser API/setup and precise type-conversion behavior beyond trigger expectations