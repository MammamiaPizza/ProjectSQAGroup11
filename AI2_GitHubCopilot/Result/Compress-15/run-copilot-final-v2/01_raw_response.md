TARGETS: ZipArchiveEntry.equals(Object) comment/name comparison; copy constructors preserving
equality.
ORACLES: testNullCommentEqualsEmptyComment expects null comment == empty comment; Object
equals/hashCode contract/symmetry.
CASES: null vs empty comment; both null; both empty; equal non-null comments; unequal comments; same
name only.
CASES: different names; self; null; other class; clone/copy equals original; hashCode consistency
for equal entries.
RISKS: inherited ZipEntry.getComment behavior is assumed; expected behavior only from trigger test
and equals contract.