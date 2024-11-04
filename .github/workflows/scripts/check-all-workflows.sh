#!/bin/bash

# List of workflows to check
WORKFLOWS=("Java CI with Maven" "Deploy Systest to Amazon EC2" "Deploy Learnspace Systest to AWS ECS")

# Time period in hours to look back
TIME_PERIOD=4

# Function to check workflow status within the last 4 hours
check_workflow_status() {
  local workflow=$1

  # Get the status of the most recent workflow run within the last TIME_PERIOD hours
  local status=$(gh run list --workflow "$workflow" --created ">=$(date -u -d "$TIME_PERIOD hours ago" +%Y-%m-%dT%H:%M:%SZ)" --json conclusion --jq '.[0].conclusion')

  case "$status" in
    success)
      echo "✅ Workflow '$workflow' passed successfully in the last $TIME_PERIOD hours."
      ;;
    failure)
      echo "❌ Workflow '$workflow' failed in the last $TIME_PERIOD hours."
      ;;
    skipped)
      echo "⏩ Workflow '$workflow' was skipped in the last $TIME_PERIOD hours."
      ;;
    "")
      echo "⚠️ No runs for '$workflow' in the last $TIME_PERIOD hours."
      ;;
    *)
      echo "❔ Workflow '$workflow' status: $status"
      ;;
  esac
}

# Check each workflow's status
for workflow in "${WORKFLOWS[@]}"; do
  check_workflow_status "$workflow"
done
