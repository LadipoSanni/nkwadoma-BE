#!/bin/bash

SMTP_SERVER=$1
SMTP_PORT=$2
SMTP_USERNAME=$3
SMTP_PASSWORD=$4
EMAILS=$5
SUCCESSFUL_COMMITS=$6
SUCCESSFUL_PRS=$7
FAILED_PRS=$8
TIMESTAMP=$9
COMMIT_AUTHOR=${10}
COMMIT_MESSAGE=${11}

CURRENT_TIME=$(date --utc +%Y-%m-%dT%H:%M:%SZ)

read -r -d '' HTML_BODY <<EOF
<!DOCTYPE html>
<html>
<head>
  <title>Activity Summary</title>
</head>
<body>
  <h1>Activity Summary</h1>
  <p><strong>Commits Verified:</strong> ${SUCCESSFUL_COMMITS}</p>
  <p><strong>Merged Pull Requests:</strong> ${SUCCESSFUL_PRS}</p>
  <p><strong>Failed PR Builds:</strong> ${FAILED_PRS}</p>
  <p><strong>Last Commit Author:</strong> ${COMMIT_AUTHOR}</p>
  <p><strong>Last Commit Message:</strong> ${COMMIT_MESSAGE}</p>
  <p>Checked at: ${CURRENT_TIME}</p>
  <p>Keep up the great work!</p>
</body>
</html>
EOF

read -r -d '' MIME_EMAIL <<EOF
From: "Build Tracker" <${SMTP_USERNAME}>
To: ${EMAILS}
Subject: 🛠️ Activity Summary Report
Content-Type: text/html; charset=UTF-8

${HTML_BODY}
EOF

IFS=',' read -r -a email_array <<< "${EMAILS}"
for email in "${email_array[@]}"; do
  echo "$MIME_EMAIL" | curl --verbose --ssl-reqd \
    --url "smtps://${SMTP_SERVER}:${SMTP_PORT}" \
    --mail-from "${SMTP_USERNAME}" \
    --mail-rcpt "$email" \
    --user "${SMTP_USERNAME}:${SMTP_PASSWORD}" \
    --upload-file -
done
