#!/usr/bin/env bash
set -u
export PATH="$HOME/defects4j/framework/bin:$PATH"

root="$(cd "$(dirname "$0")/.." && pwd)"
test_file="$root/AI2_GitHubCopilot/TestCode/Cli-1/run-01/coverage-extended/CommandLineCopilotGeneratedTest.java"
result="$root/AI2_GitHubCopilot/Result/Cli-1/run-01"
junit="$HOME/defects4j/framework/projects/Cli/lib/junit/junit/3.8.1/junit-3.8.1.jar"
class="org.apache.commons.cli.CommandLineCopilotGeneratedTest"

if [[ ! -f "$test_file" || ! -f "$junit" ]]; then
  echo "ไม่พบไฟล์ test หรือ JUnit jar"; exit 1
fi

for version in 1f 1b; do
  workspace="$HOME/sqa-workspaces/Cli-$version-copilot"
  destination="$workspace/src/test/org/apache/commons/cli/CommandLineCopilotGeneratedTest.java"
  cp "$test_file" "$destination" || exit 1

  echo "=== Cli-$version: compile ==="
  defects4j compile -w "$workspace" > "$result/14_${version}_extended_compile.log" 2>&1
  if [[ $? -ne 0 ]]; then
    tail -25 "$result/14_${version}_extended_compile.log"
    continue
  fi

  cp_test="$(defects4j export -w "$workspace" -p cp.test | tail -n 1)"
  echo "=== Cli-$version: JUnit ==="
  java -cp "$junit:$cp_test" junit.textui.TestRunner "$class" \
    > "$result/14_${version}_extended_full.log" 2>&1
  grep -E 'OK \(|FAILURES!!!|Tests run:|^[0-9]+\) ' \
    "$result/14_${version}_extended_full.log"
done
