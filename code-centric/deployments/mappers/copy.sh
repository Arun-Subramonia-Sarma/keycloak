export BASE_FOLDER=~/Dev/Java/keycloak
export DOCKER_FOLDER=$BASE_FOLDER/code-centric/deployments/Docker
rm $DOCKER_FOLDER/*.jar
cp $BASE_FOLDER/company-role-mapper/target/*.jar $DOCKER_FOLDER
cp $BASE_FOLDER/test-role-mapper/target/*.jar $DOCKER_FOLDER