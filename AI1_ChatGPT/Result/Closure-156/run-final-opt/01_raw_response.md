TARGETS: CollapseProperties.process; alias inlining and namespace/property collapsing paths.  
ORACLES: Existing trigger assertions in CollapsePropertiesTest are the expected-result source.  
CASES: Aliased top-level enum collapse, including references through the alias.  
CASES: Issue389 scenario; preserve expected transformed output asserted by its existing test.  
CASES: Normal namespace declarations with descendant properties and alias-prefix flattening.  
RISKS: Private helpers require exercising via CompilerPass.process and compiler test harness.  
RISKS: Context lacks test source/input-output details; do not infer additional transformations.