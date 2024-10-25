#!/bin/bash

SMTP_SERVER=$1
SMTP_PORT=$2
SMTP_USERNAME=$3
SMTP_PASSWORD=$4
EMAILS=$5
LAST_TIMESTAMP=$6

CURRENT_TIME=$7

TIME_DIFF=$8

read -r -d '' HTML_BODY <<EOF
<!DOCTYPE html>
<html>
<head>
  <title>Inactivity Alert</title>
</head>
<body>
  <h1>No Activity Detected</h1>
  <p>No commits or PRs have been made in the last ${TIME_DIFF} hours.</p>
  <p>Last activity was at: ${LAST_TIMESTAMP}</p>
  <p>Checked at: ${CURRENT_TIME}</p>
  <p>Please engage with your tasks!</p>
  <p>Or Speak with your PM</p>
  <p>Thanks!</p>
  <p>The Cloud Team.</p>
  
</body>
</html>
EOF

read -r -d '' MIME_EMAIL <<EOF
From: "Build Tracker" <${SMTP_USERNAME}>
To: ${EMAILS}
Subject: 🚨 Inactivity Alert
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
