class names
set. When you clone an element with classes, modifying the clone's classes shouldn't affect the
original. Let me examine the relevant source methods.

The key methods are:

 - classNames() - returns a Set of class names
 - addClass(), removeClass(), toggleClass(), hasClass()
 - clone() - which likely shares the same internal Set reference

The bug is: after clone, mutating the clone's class set affects the original (shared reference).
Tests should verify that modifying clone doesn't change original's classNames, and vice versa.

<invoke name="view">
<parameter name="path">/tmp/sqa-copilot-clean/C10</parameter>
</invoke>

Let me see what files exist in the working directory and look for the full source to understand the
clone implementation.

<invoke name="bash">
<parameter name="command">find /tmp/sqa-copilot-clean/C10 -type f -name "*.java" 2>/dev/null | head
-30</parameter>
</invoke>

<invoke name="bash">
<parameter name="command">ls /tmp/sqa-copilot-clean/C10 2>/dev/null</parameter>
</invoke>
