#!/bin/bash

# List of workflows to check
WORKFLOWS=("Java CI with Maven" "Deploy Systest to Amazon EC2" "Deploy Learnspace Systest to AWS ECS")

# Function to check workflow status
check_workflow_status() {
  local workflow=$1
  local status=$(gh run list --workflow "$workflow" --json conclusion --jq '.[0].conclusion')
  
  case "$status" in
    success)
      echo "✅ Workflow '$workflow' passed successfully."
      ;;
    failure)
      echo "❌ Workflow '$workflow' failed."
      ;;
    skipped)
      echo "⏩ Workflow '$workflow' was skipped."
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
