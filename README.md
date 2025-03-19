Important Setup Step

For New Backend-Engineer, QA-Engineer, and Frontend-Engineer
To clone this project, you are required to have git installed and running on your Laptop or any development device.
Open powershell on your device then type this command: git clone https://github.com/nkwadoma/nkwadoma-BE.git, and press enter

After cloning this project, follow the steps below to ensure seamless development environment:

Pre-requisites:
1. Docker 
2. Docker Compose
3. Keycloak Setup
4. Database

DOCKER and DOCKER COMPOSE Installation
- For windows OS:
    - Option 1: Use Windows subsystem for Linux(wsl) option
        - install windows sub-system for linux using this command: wsl --install
        - confirm wsl installed with the command: wsl --version
        - to use the windows sub-system for linux(wsl) send this command: wsl (and press enter)
          configure your wsl to use password if you want.(N:B sudo will ask you for this password)
        - to install Docker, use the following commands : sudo apt update, sudo apt install docker.io
        - check for successful installation with these commands: docker --version, docker-compose --version.
        - install docker compose seperately if docker compose is not installed.
    - Option 2: Use Docker Desktop download option
        https://docs.docker.com/desktop/setup/install/windows-install/
        Recommended: install the Docker Desktop for Windows-x86_64
        Run the downloaded installer.
        Follow the installation steps.
        Enable WSL 2 backend during installation (recommended)
        to check for successful installation use this commands: docker --version, docker-compose --version.
      N:B 
        Docker Compose is included in Docker Desktop for Windows and macOS. On Linux, it requires separate installation.
        Recommended: Enable Docker to start on boot via Docker settings
        Please ensure you change the port in your .env inside the application-dev.properties and docker compose file to avoid port conflict!

- For Linux OS
    - Open powershell on your linux machine
    - Option 1: Copy and Paste this command in your terminal and press enter: sudo apt update && sudo apt install docker.io
    - Option 2:
        - In your IDE terminal Navigate to the Script Directory; copy, paste, and enter this command: cd .github/workflows/scripts/
        - Make the script Executable chmod +x install_docker_and_compose.sh
        - Then enter this command in your terminal to install docker and docker compose: sudo bash install_docker_and_compose.sh
            - Or If you don’t want to navigate manually, run this command: sudo bash .github/workflows/scripts/install_docker_and_compose.sh

- Ensure Docker is running on your machine
   type the command: docker ps in your terminal and press enter. if docker is running you should see this in your terminal
    CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES

  
For Frontend-Engineer and QA-Engineer
To pull the backend code from the registry require credentials: 
credentials will be provided for you by the cloud team.
once the credentials are provided and you authenticate successfully
enter this command in the terminal: docker pull 357586184453.dkr.ecr.us-east-1.amazonaws.com/nkwadoma:alpha
Navigate back to the project root directory with cd to the project "/nkwadoma-BE" path, where  the docker-compose.yml file is located.
Run the following commands: To start the containers;

docker-compose up -d

To stop the containers;
docker-compose down


KEYCLOAK SETUP
Please ensure you change the port in your .env inside the application-dev.properties and docker-compose file to avoid port conflict!
Ensure all the application services in your docker compose are running the backend which might exit because it needs keycloak up and running
Access keycloak
Open browser and go to http://localhost:8090 (Or the :port specified for your keycloak server)
Login using the credentials defined in the docker-compose file for the Keycloak service.

Create a Realm:
- Log in to the Keycloak admin console.
- Click on the dropdown in the top-left corner and select "Add Realm".
- Enter a name for your realm(e.g., nkwadoma-realm) and click "Create".

Create a Client:
- Navigate to the "Clients" section in your realm.
- Click "Create" and enter a client ID.
- Enable client authentication
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
Click "Add Role" and define the necessary roles (e.g. SPONSOR, DONOR, PORTFOLIO_MANAGER, ORGANIZATION_ADMIN, FINANCIER, LOANEE, ENDOWER)

DATABASE SETUP
use pg_admin url specified in the .env to access the database UI.
Ask the backend lead to provide a query to pre populate the database for defaul values

Now you are Ready for Local Development.
check the following link for the Project Environment Diagram
- https://csgpr805b4tu.sg.larksuite.com/wiki/WZ6hw0mItisAECkSQ1kl4W2Vglc
  
Thank you!
The Cloud Team
