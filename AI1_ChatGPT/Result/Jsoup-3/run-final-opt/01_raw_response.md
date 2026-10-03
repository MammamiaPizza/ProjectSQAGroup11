TARGETS: Element.append/prepend HTML into table; Parser implicit table-row handling; Tag containment metadata.  
ORACLES: Trigger expected HTML serialization and DOM nesting from ElementTest and ParserTest assertions.  
CASES: Append `<tr><td>2</td></tr>` to table already containing a row; result is sibling `tr`, not nested table.  
CASES: Prepend the same row; result precedes existing row without creating an inner table.  
CASES: Parse nested table with implicit rows/cells; nested rows stay in nested table and outer following row remains outer.  
RISKS: Available context omits full parser/Tag rules and exact public construction/parsing assertions.