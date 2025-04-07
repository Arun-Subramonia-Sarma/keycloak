export NS=yms-keycloak-26-1-4-code-new
kubectl delete ns $NS $AZ_DEV
kubectl create namespace $NS $AZ_DEV
kubectl apply -f keycloak-secrets.yaml $AZ_DEV -n $NS
helm install keycloak codecentric/keycloakx $AZ_DEV -n $NS -f value-poc.yaml
#kubectl apply -f keycloak-ingress.yaml $AZ_DEV -n $NS
