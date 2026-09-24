#!/usr/bin/env bash
# Prepare a single, identical Lang-1 prompt input for ChatGPT and Copilot.
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
checkout="${1:-$HOME/sqa-workspaces/Lang-1b}"
source_file="$checkout/src/main/java/org/apache/commons/lang3/math/NumberUtils.java"
context_dir="$repo_root/Experiment/contexts/Lang-1/run-01"

if [[ ! -f "$source_file" ]]; then
  echo "Missing buggy source: $source_file" >&2
  exit 1
fi
if ! command -v defects4j >/dev/null 2>&1; then
  export PATH="$HOME/defects4j/framework/bin:$PATH"
fi
if ! command -v defects4j >/dev/null 2>&1; then
  echo 'defects4j not found; check your PATH and installation' >&2
  exit 1
fi

mkdir -p "$context_dir"
cp "$source_file" "$context_dir/NumberUtils.java"
(
  cd "$checkout"
  defects4j export -p classes.modified > "$context_dir/classes.modified.log"
  defects4j export -p dir.src.classes > "$context_dir/dir.src.classes.log"
  defects4j export -p dir.src.tests > "$context_dir/dir.src.tests.log"
)
if ! grep -Fq 'org.apache.commons.lang3.math.NumberUtils' "$context_dir/classes.modified.log"; then
  echo 'The checkout does not report NumberUtils as a modified class; inspect checkout before using prompts' >&2
  exit 1
fi

java -version 2> "$context_dir/java-version.txt"
if [[ -d "$HOME/defects4j/.git" ]]; then
  git -C "$HOME/defects4j" rev-parse HEAD > "$context_dir/defects4j-commit.txt"
fi
sha256sum "$context_dir/NumberUtils.java" > "$context_dir/source.sha256"

python3 - "$repo_root" "$context_dir" <<'PY'
from pathlib import Path
import sys

root, ctx = map(Path, sys.argv[1:])
source = (ctx / 'NumberUtils.java').read_text()
specification = (
    'Apache Commons Lang issue LANG-747, "NumberUtils does not handle Long Hex numbers". '
    'The issue describes that NumberUtils.createNumber should handle hexadecimal values '
    'that are too large for Integer rather than assuming every short hexadecimal literal fits Integer. '
    'Source: https://issues.apache.org/jira/browse/LANG-747\n'
)
(ctx / 'permitted-specification.txt').write_text(specification)
project_context = (
    'Production package: org.apache.commons.lang3.math. '
    'Target: public class NumberUtils; use only APIs present in the attached NumberUtils.java. '
    'Defects4J Lang-1b checkout, Java 11, JUnit 4 (4.12), Ant build. '
    'Original project tests, hand-written boundary test, fixed source, and other generated tests '
    'are not supplied as input. If further dependencies or APIs are needed, report the missing context.\n'
)
(ctx / 'project-context.txt').write_text(project_context)

values = {
    '[PROJECT_ID]': 'Lang',
    '[BUG_ID]': '1',
    '[SOURCE_VERSION]': 'Buggy (Lang-1b)',
    '[TARGET_CLASS]': 'org.apache.commons.lang3.math.NumberUtils',
    '[JUNIT_VERSION]': 'JUnit 4.12',
    '[BUILD_TOOL]': 'Ant (Defects4J)',
    '[SPECIFICATION_OR_BUG_REPORT]': specification.rstrip(),
    '[JAVA_SOURCE_CODE]': source.rstrip(),
    '[PROJECT_CONTEXT]': project_context.rstrip(),
}
for number, name in [('01', '01_analyze_context.txt'), ('02', '02_generate_suite.txt')]:
    rendered = (root / 'ChatGPT' / 'Prompt' / name).read_text()
    for key, value in values.items():
        rendered = rendered.replace(key, value)
    assert not any(key in rendered for key in values), f'Missing placeholder in {name}'
    for tool in ('ChatGPT', 'GitHubCopilot'):
        directory = root / tool / 'Prompt' / 'Lang-1' / 'run-01'
        directory.mkdir(parents=True, exist_ok=True)
        (directory / name).write_text(rendered)

print('Prepared identical Prompt 01 and 02 for ChatGPT and Copilot.')
print('Context and source snapshot:', ctx)
print('Prompt 03 and 04 remain templates until actual errors and coverage reports exist.')
PY
