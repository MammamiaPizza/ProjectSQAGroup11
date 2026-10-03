SQA Token-Saving Final Patch

1) Stop old run-final workers first:
   cd /mnt/c/Users/User/Desktop/SQAProjectGroup11-git
   RUN_ID=final ./Experiment/automation/stop_all_workers.sh
   If stop_all_workers.sh is not installed yet, stop old PIDs manually before applying this patch.

2) Extract this ZIP into the repository root with overwrite.

3) Permissions / checks:
   chmod +x Experiment/automation/*.sh
   python3 -m py_compile Experiment/automation/ai_runner.py Experiment/automation/token_report_opt.py

4) Run only the 3-case optimized pilot first:
   ./Experiment/automation/pilot_opt.sh

5) Inspect token result:
   python3 Experiment/automation/token_report_opt.py

6) If the pilot is acceptable, start full workers with the fresh run namespace:
   D4J_SLOTS=3 ./Experiment/automation/start_worker_opt.sh W1
   D4J_SLOTS=3 ./Experiment/automation/start_worker_opt.sh W2
   ...

Old run-final artifacts are untouched. New results go to run-final-opt.
