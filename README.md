Important Setup Step

After cloning this project, follow the steps below to ensure seamless development environment:

1. Docker Compose
2. Keycloak
3. Database

DOCKER COMPOSE

prerequisites
- Install Docker and Docker Compose
- Ensure Docker is running on your machine

How to Start
Navigate to the project root directory where  the docker-compose.yml file is located.
Run the command  following commands:

To start the containers:
docker-compose up -d

To stop the containers:
docker-compose down


HOW TO SPIN UP KEYCLOAK
Ensure your docker containers are running

Access keycloak
Open browser and go to http://localhost:8090
Login using the credentials defined in the docker-compose file for the Keycloak service.

Create a Realm:
- Log in to the Keycloak admin console.
- Click on the dropdown in the top-left corner and select "Add Realm".
- Enter a name for your realm(e.g., my-ream) and click "Create".

Create a Client:
- Navigate to the "Clients" section in your realm.
- Click "Create" and enter a client ID.
- Enable the following
- client authentication
- Enable authorization
- click on save

Get Client Credentials:
- Go to the Credentials tab of the client.
- Copy the Client ID and Client Secret.
- Paste the client ID and client secret next to their corresponding keys in the application.properties file.
- keycloak.client-id=<your-client-id>
- keycloak.client-secret=<your-client-secret>

Add Roles:
- Click on the client
- Click Realm roles in the left sidebar
Click "Add Role" and define the necessary roles (e.g., PORTFOLIO_MANAGER, ORGANIZATION_ADMIN, FINANCIER, LOANEE)


Thank you!
