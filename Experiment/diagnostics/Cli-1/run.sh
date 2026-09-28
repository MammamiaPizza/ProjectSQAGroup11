#!/usr/bin/env bash
set -u

root="$(cd "$(dirname "$0")/../../.." && pwd)"
output="$root/Experiment/diagnostics/Cli-1"
junit="$HOME/defects4j/framework/projects/Cli/lib/junit/junit/3.8.1/junit-3.8.1.jar"

printf 'version,compile,tests\n' > "$output/results.csv"

for version in 1f 1b; do
  workspace="$HOME/sqa-workspaces/Cli-$version-copilot"
  classes="$output/classes-$version"
  mkdir -p "$classes"

  cp_test="$(defects4j export -w "$workspace" -p cp.test | tail -n 1)"
  javac -cp "$junit:$cp_test" -d "$classes" \
    "$output/CommandLineLongOptionDiagnosticTest.java" \
    > "$output/${version}_compile.log" 2>&1
  if [[ $? -ne 0 ]]; then
    printf '%s,FAIL,NOT_RUN\n' "$version" >> "$output/results.csv"
    echo "Cli-$version: compile ไม่ผ่าน"
    continue
  fi

  java -cp "$classes:$junit:$cp_test" junit.textui.TestRunner \
    org.apache.commons.cli.CommandLineLongOptionDiagnosticTest \
    > "$output/${version}_test.log" 2>&1
  if [[ $? -eq 0 ]]; then
    printf '%s,PASS,PASS\n' "$version" >> "$output/results.csv"
    echo "Cli-$version: PASS"
  else
    printf '%s,PASS,FAIL\n' "$version" >> "$output/results.csv"
    echo "Cli-$version: FAIL"
    grep -E '^[0-9]+\)|FAILURES!!!|Tests run:' "$output/${version}_test.log"
  fi
done
