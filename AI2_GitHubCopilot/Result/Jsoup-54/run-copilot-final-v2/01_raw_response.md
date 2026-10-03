TARGETS: W3CBuilder.head() and copyAttributes() where Jsoup attributes are set on W3C DOM Element;
W3CDom.convert() entry point.
ORACLES: Expected from handlesInvalidAttributeNames: conversion must complete without throwing
DOMException (no crash); no requirement on attribute presence post‑conversion.
CASES: Attribute names with space, =, <, unbound colon (fb:like), leading digit, standalone :, empty
string "", high‑unicode.
CASES: Boundary: very long name (≥1024 chars) with legal chars; document containing only
invalid‑name attributes; empty attribute value.
RISKS: Fix strategy unknown (skip vs. mangle); tests must not assert that invalid attributes survive
— only that no exception occurs during convert/fromJsoup.