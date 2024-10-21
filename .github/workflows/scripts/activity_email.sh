#!/bin/bash

SMTP_SERVER=$1
SMTP_PORT=$2
SMTP_USERNAME=$3
SMTP_PASSWORD=$4
EMAILS=$5
SUCCESSFUL_COMMITS=$6
SUCCESSFUL_PRS=$7
TIMESTAMP=$8

# HTML Template for Activity Summary Email
read -r -d '' HTML_BODY <<EOF
<!DOCTYPE html>
<html>
<head>
  <title>Activity Summary</title>
</head>
<body>
  <h1>Activity Summary</h1>
  <p><strong>Commits:</strong> ${SUCCESSFUL_COMMITS}</p>
  <p><strong>Pull Requests Merged:</strong> ${SUCCESSFUL_PRS}</p>
  <p>Last checked at: ${TIMESTAMP}</p>
  <p>Thank you for your contributions!</p>
</body>
</html>
EOF

# Prepare MIME-formatted email
read -r -d '' MIME_EMAIL <<EOF
From: "Build Tracker" <${SMTP_USERNAME}>
To: ${EMAILS}
Subject: 🛠️ Activity Summary Report
Content-Type: text/html; charset=UTF-8

${HTML_BODY}
EOF

# Loop through email list and send each email
IFS=',' read -r -a email_array <<< "$EMAILS"
for email in "${email_array[@]}"; do
  echo "$MIME_EMAIL" | curl --verbose --ssl-reqd \
    --url "smtps://${SMTP_SERVER}:${SMTP_PORT}" \
    --mail-from "$SMTP_USERNAME" \
    --mail-rcpt "$email" \
    --user "${SMTP_USERNAME}:${SMTP_PASSWORD}" \
    --upload-file -
done
