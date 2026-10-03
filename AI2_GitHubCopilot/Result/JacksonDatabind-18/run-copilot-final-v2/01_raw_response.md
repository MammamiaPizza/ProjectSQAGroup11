TARGETS: MappingIterator.nextValue() recovery on invalid token; readAll() error-skipping;
_handleMappingException
ORACLES: Jackson spec: nextValue() skips unmappable value; hasNext() true after failure; readAll()
returns valid entries
CASES: Normal: readAll() valid Beans. Error: int in array; root-level int before Bean; consecutive
errors; empty.
RISKS: Parser state after exception; Bean inner class; _handleMappingException may not advance
parser