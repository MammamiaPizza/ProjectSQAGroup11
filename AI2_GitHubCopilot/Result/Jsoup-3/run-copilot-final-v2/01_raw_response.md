TARGETS: Element.append(String), Element.prepend(String)
TARGETS: Parser.parseBodyFragment for table context
ORACLES: Expected HTML: append <tr> to existing <table> yields single table with both rows, no extra
<table> or </table>
ORACLES: Prepend row: row order correct, no spurious table tags
ORACLES: Nested table: inner table contained in <td>, outer row after </td> untouched
CASES: Normal: append row to non-empty table; prepend row; nested table inside <td>
CASES: Boundary: append row to empty table; append multiple rows; append text after table
RISKS: Fragment parser may auto-close <table> before inserting row; Tag containment rules for
<tr>/<td> may be missing