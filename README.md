# URL Shortener Service

## Run
```bash
./gradlew bootRun
```
(ensure no process is bound to port 8080)
- App: http://localhost:8080
- JaCoCo report: `build/reports/jacoco/test/html/index.html` (after build or test)
- Health check: http://localhost:8080/actuator/health
- H2 Console: http://localhost:8080/h2-console (JDBC: `jdbc:h2:mem:urls`)
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI spec: http://localhost:8080/v3/api-docs

## Test
```bash
./gradlew clean test
```

## API Quick Reference
Note: See Swagger UI or Open API spec for full API spec.
### Create entry
example request:
```bash
 curl http://localhost:8080/short.ly -H 'Content-Type: application/json' -d '{"url":"https://www.originenergy.com.au/electricity-gas/plans.html"}'
```
example response:

`{"id":"a1B2c3","url":"https://www.originenergy.com.au/electricity-gas/plans.html"}`

### Fetch entry
example request (following from create request above):
```bash
 curl http://localhost:8080/short.ly/a1B2c3 
```
API will redirect the client to `https://www.originenergy.com.au/electricity-gas/plans.html`

### Get entry info
example request (following from create request above):
```bash
 curl http://localhost:8080/short.ly/a1B2c3/info 
```
example response:

`{"id":"a1B2c3","url":"https://www.originenergy.com.au/electricity-gas/plans.html"}`

## Potential extensions
- Flyway or Liquibase for DB migration management
- Use a production-like DB, and Dockerise it in integration tests to retain simplicity of testing
- CI/CD pipeline for deployment to cloud
- API Gateway, DNS, Web server, Load balancer, WAF considerations
- Monitor health check endpoint and alerting with SRE tool
- DB backups and DR strategy