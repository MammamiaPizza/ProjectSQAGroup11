TARGETS: handleRcData, handleRawtext, process for data-only tag states; state transitions when
tokenising raw text / RCDATA.
ORACLES: Text inside data-only tags must stay literal; no extra characters from tokeniser or state
transition.
ORACLES: ParserTest::handlesDataOnlyTags expects "Hello There" without injected script fragments.
CASES: data-only tags with normal text, empty content, angle brackets, HTML entities, and
nested-like </script> string.
CASES: boundary: whitespace-only content, very long raw text, adjacent data-only tags, attribute
values in start tag.
RISKS: Bug likely in handleRawtext/handleRcData token consumption; unexpected characters added after
raw text emission.
RISKS: Source diff unknown; exact method affected and root cause may require exploring state
transition logic around script/style tags.