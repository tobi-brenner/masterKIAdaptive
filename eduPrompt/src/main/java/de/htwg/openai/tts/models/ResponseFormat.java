package de.htwg.openai.tts.models;

import com.google.gson.annotations.SerializedName;

public enum ResponseFormat {
    @SerializedName("mp3") MP3,
    @SerializedName("opus") OPUS,
    @SerializedName("aac") AAC,
    @SerializedName("flac") FLAC
}
