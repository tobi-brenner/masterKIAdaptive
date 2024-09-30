## frontend

nodejs und npm müssen installiert sein

cd fe_eduprompt

npm install 

npm run dev

ist dann erreichbar auf localhost:3000


## backend

docker muss isntalliert sein und laufen für die dev container
jdk: corretto-21

cd /eduprompt

OpenAI API Key muss in der application.properties datei angegeben werden
quarkus.langchain4j.openai.api-key=sk-****

mit quarkus dev wenn installiert 

oder mit IDE wie intellij starten 

läuft dann auf localhost:8080


swagger ui ist unter:

http://localhost:8080/q/dev-ui/io.quarkus.quarkus-smallrye-openapi/swagger-ui


Hier muss für eine user erstellung der ``` /test-init/user ```

nachdem die user erstellt sind kann man sich zB mit

user4@gmail.com und frei gewähltem passwort einloggen