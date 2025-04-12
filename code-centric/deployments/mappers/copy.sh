export BASE_FOLDER=../../..
export DOCKER_FOLDER=$BASE_FOLDER/code-centric/deployments/Docker
rm $DOCKER_FOLDER/*.jar
# cp $BASE_FOLDER/role_mapper/company-role-mapper/target/*.jar $DOCKER_FOLDER
# cp $BASE_FOLDER/role_mapper/hello-role-mapper/target/*.jar $DOCKER_FOLDER
cp $BASE_FOLDER/role_mapper/fk-keycloak-ext/target/*.jar $DOCKER_FOLDER