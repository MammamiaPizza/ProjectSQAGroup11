#!/usr/bin/env python3
"""Render Prompt 04 using only the Cli-1b source and its measured coverage."""

import argparse
import re
from pathlib import Path
from xml.etree import ElementTree


def coverage_report(xml_path: Path, log_path: Path) -> str:
    root = ElementTree.parse(xml_path).getroot()
    matches = [
        node for node in root.iter('class')
        if node.get('name', '').replace('/', '.') == 'org.apache.commons.cli.CommandLine'
    ]
    if len(matches) != 1:
        raise ValueError(f'Expected one CommandLine class in {xml_path}; found {len(matches)}')
    summary = log_path.read_text()
    totals = {}
    for label in ('Lines total', 'Lines covered', 'Conditions total', 'Conditions covered'):
        match = re.search(rf'{re.escape(label)}:\s*(\d+)', summary)
        if not match:
            raise ValueError(f'Missing {label} in {log_path}')
        totals[label] = match.group(1)
    lines = [
        'Defects4J coverage of CommandLine on buggy Cli-1b ONLY:',
        *(f'{label}: {amount}' for label, amount in totals.items()),
        'Cobertura line observations (source line, hits, branch, condition coverage):',
    ]
    for node in matches[0].iter('line'):
        details = [
            f"line={node.get('number')}",
            f"hits={node.get('hits')}",
            f"branch={node.get('branch', 'false')}",
        ]
        if node.get('condition-coverage'):
            details.append(f"condition-coverage={node.get('condition-coverage')}")
        for condition in node.iter('condition'):
            details.append(
                f"condition-{condition.get('number')}={condition.get('coverage')}"
            )
        lines.append(' '.join(details))
    if len(lines) == 6:
        raise ValueError(f'No line observations for CommandLine in {xml_path}')
    return '\n'.join(lines)


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--tool', required=True, choices=('AI1_ChatGPT', 'AI2_GitHubCopilot'))
    parser.add_argument('--test-class', required=True, type=Path)
    parser.add_argument('--coverage-xml', required=True, type=Path)
    parser.add_argument('--coverage-log', required=True, type=Path)
    args = parser.parse_args()

    repo = Path(__file__).resolve().parent.parent
    context = repo / 'Experiment/contexts/Cli-1/run-01'
    template = (repo / args.tool / 'Prompt/04_extend_coverage.txt').read_text()
    suite = args.test_class.read_text()
    report = coverage_report(args.coverage_xml, args.coverage_log)
    values = {
        '[PROJECT_ID]': 'Cli',
        '[BUG_ID]': '1',
        '[SOURCE_VERSION]': 'Buggy (Cli-1b)',
        '[JUNIT_VERSION]': 'JUnit 3.8.1',
        '[BUILD_TOOL]': 'Ant (Defects4J)',
        '[TARGET_CLASS]': 'org.apache.commons.cli.CommandLine',
        '[SPECIFICATION_OR_BUG_REPORT]': (context / 'permitted-specification.txt').read_text().rstrip(),
        '[JAVA_SOURCE_CODE]': (context / 'CommandLine.java').read_text().rstrip(),
        '[PROJECT_CONTEXT]': (context / 'project-context.txt').read_text().rstrip(),
        '[CURRENT_TEST_SUITE]': suite.rstrip(),
        '[COVERAGE_REPORT]': report,
    }
    for key, value in values.items():
        template = template.replace(key, value)
    missing = [key for key in values if key in template]
    if missing:
        raise ValueError(f'Unfilled placeholders: {missing}')

    output = repo / args.tool / 'Prompt/Cli-1/run-01/04_extend_coverage.txt'
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(template)
    print(f'Prepared {output} using buggy Cli-1b coverage only')


if __name__ == '__main__':
    main()
