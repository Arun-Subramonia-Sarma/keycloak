export NS=yms-keycloak-26-1-4-code-new
kubectl port-forward svc/keycloak-http 9000 $AZ_DEV -n $NS