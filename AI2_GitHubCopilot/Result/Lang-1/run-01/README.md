# GitHub Copilot — Lang-1, run-01

- Source supplied to Copilot: Lang-1b only.
- Prompt 02 generated 29 JUnit tests. The raw response and raw test class are preserved.
- Test class was renamed for execution to avoid colliding with the project's existing NumberUtilsTest.
- One assertEquals argument needed a type adjustment to compile; the asserted numeric value was unchanged.
- Before repair: Lang-1b failed 5/29 tests; Lang-1f failed 3/29 tests.
- Prompt 03 repaired the generated suite. The complete response and execution copy are preserved.
- After repair: Lang-1b failed 5/29 tests; Lang-1f passed 29/29 tests.
- The eight-digit hexadecimal test fails on Lang-1b and passes on Lang-1f, detecting LANG-747.
- Repaired-suite coverage on Lang-1f: 334/380 lines (87.9%) and 241/350 conditions (68.9%); 29/29 tests passed.
- Repaired-suite coverage on Lang-1b: 328/375 lines (87.5%) and 231/338 conditions (68.3%); 5/29 tests failed.

## Prompt 04 — Extend coverage

Copilot preserved the 29 repaired tests and added 8 tests (37 total). Its raw
extended suite had one incorrect octal assertion: createBigInteger("012")
equals decimal 10, while the generated test expected decimal 8. The raw
response, raw extended code, and failing logs are preserved. A separate
execution copy corrects the expected value to BigInteger("12", 8).

| Suite | Lang-1f | Lang-1b |
| --- | --- | --- |
| 29 repaired tests | 29 passed; 87.9% lines, 68.9% conditions | 5 failed; 87.5% lines, 68.3% conditions |
| 37 extended tests, corrected octal assertion | 37 passed; 95.3% lines, 83.1% conditions | 3 failed; 94.9% lines, 82.5% conditions |

The Lang-1b failures include the eight-digit hexadecimal LANG-747 case.
Coverage is measured separately on each version because their total line and
condition counts differ.
