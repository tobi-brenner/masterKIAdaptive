package de.htwg.openai.tts;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.htwg.filehandler.utils.TextUtils;
import de.htwg.openai.tts.models.TTSRequest;
import de.htwg.openai.tts.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class TextToSpeech {
    private final Logger logger = LoggerFactory.getLogger(TextToSpeech.class);

    private static final String TTS_URL = "https://api.openai.com/v1/audio/speech";
    //    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String OPENAI_API_KEY = "";

    public final static String TTS_1 = "tts-1";
    public final static String TTS_1_HD = "tts-1-hd";

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public byte[] generateMp3(TTSRequest ttsRequest) {
        String postBody = gson.toJson(ttsRequest);
        logger.info("postBody = {}", postBody);
        System.out.println(postBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TTS_URL))
                .header("Authorization", "Bearer %s".formatted(OPENAI_API_KEY))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .build();
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<byte[]> response =
                    client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            byte[] body = response.body();
            String fileName = FileUtils.writeSoundBytesToFile(body);
            logger.info("Saved {} to {}", fileName, FileUtils.AUDIO_RESOURCES_PATH);
            response.headers().map().forEach((k, v) -> logger.info("Header: {} = {}", k, v));
            return body;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] generateConcatenatedMp3(TTSRequest templateRequest, String fullText) {
        List<String> textChunks = TextUtils.splitText(fullText, 4096);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (String chunk : textChunks) {
            TTSRequest request = new TTSRequest(templateRequest.model(), chunk, templateRequest.voice(),
                    templateRequest.responseFormat(), templateRequest.speed());
            byte[] audioChunk = generateMp3(request);
            try {
                outputStream.write(audioChunk);
            } catch (IOException e) {
                logger.error("Error writing to output stream", e);
            }
        }

        return outputStream.toByteArray();
    }

//    public void playMp3UsingJLayer(String fileName) {
//        var buffer = new BufferedInputStream(
//                Objects.requireNonNull(getClass().getClassLoader()
//                        .getResourceAsStream("audio/%s".formatted(fileName))));
//        try {
//            Player player = new Player(buffer);
//            player.play();
//        } catch (JavaLayerException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void createAndPlay(String text, Voice voice) {
//        TTSRequest ttsRequest = new TTSRequest(TTS_1_HD,
//                text.replaceAll("\\s+", " ").trim(), voice);
//        byte[] bytes = generateMp3(ttsRequest);
//        var bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(bytes));
//        try {
//            new Player(bufferedInputStream).play();
//        } catch (JavaLayerException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
