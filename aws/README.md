# aws

AWS serverless patterns — Lambda handlers, EventBridge routing, and DynamoDB single-table design, modeled in Java.

## Packages

### `lambda`
- **`HelloHandler`** — minimal `RequestHandler<Map<String,String>, String>` using the AWS Lambda Java runtime (`com.amazonaws.services.lambda.runtime`); reads `name`, logs via `context.getLogger()`, returns a greeting.
- **`LambdaColdStartOptimization`** — demonstrates the cold-start init pattern: heavy setup (config load) happens once in the constructor and is reused across warm invocations; the handler stays fast and reports init vs handler timing. Doc-comment also covers SnapStart, provisioned concurrency, and GraalVM native image.

### `events`
- **`EventBridgePattern`** — in-memory EventBridge model: `CloudEvent` (source, detail-type, detail), `Rule`s with `Predicate`-based content matching, and per-target queues. `putEvent` routes through all matching rules. `ecommerceSetup()` shows choreography, content-based routing, and fan-out (order → fulfillment + analytics, high-value → fraud detection).

### `dynamodb`
- **`SingleTableDesign`** — single-table design simulated in-memory: composite `PK`+`SK` keys, `getItem` (point lookup) and `query` (partition scan with optional SK-prefix filter), and overloaded keys (`USER#id` / `PROFILE` | `ORDER#id`). E-commerce helpers cover create-user, place-order, get-profile, and list-orders access patterns.

## Notes

EventBridge and DynamoDB classes are in-memory simulations that demonstrate the access/routing patterns; only the Lambda handlers depend on the AWS SDK. Integration against real AWS services (via Testcontainers/LocalStack) lives in the `integration-tests` module.

### Build note

`LambdaColdStartOptimization.java` uses **Java 25 unnamed-variable syntax** (`catch (InterruptedException _)`), so it is **excluded from Spotless formatting** (the formatter runs on JDK 21 and cannot parse the preview syntax).
