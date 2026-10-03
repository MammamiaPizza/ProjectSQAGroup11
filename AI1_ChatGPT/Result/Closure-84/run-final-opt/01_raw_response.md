TARGETS: IRFactory transformation of destructuring-assignment syntax during parsing  
ORACLES: ParserTest::testDestructuringAssignForbidden4 and bug report 215 define expected rejection  
CASES: Forbidden destructuring assignment matching the trigger should produce the asserted parse failure  
CASES: Nearby valid syntax and boundary placement should distinguish allowed parsing from forbidden assignment  
RISKS: IRFactory APIs shown are private; parser entry points and exact expected diagnostic are unavailable  
RISKS: No patch/diff or alternate version is provided, limiting localization of the faulty transformation