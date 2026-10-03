TARGETS: HtmlParser handling of nested <A>/<a> when tag case is preserved; Element/Tag name comparison behavior.
ORACLES: Existing trigger expected HTML: <A> ONE </A> <A> Two </A>.
CASES: Parse preserved-case nested links; verify outer link closes before second link opens.
CASES: Exercise uppercase, lowercase, and mixed-case A start/end tags under preserved-case settings.
RISKS: Available context omits parser entry-point/settings signatures and full modified logic.
RISKS: Do not derive expectations from another program version; use trigger assertion/spec only.