TARGETS: setKey(String) and Attribute(String,String,String) — validate key is not empty.
ORACLES: Expect IllegalArgumentException for empty key; existing doc says "must not be null", bug
extends to "not empty".
CASES: setKey("") → IAE; setKey("a") → success; setKey(null) → IAE; new Attribute("","v") → IAE;
parent update after setKey.
RISKS: Whitespace-only (" ") boundary unknown; unable to confirm fixed version behavior; guidance
from trigger test only.