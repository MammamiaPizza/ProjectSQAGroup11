TARGETS: parseOctal(byte[],int,int) – parse octal, handle 0xff negative flag, trailing spaces/nulls
ORACLES: Round-trip via formatOctalBytes; octal→decimal manual conversion; expects IAE on invalid
bytes
CASES: Valid: '7', '777', '0', '777777777777' (12x'7'); negative: {0xff,'7','7','7'}; trailing
spaces: {'7',' ',' '}; invalid: {'8','9','a','z'}, empty buffer, offset out-of-range
RISKS: No spec for octal-to-long; round-trip oracle may hide bugs; negative flag handling may
interact with overflow; only public API available

That's 6 lines. Could add more details like boundary cases: max octal that fits in long (e.g.,
"777777777777777777777"?) but 12 digits is not max. Actually max octal digits for long: 2^63-1 in
octal is 7_777_777_777_777_777_777_777 (21 digits of 7? No, 2^63-1 = 9,223,372,036,854,775,807
decimal. Octal representation: 777777777777777777777 (21 digits of 7). So 12 digits is far smaller.
So boundary could be the max long representable. So mention: boundary: octal representation of
Long.MAX_VALUE (21 sept-digit? Actually 21 digits of 7: 7_777_777_777_777_777_777_777). But we don't
know if parseOctal supports that length. Could cause overflow if implementation uses naive
shift+add.

I'll add a CASES line for boundary. I'll keep it within 10 lines.

Final answer.TARGETS: parseOctal(byte[],int,int) – parse octal bytes to long, handle 0xff negative
flag, trailing spaces/nulls
ORACLES: Round-trip via formatOctalBytes; manual octal→decimal calculation; expect
IllegalArgumentException on invalid bytes
CASES: Normal: "7", "777", "0"; trigger: "777777777777" (12×'7'); negative: {0xff,'7','7','7'};
trailing spaces: {'7',' ',' '}; invalid chars: '8','9','a','z'; empty buffer; offset→length OOB
CASES: Boundary: octal representation near Long.MAX_VALUE (21 sept-digits? also "0");
overflow/underflow? No spec but test round-trip stability
RISKS: No official behavior spec; round-trip oracle with formatOctalBytes may mask bugs if both
methods share same defect
RISKS: Negative-flag (0xff) interaction with sign extension could produce unexpected exceptions or
wrong sign for large values