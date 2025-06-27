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
pip show config && echo "❌ Config package still installed! Remove 'config' from requirements.txt" && exit 1 || echo "✅ Config not installed"
pip install pytest pytest-html python-dotenv -r requirements.txt

# 🔧 Set PYTHONPATH
export PYTHONPATH=$(pwd)/src:$(pwd)/config:$(pwd)/utils

# 🧹 Clean cache
find . -type d -name "__pycache__" -exec rm -rf {} +
find . -type f -name "*.pyc" -delete

# 🧪 Run tests and generate HTML report
echo "🧪 Running tests..."
pytest test/ --html=report-pytest-results.html --self-contained-html -v

# ☁️ Upload report to S3
if [ -f "report-pytest-results.html" ]; then
  echo "☁️ Uploading report to S3..."
  
  if [[ -z "$AWS_ACCESS_KEY_ID" || -z "$AWS_SECRET_ACCESS_KEY" ]]; then
    echo "❌ AWS credentials not found. S3 upload skipped."
    exit 1
  fi

  export AWS_DEFAULT_REGION=${AWS_DEFAULT_REGION:-eu-west-1}

  aws s3 cp report-pytest-results.html \
    s3://semicolon-delivery/nkwadoma/automation-test-report/automation-tests-result/report-pytest-results.html \
    --acl public-read \
    --content-type text/html

  echo "✅ Report uploaded successfully!"
else
  echo "⚠️ Test report not found. Skipping S3 upload."
  exit 1
fi

# 🔻 Deactivate venv
deactivate
echo "✅ Script completed."
