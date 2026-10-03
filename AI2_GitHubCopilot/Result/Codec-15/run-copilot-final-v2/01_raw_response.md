TARGETS: getMappingCode(str,index) applies H/W rule: skip H/W and compare surrounding consonants'
codes.
TARGETS: soundex(String) uses getMappingCode to build encoding up to maxLength; map(char) maps via
US_ENGLISH_MAPPING.
ORACLES: US_ENGLISH_MAPPING_STRING; standard Soundex values: "Washington"→W252, "Ashcroft"→A261,
"Jackson"→J250, "Robert"→R163.
CASES: H/W as separator between same-code consonants ("Ashcroft" A261, "Jackson" J250); H/W at
start/end ("Harry" H600, "Marsh" M620).
CASES: Multiple H/W ("Mawson" M250); H/W only ("H"→H000); empty string→""; single char→"A000"; max
length boundary via setMaxLength.
RISKS: Expected encoding relies on correct H/W rule in getMappingCode; other mapping bugs possible;
no alternative Soundex reference; static US_ENGLISH_MAPPING must remain unchanged.