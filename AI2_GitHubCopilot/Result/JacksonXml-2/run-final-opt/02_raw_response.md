<tool_calls>
<tool_calls>
<invoke name="bash">
<parameter name="command">find /tmp/sqa-copilot-clean/Cpilot -type f -name "*.java" | xargs grep -l
"testMixedContent" 2>/dev/null</parameter>
</invoke>
<invoke name="bash">
<parameter name="command">find /tmp/sqa-copilot-clean/Cpilot -type f -name "*.java" -path
"*XmlTextTest*"</parameter>
</invoke>
<invoke name="bash">
<parameter name="command">find /tmp/sqa-copilot-clean/Cpilot -type f -name "*.java" | xargs grep -l
"XmlTokenStream" 2>/dev/null | head -20</parameter>
</invoke>
</tool_calls>