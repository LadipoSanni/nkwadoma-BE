#!/bin/bash

REPO_PAT=$1

# Exit immediately if a command exits with a non-zero status
set -e

# Clone the target repository
git clone https://${REPO_PAT}@github.com/nkwadoma/nkwadoma_backend_QA.git
# Navigate to the cloned repository
ls
cd nkwadoma_backend_QA
git checkout dev

# Create and activate a virtual environment
python3 -m venv venv
source venv/bin/activate

# Install pytest and other requirements
pip install pytest
pip install -r requirements.txt
pip install pytest-html

# Run the tests
pytest test/ --html=report-pytest-results.html --self-contained-html || true 
#aws s3 cp report-pytest-results.html s3://semicolon-build-reports/karrabo/identity-management/automation-tests-result/report-pytest-results.html
aws s3 cp report-pytest-results.html s3://meedl-build-report/automation-tests-result/report-pytest-results.html

# Deactivate the virtual environment
deactivate

# Navigate back to the original directory
cd ..

# Clean up (optional, remove if you want to keep the cloned repo)
rm -rf nkwadoma_backend_QA
