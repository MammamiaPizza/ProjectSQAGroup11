TARGETS: CodeGenerator expression printing for object-literal values in conditional statement bodies.  
TARGETS: isOneExactlyFunctionOrDo(Node) and non-empty-child helpers may govern required braces.  
ORACLES: Existing CodePrinterTest exact output for Safari function and IE do-loop compatibility.  
CASES: if(e1){A:function goo(){return true}} must retain braces around function-valued object literal.  
CASES: if(x){A:do foo();while(y)} must retain braces around do-loop-valued object literal.  
CASES: Verify generated output distinguishes required block braces from bare object-literal text.  
RISKS: Context is limited to two trigger comparisons and partial private signatures; no broader rules inferred.