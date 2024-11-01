#!/bin/bash

SMTP_SERVER=$1
SMTP_PORT=$2
SMTP_USERNAME=$3
SMTP_PASSWORD=$4
EMAILS=$5
TAG=$6
BRANCH_NAME=$7
COMMIT_AUTHOR=$8
SONARQUBE_URL_SET=$9
MAVEN_REPORT_URL_SET=${10}
AUTOMATION_TEST_URL_SET=${11}
COMMIT_MESSAGE=${12}

# Debugging outputs for validation
echo "Debug: COMMIT_AUTHOR = $COMMIT_AUTHOR"
echo "Debug: COMMIT_MESSAGE (raw) = $COMMIT_MESSAGE"

# Unescape any special characters in commit message
COMMIT_MESSAGE=$(echo "$COMMIT_MESSAGE" | sed 's/\\(/(/g; s/\\)/)/g; s/\\#/#/g')
echo "Debug: COMMIT_MESSAGE (unescaped) = $COMMIT_MESSAGE"

# Extract engineer's name from commit author
ENGINEER_NAME=$(echo "$COMMIT_AUTHOR" | sed 's/ <.*//')

# Split email addresses by comma
IFS=',' read -r -a email_array <<< "$EMAILS"

# Create HTML email content
cat << EOF > /tmp/email.html
From: builds@semicolon.africa
To: ${EMAILS}
Subject: Build Failure Notification
Content-Type: text/html; charset=UTF-8

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Build Failure</title>
</head>
<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
    <div style="background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 5px; padding: 20px;">
        <h1 style="color: #721c24; margin-top: 0;">Build Failure</h1>
        <p>Oooops, the recent build in Nkwadoma Backend was unsuccessful. Please check the details below:</p>
        <p><strong>Engineer:</strong> ${ENGINEER_NAME}</p>
        <p><strong>Branch:</strong> ${BRANCH_NAME}</p>
        <p><strong>Tag:</strong> ${TAG}</p>
        <p><strong>Commit Message:</strong> ${COMMIT_MESSAGE}</p>
    </div>

    <h2>Reports</h2>
    <ul>
EOF

# Append report links if set
[ "$SONARQUBE_URL_SET" = "true" ] && echo "<li><a href='${SONARQUBE_URL}' target='_blank'>SonarQube Report</a></li>" >> /tmp/email.html
[ "$MAVEN_REPORT_URL_SET" = "true" ] && echo "<li><a href='${MAVEN_REPORT_URL}' target='_blank'>Maven Report</a></li>" >> /tmp/email.html
[ "$AUTOMATION_TEST_URL_SET" = "true" ] && echo "<li><a href='${AUTOMATION_TEST_URL}' target='_blank'>Automation Test Report</a></li>" >> /tmp/email.html

cat << EOF >> /tmp/email.html
    </ul>
    <p>Regards,<br><strong>The Cloud Team</strong></p>
</body>
</html>
EOF

# Send emails to all recipients
for email in "${email_array[@]}"; do
  echo "Sending email to: $email" # Debug output

  curl --verbose --ssl-reqd \
    --url "smtps://${SMTP_SERVER}:${SMTP_PORT}" \
    --mail-from "builds@semicolon.africa" \
    --mail-rcpt "$email" \
    --user "${SMTP_USERNAME}:${SMTP_PASSWORD}" \
    --upload-file /tmp/email.html
done
