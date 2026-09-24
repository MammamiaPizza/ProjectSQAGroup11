# GitHub Copilot — Lang-1, run-01

- Source supplied to Copilot: Lang-1b only.
- Prompt 02 generated 29 JUnit tests. The raw response and raw test class are preserved.
- Test class was renamed for execution to avoid colliding with the project's existing NumberUtilsTest.
- One assertEquals argument needed a type adjustment to compile; the asserted numeric value was unchanged.
- Before repair: Lang-1b failed 5/29 tests; Lang-1f failed 3/29 tests.
- Prompt 03 repaired the generated suite. The complete response and execution copy are preserved.
- After repair: Lang-1b failed 5/29 tests; Lang-1f passed 29/29 tests.
- The eight-digit hexadecimal test fails on Lang-1b and passes on Lang-1f, detecting LANG-747.
- Coverage has not yet been measured for the repaired suite.
