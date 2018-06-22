package me.augu.Astolfo;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("FieldCanBeLocal")
public class AstolfoConfig {
    @SerializedName("token")
    private String token = "";

    @SerializedName("prefix")
    private String prefix = "astolfo ";

    @SerializedName("weebSh")
    private String weebSh = "";

    @SerializedName("game")
    private String game = "astolfo help | astolfo inviteme";

    public String getToken() {
        return token;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getWeebShToken() {
        return weebSh;
    }

    public String getGame() {
        return game;
    }
}