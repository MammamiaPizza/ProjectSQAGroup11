TARGETS: HtmlTreeBuilderState handling of start token "a"/"A" in InBody; uses Tag equality or string
compare that may fail on case.
TARGETS: HtmlTreeBuilder.isSameFormattingElement, replaceInQueue, insertNode; case-sensitive
equality may break formatting-element tracking.
TARGETS: Element.tagName(String) applies parse settings; Tag.valueOf may not normalise case, causing
"A" ≠ "a" for nesting rules.
ORACLES: Expected (test assertion): <A> ONE </A> <A> Two </A> — outer <A> must close before inner
<A> regardless of tag‑name case.
ORACLES: HTML spec: <a> elements cannot nest; parser must implicitly close an open <a> when
encountering a new start tag for "a".
CASES: Normal: <a>one<a>two</a>three</a> (lowercase). Boundary: mixed‑case input
<A>one<a>two</a>three</A>.
CASES: Error: <A>one<A>two</A></A>, with attributes, empty <a></a><a></a>, deep nesting
<a><A><a>text</a></A></a>.
CASES: Parser configure: ParseSettings.preserveCase vs lowercase; verify nesting is identical
despite case preservation.
RISKS: HtmlTreeBuilderState constants likely hardcode lowercase tag‑name arrays; missing case
normalisation before match.
RISKS: Tag.equals may be case‑sensitive; combined with preserved case yields two distinct "A" and
"a" breaking nesting enforcement.