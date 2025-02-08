# Set up JDK
echo "Setting up JDK 17..."
# Add commands to set up JDK if necessary

# Maven Package
echo "Running Maven Package..."
if mvn -B clean verify; then
    BUILD_SUCCESS=true
else
    BUILD_SUCCESS=false
    echo "Maven verify failed, but continuing to generate site..."
fi

# Generate Maven site
echo "Generating Maven site..."
mvn site || echo "Maven site generation failed, but continuing to upload..."

# Configure AWS CLI
echo "Configuring AWS CLI..."
aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
aws configure set region $AWS_REGION

# Debugging output
echo "AWS CLI Version:"
aws --version
echo "AWS S3 Bucket: $S3_BUCKET"
echo "Project Name: $PROJECT_NAME"

# Deploy to S3
echo "Deploying to S3..."
if aws s3 sync target/site s3://${S3_BUCKET}/${PROJECT_NAME}/new-reports; then
    echo "Successfully uploaded Maven site to S3"
else
    echo "Failed to upload Maven site to S3"
    exit 1
fi

# If build failed, exit here
if [ "$BUILD_SUCCESS" = false ]; then
    echo "Build failed. Exiting after S3 upload."
    exit 1
fi

# The rest of the script only runs if the build was successful

# Upload artifact
echo "Uploading artifact..."
# Add commands to upload artifact if necessary

# Login to Amazon ECR
echo "Logging in to Amazon ECR..."
aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}

# Build, tag, and push image to Amazon ECR
echo "Building and pushing Docker image..."
docker build -t ${ECR_REPO}:$GITHUB_SHA .
DOCKER_TAG=${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:latest
docker tag ${ECR_REPO}:$GITHUB_SHA $DOCKER_TAG
docker tag ${ECR_REPO}:$GITHUB_SHA ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:$GITHUB_SHA

docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:latest
docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:$GITHUB_SHA

# Update ECS service
echo "Updating ECS service..."
aws ecs update-service --cluster ${CLUSTER} --service ${SERVICE} --force-new-deployment

echo "Script completed successfully."
