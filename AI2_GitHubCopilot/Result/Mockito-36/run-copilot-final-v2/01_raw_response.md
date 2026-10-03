TARGETS: Invocation.callRealMethod() – detect interface mock, throw meaningful exception
ORACLES: InvocationTest.shouldScreamWhenCallingRealMethodOnInterface expects scream, not NPE/NSME
CASES: callRealMethod on interface mock w/o impl, spy on interface, concrete mock, null RealMethod
CASES: boundary: zero-arg, varargs, primitive-return, toString on interface
RISKS: Cannot see current callRealMethod() source; fix might break normal spying on classes
RISKS: Java 8+ default interface methods may need separate handling