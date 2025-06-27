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
echo "✅ .env file created"

# 🧹 Clean up if venv already exists
if [ -d "venv" ]; then
  echo "⚠️ Removing existing venv"
  rm -rf venv
fi

# 🐍 Create fresh virtual environment
echo "✅ Creating fresh virtualenv..."
python3 -m venv venv

# 🧠 Confirm venv was created successfully
if [ ! -x "venv/bin/python" ]; then
  echo "❌ venv creation failed!"
  exit 1
fi

# 🧪 Activate the venv
source venv/bin/activate

# 📦 Install dependencies
echo "📦 Installing dependencies..."
pip install --upgrade pip
pip uninstall -y config || true
pip show config && echo "❌ Config package still installed! Please remove 'config' from requirements.txt" && exit 1 || echo "✅ Config package not installed"
pip install pytest pytest-html python-dotenv -r requirements.txt

# 🔧 Set PYTHONPATH
export PYTHONPATH=$(pwd)/src:$(pwd)/config:$(pwd)/utils
echo "🔧 PYTHONPATH set to: $PYTHONPATH"

# 🔍 Import check
python3 <<EOF
try:
    from config.project_configuration import logger
    print('✅ Successfully imported logger')
except Exception as e:
    print('❌ Import failed:', str(e))
    exit(1)
EOF

# 🧹 Remove caches
echo "🧹 Cleaning __pycache__ and *.pyc files..."
find . -type d -name "__pycache__" -exec rm -rf {} +
find . -type f -name "*.pyc" -delete

# 🧪 Run tests and generate report
REPORT_NAME="report-pytest-results.html"
echo "🧪 Running tests and generating report..."
pytest test/ --html="$REPORT_NAME" --self-contained-html -v

# ☁️ Upload report to S3
if [ -f "$REPORT_NAME" ]; then
  echo "✅ Report generated: $REPORT_NAME"
  echo "☁️ Uploading report to S3..."
  aws s3 cp "$REPORT_NAME" "s3://semicolon-delivery/nkwadoma/automation-test-report/$REPORT_NAME"
  echo "✅ Report uploaded to S3!"
else
  echo "❌ Report not found. Cannot upload."
  exit 1
fi

# 🔻 Deactivate venv
deactivate

echo "🎉 Script completed."
