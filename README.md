# Setup App in Docker

[Install Docker](https://www.docker.com/)

Gegebenenfalls andere container stoppen die relevante Ports benutzen. Ansonsten müssen die Ports(3000, 8080, 6379, 5432) in der .env oder der application.properties bzw. der docker-compose.yml angepasst werden.


In der application.properties Datei muss der openAI API key hinzugefügt werden.

```
quarkus.langchain4j.openai.api-key=sk-...
```

Dann können die Images gebaut werden und container gestartet werden.


```bash
docker-compose up --build -d
```

Um sich einloggen zu können muss dann initial ein request ausgeführt werden um Testuser zu erstellen. Hierzu kann man bspw. Postman oder curl benutzen.

```bash
curl --location --request POST 'localhost:8080/test-init/user'
```

Danach kann sich unter http://localhost:3000 eingeloggt werden.

```
teacher@test.de
test123

student@test.de
test123
```

# Setup Local

## Downloads

- [Node.js](https://nodejs.org/en/download/)
- [Git](https://git-scm.com/downloads)
- [Java](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
    - [Windows download link](https://corretto.aws/downloads/latest/amazon-corretto-21-x64-windows-jdk.msi), dabei Java als Pfadvariable setzen
    - [Mac tar](https://corretto.aws/downloads/latest/amazon-corretto-21-x64-macos-jdk.tar.gz) oder mit brew: `brew install --cask corretto@21`
- [Maven](https://maven.apache.org/download.cgi) 
- [Docker](https://www.docker.com/)


## Check installations

```bash
javac --version
mvn -v
node --version
npm --version
```

## Frontend

Innerhalb des Projekts mit `cd fe_eduprompt`
in den Frontend-Ordner wechseln.

Hier die Abhängigkeiten installieren und den dev-server starten.

```bash
npm install
npm run dev
```

Das Frontend läuft dann unter `http://localhost:3000`

## Backend

### Prereqs:


- Docker muss laufen
- JDK(21) muss instaliert und als Pfadvariable vorhanden sein
- Optional: Maven installiert und im Pfad

### Backend starten

In beliebigen Editor den openAI-API-Key hinzufügen.
Dieser wird in `.../eduPrompt/src/main/resources/application.properties` wie folgt gesetzt:

```
quarkus.langchain4j.openai.api-key=sk-...
```

Für das lokale Setup muss auch der markierte block für die Postgres and Redis DB auskommentiert werden, da quarkus diese für das development selbst startet:

```
############### DOCKER ######################
############### comment out for local development ######################
...
############### DOCKER ######################
```

Sobald der API-Key gesetzt ist kann das Backend gestartet werden. Hierzu mit `cd eduprompt`
in den Projektordner navigieren.

Falls Maven installiert und als Pfadvariable definiert ist kann die app mit folgenden Befehl gestartet werden:

```bash
mvn quarkus:dev
```

ansonsten kann es auch mit folgendem Befehl gestartet werden


```bash
./mvnw quarkus:dev
```

Nachdem der Service gestartet ist kann er unter `localhost:8080` erreicht werden.

Um sich über das Frontend einloggen zu können muss zunächst ein Request ausgeführt werden um Testuser zu erstellen.
Dieser kann unter `http://localhost:8080/q/dev-ui/io.quarkus.quarkus-smallrye-openapi/swagger-ui` gefunden werden oder mit Postman, curl etc. ausgeführt werden.

```
POST localhost:8080/test-init/user
```

Nachdem dieser request ausgeführt wurde, kann man sich beispielsweise mit dem User

```
user: user3@gmail.com
password: password
```
einloggen.

### Port


Der default Port von quarkus ist 8080. Falls ein anderer port gewollt ist kann dieser in der application.properties wie folgt geändert werden:
```
quarkus.http.port=9000
```


Falls der port angepasst wird muss auch im Frontend der port angepasst werden. Dies kann entweder über die Datei `constants.js` im root-Ordner des frontends gemacht werden:

```
const BE_BASE_URL = process.env.BE_URL || 'http://localhost:8080';
```
oder über eine `.env` Datei:

```
BE_URL=http://localhost:8080
```

