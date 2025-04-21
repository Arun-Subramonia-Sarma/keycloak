export NS=yms-keycloak-26-1-4-code-new
kubectl delete ns $NS $AZ_DEV
kubectl create namespace $NS $AZ_DEV
kubectl apply -f keycloak-secrets.yaml $AZ_DEV -n $NS
helm install postgres bitnami/postgresql $AZ_DEV -n $NS -f value-postgres.yaml 
helm install keycloak codecentric/keycloakx $AZ_DEV -n $NS -f value-keycloak.yaml
kubectl apply -f keycloak-ingress.yaml $AZ_DEV -n $NS
kubectl port-forward svc/keycloak-http 8080 $AZ_DEV -n $NS
