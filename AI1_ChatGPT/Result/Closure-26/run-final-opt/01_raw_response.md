TARGETS: process(Node,Node); toModuleName(String); CommonJS require/exports processing and script handling.
ORACLES: Existing named trigger tests are the sole expected-result source in this context.
CASES: Module-name conversion, including filenames with dashes and default filename-prefix normalization.
CASES: exports handling and scripts without exports.
CASES: require processing and variable renaming during CommonJS transformation.
CASES: CommandLineRunner processing of CJS and combined AMD-to-CJS transformation.
RISKS: No source bodies, fixtures, or asserted output strings are provided; avoid assuming exact rewrites.