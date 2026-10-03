<tool_calls>
<invoke name="bash">
<parameter name="command">find /tmp/sqa-copilot-clean/Cpilot -maxdepth 3 -type f | head -50 && echo
'---' && find /tmp/sqa-copilot-clean/Cpilot -path '*XmlTokenStream*' -o -path '*XmlTextTest*' |
head</parameter>
</invoke>
</tool_calls>