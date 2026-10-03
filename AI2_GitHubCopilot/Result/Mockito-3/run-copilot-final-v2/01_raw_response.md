TARGETS: InvocationMatcher.matches(), safelyArgumentsMatch(), captureArgumentsFrom()
ORACLES: Vararg argument positions must be matched element-by-element, not array-vs-scalar
ORACLES: Argument count mismatch must not cause ArrayIndexOutOfBoundsException
ORACLES: Primitive byte varargs must not cause ClassCastException when capturing
CASES: Vararg method with 0 actual args vs 1+ matchers (boundary)
CASES: Multiple vararg invocations with different arg counts sharing same matchers
CASES: Primitive varargs (byte[]) captured via argument captor
RISKS: safelyArgumentsMatch does not expand varargs before indexing into actualArgs
RISKS: captureArgumentsFrom casts primitive array to wrong wrapper type (byte[] vs Byte)