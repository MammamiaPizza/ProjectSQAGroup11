TARGETS: Parser.parse(String,String), Parser.parseBodyFragment(String,String); script text handling after data.
ORACLES: Trigger expected serialized HTML: `<script>inner</script> aft` (text after closing script is outside).
CASES: Full HTML with `pre <script>inner</script> aft` in body; assert document HTML/tree placement.
CASES: Body fragment equivalent; assert script text is `inner` and following body text is ` aft`.
CASES: Boundary text immediately after `</script>` and text before `<script>`.
RISKS: Parser internals are private; assert public Document/Element output only.
RISKS: No broader script/CDATA/error behavior is specified by the provided context.