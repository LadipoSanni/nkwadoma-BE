# #!/bin/bash
# set -e

# REPO_PAT=$1

# echo "🔁 Cloning repo..."
# git clone https://${REPO_PAT}@github.com/nkwadoma/nkwadoma_backend_QA.git
# cd nkwadoma_backend_QA
# git checkout dev

# # 📄 Create .env file dynamically
# echo "📄 Creating .env file..."
# cat <<EOF > .env
# BASE_URL=https://api-systest.meedl.africa/
# MAILOSAUR_API_KEY=DwamalC8ApwSDcbCfVSCl1Un5iR13BpB
# MAILOSAUR_SERVER_ID=7fn3xvxg
# AES_SECRET_KEY=secret_key
# IV_AES_KEY=4983929933491528
# EOF
# echo "✅ .env file created"

# # 🧹 Clean up if venv already exists
# if [ -d "venv" ]; then
#   echo "⚠️ Removing existing venv"
#   rm -rf venv
# fi

# # 🐍 Create fresh virtual environment
# echo "✅ Creating fresh virtualenv..."
# python3 -m venv venv

# # 🧠 Confirm venv was created successfully
# if [ ! -x "venv/bin/python" ]; then
#   echo "❌ venv creation failed!"
#   exit 1
# fi

# # 🧪 Activate the venv
# source venv/bin/activate

# # 📦 Install dependencies
# echo "📦 Upgrading pip and installing dependencies..."
# pip install --upgrade pip
# pip uninstall -y config || true
# pip show config && echo "❌ Config package still installed! Please remove 'config' from requirements.txt" && exit 1 || echo "✅ Config package not installed"
# pip install pytest pytest-html python-dotenv -r requirements.txt

# # 📄 Check if .env file exists and show contents
# echo "📄 Checking if .env file is loaded..."
# if [ ! -f .env ]; then
#   echo "❌ .env file not found in project root!"
#   exit 1
# else
#   echo "✅ .env file found. Contents:"
#   cat .env
# fi

# # 🔧 Set PYTHONPATH
# echo "🔧 Setting PYTHONPATH..."
# export PYTHONPATH=$(pwd)/src:$(pwd)/config:$(pwd)/utils
# echo "🔧 PYTHONPATH set to: $PYTHONPATH"
# python3 -c "import sys; print('sys.path:', sys.path)"

# # 🔍 Test import manually
# echo "🔍 Verifying Python import from config.project_configuration..."
# python3 <<EOF
# try:
#     from config.project_configuration import logger
#     print('✅ Successfully imported logger')
# except Exception as e:
#     print('❌ Import failed:', str(e))
#     exit(1)
# EOF

# # 🧹 Remove __pycache__ and *.pyc to avoid import mismatches
# echo "🧹 Removing __pycache__ and *.pyc files to prevent import issues..."
# find . -type d -name "__pycache__" -exec rm -rf {} +
# find . -type f -name "*.pyc" -delete

# # 🧪 Run tests with pytest
# echo "🧪 Running tests with pytest..."
# set +e
# PYTHONPATH=$(pwd)/src:$(pwd)/config:$(pwd)/utils \
#   python3 -m pytest test/ --html=report-pytest-results.html --self-contained-html -v
# TEST_EXIT_CODE=$?
# set -e

# # ☁️ Upload report
# if [ -f "report-pytest-results.html" ]; then
#   echo "☁️ Uploading report to S3..."
#   aws s3 cp report-pytest-results.html s3://semicolon-delivery/nkwadoma/automation-test-report/report-pytest-results.html
# else
#   echo "⚠️ Test report not found. Skipping S3 upload."
# fi

# # 🔻 Deactivate venv
# deactivate

# echo "✅ Script completed."
# exit $TEST_EXIT_CODE
