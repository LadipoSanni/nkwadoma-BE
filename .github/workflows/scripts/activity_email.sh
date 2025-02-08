#!/bin/bash 

SMTP_SERVER=$1
SMTP_PORT=$2
SMTP_USERNAME=$3
SMTP_PASSWORD=$4
EMAILS=$5
CURRENT_TIMESTAMP=$6
LAST_TIMESTAMP=$7
TIME_DIFF=$8
BUILDS=$9
COMMIT_AUTHOR=${10}
COMMIT_MESSAGE=${11}

CURRENT_TIME=$(date --utc +%Y-%m-%dT%H:%M:%SZ)

# Convert CURRENT_TIME to seconds since epoch for comparison
CURRENT_TIME_SECONDS=$(date --utc --date="$CURRENT_TIME" +%s)

# Filter builds that occurred in the last 4 hours (14400 seconds)
RECENT_BUILDS=$(echo "$BUILDS" | jq -c ".[] | select((($CURRENT_TIME_SECONDS - (strptime(.timestamp) | mktime)) <= 14400))")

# Count successful and failed PR builds in the last 4 hours
SUCCESSFUL_PR_BUILDS=$(echo "$RECENT_BUILDS" | jq '[select(.status == "success")] | length')
FAILED_PR_BUILDS=$(echo "$RECENT_BUILDS" | jq '[select(.status == "failure")] | length')

# Build detail rows
BUILD_DETAILS=""
while IFS= read -r build; do
    TIMESTAMP=$(echo "$build" | jq -r '.timestamp')
    AUTHOR=$(echo "$build" | jq -r '.author')
    MESSAGE=$(echo "$build" | jq -r '.message')
    BUILD_DETAILS+="<tr><td>${TIMESTAMP}</td><td>${AUTHOR}</td><td>${MESSAGE}</td></tr>"
done < <(echo "$RECENT_BUILDS" | jq -c '.')

# HTML email body with a table
read -r -d '' HTML_BODY <<EOF
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
  <p><strong>Commits Verified:</strong> ${CURRENT_TIMESTAMP}</p>
  <p><strong>Merged Pull Requests:</strong> ${LAST_TIMESTAMP}</p>
  <p><strong>Failed PR Builds (Last 4 hours):</strong> ${FAILED_PR_BUILDS}</p>
  <p><strong>Successful PR Builds (Last 4 hours):</strong> ${SUCCESSFUL_PR_BUILDS}</p>
  <p><strong>Last Commit Author:</strong> ${COMMIT_AUTHOR}</p>
  <p><strong>Last Commit Message:</strong> ${COMMIT_MESSAGE}</p>
  <p><strong>Build Details:</strong></p>
  <table>
    <tr>
      <th>Timestamp</th>
      <th>Author</th>
      <th>Message</th>
    </tr>
    ${BUILD_DETAILS}
  </table>
  <p>Checked at: ${CURRENT_TIME}</p>
  <p>Keep up the great work!</p>
</body>
</html>
EOF

# Prepare and send the email
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
