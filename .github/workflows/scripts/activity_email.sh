#!/bin/bash 

SMTP_SERVER=$1
SMTP_PORT=$2
SMTP_USERNAME=$3
SMTP_PASSWORD=$4
EMAILS=$5
TIMESTAMP=$6
LAST_TIMESTAMP=$7
TIME_DIFF=$8
BUILDS=$9

CURRENT_TIME=$(date --utc +%Y-%m-%dT%H:%M:%SZ)

# Prepare builds summary
BUILD_SUMMARY=$(echo "$BUILDS" | jq -c '.[]' | while read -r build; do
    BRANCH=$(echo "$build" | jq -r '.branch')
    AUTHOR=$(echo "$build" | jq -r '.author')
    TIMESTAMP=$(echo "$build" | jq -r '.timestamp')
    echo "<tr><td>$BRANCH</td><td>$AUTHOR</td><td>$TIMESTAMP</td></tr>"
done)

read -r -d '' HTML_BODY <<EOF
<!DOCTYPE html>
<html>
<head>
  <title>Activity Summary</title>
</head>
<body>
  <h1>Activity Summary</h1>
  <p><strong>Last Checked:</strong> $CURRENT_TIME</p>
  <table border="1">
    <tr>
      <th>Branch</th>
      <th>Author</th>
      <th>Timestamp</th>
    </tr>
    $BUILD_SUMMARY
  </table>
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
