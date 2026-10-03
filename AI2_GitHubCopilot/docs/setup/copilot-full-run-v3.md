Copilot full-run safety patch

- Keeps the validated Copilot v2 agent/prompt behavior unchanged.
- Shares Defects4J slots with ChatGPT through Experiment/runtime/run-final-opt/d4j-slots.
- Runs unmeasured projects first and defers JacksonXml until the end.
- Full run id remains copilot-final-v2 so existing reporting paths stay stable.

Start: D4J_SLOTS=3 ./Experiment/automation/start_copilot_worker_v3.sh C1
Monitor: watch -n 5 ./Experiment/automation/live_monitor_copilot_v3.sh
