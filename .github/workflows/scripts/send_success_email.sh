#!/bin/bash

# Input variables
PROJECT_NAME="Nkwadoma_Be"
TASK_ID="task-${BRANCH_NAME}"

SMTP_SERVER=$1
SMTP_PORT=$2
SMTP_USERNAME=$3
SMTP_PASSWORD=$4
EMAILS=$5
COMMIT_AUTHOR=$6
BRANCH_NAME=$7
COMMIT_AUTHOR=$8
SONARQUBE_URL_SET=$9
MAVEN_REPORT_URL_SET=${10}
AUTOMATION_TEST_URL_SET=${11}
COMMIT_MESSAGE=${12}
ENGINEER_NAME=$(echo "$COMMIT_AUTHOR" | sed 's/ <.*//')
SONARQUBE_URL=http://52.2.188.133:9000/
MAVEN_REPORT_URL=
AUTOMATION_TEST_URL=
COMMIT_MESSAGE=$(echo "$COMMIT_MESSAGE" | sed 's/\\(/(/g; s/\\)/)/g; s/\\#/#/g')

# Fixing unescaped characters in commit message
COMMIT_MESSAGE=$(echo "$COMMIT_MESSAGE" | sed 's/\\(/(/g; s/\\)/)/g; s/\\#/#/g')

# Determine branch name based on event type
if [ "$GITHUB_EVENT_NAME" == "pull_request" ]; then
    BRANCH_NAME=${GITHUB_HEAD_REF}
else
    BRANCH_NAME=$(git rev-parse --abbrev-ref HEAD)
fi
echo "BRANCH_NAME=${BRANCH_NAME}" >> $GITHUB_ENV

# Set tag name
TAG="${PROJECT_NAME}-${BRANCH_NAME}-${TASK_ID}"

# Fetch commit logs
if [ "$GITHUB_EVENT_NAME" == "pull_request" ]; then
    git fetch origin ${GITHUB_BASE_REF}
    COMMITS=$(git log origin/${GITHUB_BASE_REF}..HEAD --pretty=%B)
else
    git fetch --all
    COMMITS=$(git log -1 --pretty=%B)
fi

echo "Commit messages after merge: $COMMITS"


# Extract engineer's name from commit author
ENGINEER_NAME=$(echo "$COMMIT_AUTHOR" | sed 's/ <.*//')

# Read email recipients into an array
IFS=',' read -r -a email_array <<< "${EMAILS}"

# Send email to each recipient
for email in "${email_array[@]}"
do
  cat << EOF > /tmp/email.html
From: builds@semicolon.africa
To: $email
Subject: Build Success - ${PROJECT_NAME}
Content-Type: text/html
MIME-Version: 1.0

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Build Success</title>
</head>
<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
    <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 5px; padding: 20px; margin-bottom: 20px;">
        <h1 style="color: #155724; margin-top: 0;">Fantastic! Successful Build</h1>
        <p style="margin-bottom: 10px;">Your recent build for the Nkwadoma Backend project was successful.</p>
    </div>

    <div style="background-color: #f8f9fa; border: 1px solid #e9ecef; border-radius: 5px; padding: 20px; margin-bottom: 20px;">
        <h2 style="margin-top: 0;">Build Details</h2>
        <p><strong>ENGINEER:</strong> ${ENGINEER_NAME}</p>
        <p><strong>BRANCH:</strong> ${BRANCH_NAME}</p>
        <p><strong>TAG:</strong> ${TAG}</p>
        <p><strong>COMMIT MESSAGE:</strong> ${COMMIT_MESSAGE}</p>
    </div>

    <div style="background-color: #e9ecef; border: 1px solid #ced4da; border-radius: 5px; padding: 20px;">
        <h2 style="margin-top: 0;">Reports</h2>
        <p>Click on the links below to view your reports:</p>
        <ul style="padding-left: 20px;">
EOF

  # Include links only if enabled
  if [ "$SONARQUBE_URL_SET" = "true" ]; then
    echo "<li><a href=\"$SONARQUBE_URL\" style=\"color: #007bff; text-decoration: none;\">SonarQube Report</a></li>" >> /tmp/email.html
  fi
  if [ "$AUTOMATION_TEST_URL_SET" = "true" ]; then
    echo "<li><a href=\"$AUTOMATION_TEST_URL\" style=\"color: #007bff; text-decoration: none;\">Automation Test Report</a></li>" >> /tmp/email.html
  fi

  cat << EOF >> /tmp/email.html
        </ul>
    </div>

    <div style="margin-top: 20px; padding-top: 20px; border-top: 1px solid #ced4da;">
        <p style="margin-bottom: 5px;">Regards,</p>
        <p style="margin-top: 0;"><strong>The Cloud Team</strong></p>
    </div>
</body>
</html>
EOF

  # Send the email using curl with error handling
  curl --ssl-reqd \
    --url "smtps://${SMTP_SERVER}:${SMTP_PORT}" \
    --mail-from "builds@semicolon.africa" \
    --mail-rcpt "$email" \
    --user "${SMTP_USERNAME}:${SMTP_PASSWORD}" \
    --upload-file /tmp/email.html || echo "Failed to send email to $email"
done
