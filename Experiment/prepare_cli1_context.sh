#!/usr/bin/env bash
# Build matched AI prompts from the buggy Cli-1 checkout only.
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
checkout="${1:-$HOME/sqa-workspaces/Cli-1b}"
source_file="$checkout/src/java/org/apache/commons/cli/CommandLine.java"
context_dir="$repo_root/Experiment/contexts/Cli-1/run-01"

if [[ ! -f "$source_file" ]]; then
  echo "Missing Cli-1b source: $source_file" >&2
  exit 1
fi
if ! command -v defects4j >/dev/null 2>&1; then
  export PATH="$HOME/defects4j/framework/bin:$PATH"
fi
if ! command -v defects4j >/dev/null 2>&1; then
  echo 'defects4j not found' >&2
  exit 1
fi

# Check the checkout before writing any context or prompts.
modified="$(cd "$checkout" && defects4j export -p classes.modified | tail -n 1)"
if [[ "$modified" != 'org.apache.commons.cli.CommandLine' ]]; then
  echo "Expected modified class CommandLine; got: $modified" >&2
  exit 1
fi
test_classpath="$(cd "$checkout" && defects4j export -p cp.test | tail -n 1)"
if [[ "$test_classpath" != *junit-3.8.1.jar* ]]; then
  echo 'Expected JUnit 3.8.1 in the Cli-1b test classpath' >&2
  exit 1
fi

mkdir -p "$context_dir"
cp "$source_file" "$context_dir/CommandLine.java"
printf '%s\n' "$modified" > "$context_dir/classes.modified.log"
(cd "$checkout" && defects4j export -p dir.src.classes | tail -n 1) > "$context_dir/dir.src.classes.log"
(cd "$checkout" && defects4j export -p dir.src.tests | tail -n 1) > "$context_dir/dir.src.tests.log"
java -version 2> "$context_dir/java-version.txt"
if [[ -d "$HOME/defects4j/.git" ]]; then
  git -C "$HOME/defects4j" rev-parse HEAD > "$context_dir/defects4j-commit.txt"
fi
sha256sum "$context_dir/CommandLine.java" > "$context_dir/source.sha256"

# javap lists the public signatures of related classes already compiled by Defects4J.
javap -public -classpath "$test_classpath" \
  org.apache.commons.cli.Options \
  org.apache.commons.cli.Option \
  org.apache.commons.cli.PosixParser \
  org.apache.commons.cli.CommandLineParser > "$context_dir/related-public-api.txt"

python3 - "$repo_root" "$context_dir" <<'PY'
from pathlib import Path
import sys

root, ctx = map(Path, sys.argv[1:])
source = (ctx / 'CommandLine.java').read_text()
related_api = (ctx / 'related-public-api.txt').read_text()
specification = (
    'Apache Commons CLI issue CLI-13 is described as "CommandLine.getOptionValue() '
    'behaves contrary to docs" in the Apache Commons CLI release notes. '
    'Source: https://github.com/apache/commons-cli/blob/master/RELEASE-NOTES.txt . '
    'The Javadoc in the supplied buggy CommandLine.java says getOptionValue(String) '
    'returns the argument value if an option is set and has an argument; otherwise null. '
    'Use the supplied Javadoc for the other overloads. Do not infer behavior from '
    'the buggy method body.\n'
)
project_context = (
    'Production package: org.apache.commons.cli. Target: CommandLine; its constructor '
    'is package-private, so construct it through public parsing APIs when needed. '
    'Defects4J Cli-1b; Java version in the attached java-version.txt; JUnit 3.8.1; Ant. '
    'Use JUnit 3-compatible TestCase methods and test method names starting with test. '
    'The attached javap listing contains public signatures from the buggy checkout '
    'for the related parser and option classes; source implementations for those '
    'classes are not supplied. Original project tests, fixed source, and generated '
    'tests from other methods are not supplied.\n\n'
    'Related public signatures from the buggy checkout:\n' + related_api
)
(ctx / 'permitted-specification.txt').write_text(specification)
(ctx / 'project-context.txt').write_text(project_context)

values = {
    '[PROJECT_ID]': 'Cli',
    '[BUG_ID]': '1',
    '[SOURCE_VERSION]': 'Buggy (Cli-1b)',
    '[TARGET_CLASS]': 'org.apache.commons.cli.CommandLine',
    '[JUNIT_VERSION]': 'JUnit 3.8.1',
    '[BUILD_TOOL]': 'Ant (Defects4J)',
    '[SPECIFICATION_OR_BUG_REPORT]': specification.rstrip(),
    '[JAVA_SOURCE_CODE]': source.rstrip(),
    '[PROJECT_CONTEXT]': project_context.rstrip(),
}
for name in ('01_analyze_context.txt', '02_generate_suite.txt'):
    rendered = (root / 'AI1_ChatGPT' / 'Prompt' / name).read_text()
    for key, value in values.items():
        rendered = rendered.replace(key, value)
    if any(key in rendered for key in values):
        raise ValueError('Unfilled prompt placeholder: ' + name)
    for tool in ('AI1_ChatGPT', 'AI2_GitHubCopilot'):
        destination = root / tool / 'Prompt' / 'Cli-1' / 'run-01'
        destination.mkdir(parents=True, exist_ok=True)
        (destination / name).write_text(rendered)

print('Cli-1b context:', ctx)
print('Prepared identical prompts 01 and 02 for both AI tools (JUnit 3.8.1).')
PY
