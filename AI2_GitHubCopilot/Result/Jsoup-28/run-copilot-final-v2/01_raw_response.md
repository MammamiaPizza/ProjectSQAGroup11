TARGETS: Entities.unescape(String,boolean) strict/non-strict; Tokeniser charBuffer &
characterReferenceError; Parser parseInput
ORACLES: 6 test expected values: e.g. unescape expects "&angst", noSpuriousDecodes expects
"&num_rooms", strictAttributeUnescapes expects "&mid"
CASES: named entity with/without semicolon in text vs attribute; ambiguous prefix (amp vs clubsuit);
base entity (no semicolon) vs extended (require semicolon)
CASES: numeric entities (hex/dec); unknown entities left as-is; spurious decode in URL query params;
entity fragment at end of attribute
RISKS: correct behavior known only from 6 test expectations; spec not accessible; risk of
overfitting or missing edge cases beyond these triggers