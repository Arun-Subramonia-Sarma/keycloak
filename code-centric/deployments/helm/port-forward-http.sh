export NS=yms-keycloak-26-1-4-code-new
kubectl port-forward svc/keycloak-http 8080:80 $AZ_DEV -n $NS