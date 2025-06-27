#!/bin/bash
set -e

REPO_PAT=$1

echo "🔁 Cloning repo..."
git clone https://${REPO_PAT}@github.com/nkwadoma/nkwadoma_backend_QA.git
cd nkwadoma_backend_QA
git checkout dev

# 📄 Create .env file dynamically
echo "📄 Creating .env file..."
cat <<EOF > .env
BASE_URL=https://api-systest.meedl.africa/
MAILOSAUR_API_KEY=DwamalC8ApwSDcbCfVSCl1Un5iR13BpB
MAILOSAUR_SERVER_ID=7fn3xvxg
AES_SECRET_KEY=secret_key
IV_AES_KEY=4983929933491528
EOF

# 🧹 Remove old venv if any
[ -d "venv" ] && rm -rf venv

# 🐍 Setup virtualenv
python3 -m venv venv
source venv/bin/activate

# 📦 Install dependencies
pip install --upgrade pip
pip uninstall -y config || true
pip install pytest pytest-html python-dotenv -r requirements.txt

# 🔧 Set PYTHONPATH
export PYTHONPATH=$(pwd)/src:$(pwd)/config:$(pwd)/utils

# ✅ Manual import check
python3 <<EOF
try:
    from config.project_configuration import logger
    print('✅ Successfully imported logger')
except Exception as e:
    print('❌ Import failed:', str(e))
    exit(1)
EOF

# 🧹 Cleanup caches
find . -type d -name "__pycache__" -exec rm -rf {} +
find . -type f -name "*.pyc" -delete

# 🧪 Run tests with report
REPORT_NAME="report-pytest-results.html"
pytest test/ --html="$REPORT_NAME" --self-contained-html -v

# ☁️ Upload to S3
if [ -f "$REPORT_NAME" ]; then
  echo "☁️ Uploading report to S3..."
  aws s3 cp "$REPORT_NAME" "s3://semicolon-delivery/nkwadoma/automation-test-report/$REPORT_NAME"
  echo "✅ Report uploaded successfully!"
  echo "📎 Report URL:"
  echo "https://semicolon-delivery.s3.eu-west-1.amazonaws.com/nkwadoma/automation-test-report/$REPORT_NAME"
else
  echo "❌ Report not found! Skipping upload."
  exit 1
fi

deactivate
echo "✅ Script completed."
