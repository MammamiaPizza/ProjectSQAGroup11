TARGETS: Timer(long durationMillis), isCounting(), start(); indirect Mockito.timeout()/after() duration validation.
ORACLES: Trigger assertions require friendly failures for negative Timer, timeout, and after durations.
CASES: Timer(-1) throws expected reminder exception/message; test -1 boundary.
CASES: Mockito.timeout(-1) and Mockito.after(-1) throw expected negative-value exceptions/messages.
CASES: Timer(0) and positive durations preserve normal construction/counting behavior if observable.
RISKS: Context lacks Timer implementation, exception types, and exact behavior for zero/elapsed durations.