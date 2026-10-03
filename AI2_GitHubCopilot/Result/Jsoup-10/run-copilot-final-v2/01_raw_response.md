TARGETS: org.jsoup.nodes.Node.absUrl(String) method for resolving absolute URLs from relative URLs.
ORACLES: Bug report #49 expected behavior: relative URL "file?foo" against base
"]8;id=md-1gpdjiz;http://jsoup.org/path/http://jsoup.org/path/]8;;]8;;" → "]8;;]8;id=md-uzocn2;http://jsoup.org/path/file?foohttp://jsoup.org/path/file?foo]8;;]8;;".]8;;
CASES: High-value: relative with query only (?foo), with path segment (path/file), with fragment
(#bar), combined (path/file?foo#bar).
CASES: Boundary: null/empty attribute key, attribute value is absolute URL, base URI missing
trailing slash, base URI with query/fragment.
RISKS: Only one test case provided; may miss path/components interaction (file vs. dir), multi-param
query, or URL class edge behaviors.