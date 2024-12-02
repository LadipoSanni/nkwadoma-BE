#!/bin/bash

set -e  # Exit on error
set -o pipefail  # Fail if any part of a pipe fails

# Input arguments
SMTP_SERVER="$1"
SMTP_PORT="$2"
SMTP_USERNAME="$3"
SMTP_PASSWORD="$4"
EMAILS="$5"
CURRENT_TIMESTAMP="$6"
LAST_TIMESTAMP="$7"
TIME_DIFF="$8"
BUILDS="$9"
COMMIT_AUTHOR="${10}"
COMMIT_MESSAGE="${11}"

CURRENT_TIME=$(date --utc +%Y-%m-%dT%H:%M:%SZ)

# Count successful and failed PR builds
SUCCESSFUL_PR_BUILDS=$(echo "$BUILDS" | jq '[.[] | select(.status == "success")] | length')
FAILED_PR_BUILDS=$(echo "$BUILDS" | jq '[.[] | select(.status == "failure")] | length')

# Build HTML rows for build details
BUILD_DETAILS=$(echo "$BUILDS" | jq -r '.[] | "<tr><td>\(.timestamp)</td><td>\(.author)</td><td>\(.message)</td></tr>"')

# HTML email body
HTML_BODY=$(cat <<EOF
<!DOCTYPE html>
<html>
<head>
  <title>Activity Summary</title>
  <style>
    table {
      width: 100%;
      border-collapse: collapse;
    }
    th, td {
      border: 1px solid #ddd;
      padding: 8px;
    }
    th {
      background-color: #f2f2f2;
      text-align: left;
    }
  </style>
</head>
<body>
  <h1>Activity Summary</h1>
  <p><strong>Current Timestamp:</strong> ${CURRENT_TIMESTAMP}</p>
  <p><strong>Last Activity Timestamp:</strong> ${LAST_TIMESTAMP}</p>
  <p><strong>Failed PR Builds:</strong> ${FAILED_PR_BUILDS}</p>
  <p><strong>Successful PR Builds:</strong> ${SUCCESSFUL_PR_BUILDS}</p>
  <p><strong>Last Commit Author:</strong> ${COMMIT_AUTHOR}</p>
  <p><strong>Last Commit Message:</strong> ${COMMIT_MESSAGE}</p>
  <p><strong>Build Details:</strong></p>
  <table>
    <thead>
      <tr>
        <th>Timestamp</th>
        <th>Author</th>
        <th>Message</th>
      </tr>
    </thead>
    <tbody>
      ${BUILD_DETAILS}
    </tbody>
  </table>
  <p>Checked at: ${CURRENT_TIME}</p>
  <p>Keep up the great work!</p>
</body>
</html>
EOF
)

# MIME email structure
MIME_EMAIL=$(cat <<EOF
From: "Build Tracker" <${SMTP_USERNAME}>
To: ${EMAILS}
Subject: 🛠️ Activity Summary Report
Content-Type: text/html; charset=UTF-8

${HTML_BODY}
EOF
)

# Send email to each recipient
IFS=',' read -r -a email_array <<< "${EMAILS}"
for email in "${email_array[@]}"; do
  echo "$MIME_EMAIL" | curl --verbose --ssl-reqd \
    --url "smtps://${SMTP_SERVER}:${SMTP_PORT}" \
    --mail-from "${SMTP_USERNAME}" \
    --mail-rcpt "${email}" \
    --user "${SMTP_USERNAME}:${SMTP_PASSWORD}" \
    --upload-file -
done
