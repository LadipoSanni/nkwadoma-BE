#!/bin/bash

SMTP_SERVER=$1
SMTP_PORT=$2
SMTP_USERNAME=$3
SMTP_PASSWORD=$4
EMAILS=$5
SUCCESSFUL_COMMITS=$6
UNSUCCESSFUL_COMMITS=$7
SUCCESSFUL_PRS=$8
UNSUCCESSFUL_PRS=$9

# HTML Template for Activity Summary Email
read -r -d '' HTML_BODY <<EOF
<!DOCTYPE html>
<html>
<head>
  <title>Activity Summary</title>
</head>
<body>
  <h1>Activity Summary Report</h1>
  <p><strong>Successful Commits:</strong> ${SUCCESSFUL_COMMITS}</p>
  <p><strong>Unsuccessful Commits:</strong> ${UNSUCCESSFUL_COMMITS}</p>
  <p><strong>Successful PRs:</strong> ${SUCCESSFUL_PRS}</p>
  <p><strong>Unsuccessful PRs:</strong> ${UNSUCCESSFUL_PRS}</p>
</body>
</html>
EOF

# Send Email
IFS=',' read -r -a email_array <<< "${EMAILS}"
for email in "${email_array[@]}"; do
  echo "$HTML_BODY" | curl --verbose --ssl-reqd \
    --url "smtps://${SMTP_SERVER}:${SMTP_PORT}" \
    --mail-from "$SMTP_USERNAME" \
    --mail-rcpt "$email" \
    --user "${SMTP_USERNAME}:${SMTP_PASSWORD}" \
    --upload-file -
done
