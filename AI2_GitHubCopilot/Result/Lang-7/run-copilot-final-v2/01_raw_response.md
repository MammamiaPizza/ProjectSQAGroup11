TARGETS: createNumber(String) hex (0x/0X) prefix validation, NumberFormatException throwing
ORACLES: NumberFormatException for empty hex body per LANG-822; testCreateNumber asserts exception
CASES: Normal: "0x1A","-0.5","123" ; Boundary: "0x","0X","0x0","0x-1","+0x"
CASES: Error: null, "", "noNum", "--1"
RISKS: Exact invalid inputs not fully known; testCreateNumber details unavailable; other prefix bugs
possible