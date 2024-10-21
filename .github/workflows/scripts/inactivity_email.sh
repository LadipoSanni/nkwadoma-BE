#!/bin/bash

SMTP_SERVER=$1
SMTP_PORT=$2
SMTP_USERNAME=$3
SMTP_PASSWORD=$4
EMAILS=$5
TIMESTAMP=$6

# Define the HTML body with proper formatting
read -r -d '' HTML_BODY <<EOF
<!DOCTYPE html>
<html>
<head>
  <title>Inactivity Alert</title>
</head>
<body>
  <h1>No Activity Detected</h1>
  <p>No commits or PRs were recorded since ${TIMESTAMP}.</p>
  <p>What are you guys up to! Please report to your PM!</p>
</body>
</html>
EOF

# Prepare the email headers and body as MIME format
read -r -d '' MIME_EMAIL <<EOF
From: "Build Tracker" <${SMTP_USERNAME}>
To: ${EMAILS}
Subject: 🚨 Inactivity Alert - No Commits or PRs
Content-Type: text/html; charset=UTF-8

${HTML_BODY}
EOF

# Loop through the email list and send each email using curl
IFS=',' read -r -a email_array <<< "$EMAILS"
for email in "${email_array[@]}"; do
  echo "$MIME_EMAIL" | curl --verbose --ssl-reqd \
    --url "smtps://${SMTP_SERVER}:${SMTP_PORT}" \
    --mail-from "$SMTP_USERNAME" \
    --mail-rcpt "$email" \
    --user "${SMTP_USERNAME}:${SMTP_PASSWORD}" \
    --upload-file -
done
