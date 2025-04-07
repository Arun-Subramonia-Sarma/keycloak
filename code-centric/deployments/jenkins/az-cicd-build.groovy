@Library('jenkins-shared-library') _

stage('Build Push') {
  withCredentials([string(credentialsId: 'jenkins_github_token', variable: 'TOKEN')]) {
    buildPush(
      service: 'yms-keycloak-optimized',
      giturl: "$GITURL",
      branch: "$GITBRANCH",
      dockerfile: "$DOCKERFILE",
      imageTag: "$IMAGE_TAG",
      imageRepo: 'facility/dyf/yms-keycloak-optimized',
      extraTags: ['develop-latest'],
      kanikoRequestMemory: '512Mi',
      azureEnv: "$AZURE_ENV",
      cache: 'false',
      REGISTRY_NAME: "$REGISTRY_NAME"
    )
  }
}

