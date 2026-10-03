TARGETS: unixTimeToZipLong, dateToZipLong; setModifyJavaTime/getModifyJavaTime round-trip; similarly
for access and create times.
ORACLES: Expected from COMPRESS-416 fix: no longer reject timestamps >32-bit signed with "Time too
big for 32 bits!"; must handle up to unsigned 32-bit.
ORACLES: Verify that setModifyJavaTime for a far-future date (e.g., year 2100) no longer throws nor
fails assertion on retrieval.
CASES: Normal round-trip: setModifyJavaTime(new Date(1_500_000_000_000L)) →
getModifyJavaTime().equals(originalDate).
CASES: Boundary: setModifyJavaTime(new Date(0x7FFFFFFF_000L)) (2038-01-19T03:14:07 max signed
32-bit+1 sec); should succeed and retrieve equal.
CASES: Boundary: setModifyJavaTime(new Date(Long.MAX_VALUE)) – should not throw; subsequent
getModifyTime()/getModifyJavaTime behavior.
CASES: Zero flags: setFlags(0) then setModifyJavaTime(…) and getModifyJavaTime() return null; no
crash.
CASES: Null inputs: setModifyTime(null) (maybe NPE or store null); getModifyJavaTime after null
internal state.
RISKS: Cannot see fixed code; may only need to suppress or refactor unixTimeToZipLong validation;
risk of silent overflow if unsigned >0xFFFFFFFF.
RISKS: Limited to public API; cannot test private unixTimeToZipLong directly; trigger test not
supplied.