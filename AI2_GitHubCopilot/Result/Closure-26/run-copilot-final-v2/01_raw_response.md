TARGETS: toModuleName(String filename) mapping; visitRequireCall rewriting; visitModuleExports
handling; visitScript processing; SuffixVarsCallback suffix addition
ORACLES: Node.js CommonJS spec behavior for require/exports; existing trigger-test expected outputs;
compiler output consistency
CASES: normal relative paths, dash-in-name, empty exports, var renaming in exports, AMD+CJS combined
flow, boundary: empty/null filename, absolute paths
RISKS: no access to expected-output string diffs; File.separator may cause OS-dependent failures;
limited to public API shown; need to infer bug from test names only