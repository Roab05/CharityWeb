# CharityWeb

## Chatbot AI (Gemini) - V1

### Muc tieu
- Tra loi cau hoi ve du an, tien do, va quy trinh quyen gop.
- Su dung Gemini qua backend de khong lo API key tren frontend.

### API moi
- `POST /api/v1/chatbot/ask`
  - Request:
    - `question` (bat buoc)
    - `projectId` (tuy chon)
    - `conversationId` (tuy chon)
    - `locale` (tuy chon)
- `GET /api/v1/chatbot/suggestions?projectId=...`

### Cac bien cau hinh
Dat trong `application.properties`/`application-dev.properties`:
- `ai.gemini.api-key=${GEMINI_API_KEY:}`
- `ai.gemini.base-url`
- `ai.gemini.model`
- `ai.gemini.temperature`
- `ai.gemini.max-output-tokens`
- `ai.chatbot.timeout-ms`
- `ai.chatbot.max-question-length`
- `ai.chatbot.max-context-projects`
- `ai.chatbot.max-context-activities`
- `ai.chatbot.max-output-chars`
- `ai.chatbot.strict-upstream`
- `ai.chatbot.anonymous-rate-limit`
- `ai.chatbot.authenticated-rate-limit`
- `ai.chatbot.rate-window-seconds`

### Hanh vi fallback
- Khi `ai.chatbot.strict-upstream=false`: neu Gemini loi, he thong tra thong diep huong dan mac dinh.
- Khi `ai.chatbot.strict-upstream=true`: he thong nem loi upstream (503).

### Test nhanh
```powershell
Set-Location "D:\Project\LTW_CharityWeb\CharityWeb\CharityWebBackend"
$env:JAVA_HOME="C:\Program Files\Java\jdk-25"
$env:Path="$env:JAVA_HOME\bin;" + $env:Path
.\mvnw.cmd "-Dtest=ChatbotServiceImplTest,InteractionServiceImplTest" test
```

### Vi du request Postman
```json
{
  "question": "Lam sao de quyen gop cho du an nay?",
  "projectId": "your-project-id",
  "locale": "vi-VN"
}
```
