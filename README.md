# EmailReplier

A small Spring Boot service that generates email replies using an LLM (configured via Gemini API).

**Features**
- Generate a reply given an email body and optional tone.
- Simple REST API with a health endpoint.

**Prerequisites**
- Java 21
- Maven (or use the included Maven wrapper)

**Build & Run (Windows)**
1. Build:

```powershell
.\mvnw.cmd -DskipTests package
```

2. Run with the wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Or run the packaged jar:

```powershell
java -jar target\EmailReplier-0.0.1-SNAPSHOT.jar
```

**Configuration**
Set the Gemini API URL and key in `src/main/resources/application.properties` or via environment/system properties.

Properties (in `application.properties`):

```
gemini.api.url=
gemini.api.key=
```

You can also pass them as JVM properties:

```powershell
.\mvnw.cmd spring-boot:run -Dgemini.api.url="https://..." -Dgemini.api.key="KEY"
```

**API**
- Health check

  - Method: GET
  - URL: `/public/api`
  - Response: `Everything works fine`

- Generate email reply

  - Method: POST
  - URL: `/public/api/emailReply`
  - Content-Type: `application/json`
  - Request body JSON schema:

```json
{
  "emailResponse": "<original email text>",
  "tone": "<optional tone, e.g. friendly, formal>"
}
```

  - Example curl:

```bash
curl -X POST http://localhost:8080/public/api/emailReply \
  -H "Content-Type: application/json" \
  -d '{"emailResponse":"Hi — are we still on for tomorrow?","tone":"friendly"}'
```

  - Response: plain text containing the generated reply.

**Implementation notes**
- The service uses a `WebClient` bean to POST JSON to the configured Gemini URL and expects a JSON response containing `candidates[0].content.parts[0].text`.
- Request/response mapping is defined in `src/main/java/com/emailreplier/EmailReplier/entity/EmailEntity.java` and `emailService`.


**Files**
- `src/main/java/com/emailreplier/EmailReplier/controller/emailController.java` — REST endpoints
- `src/main/java/com/emailreplier/EmailReplier/service/emailService.java` — Gemini integration logic
- `src/main/resources/application.properties` — config values

---
Created for the EmailReplier project.
