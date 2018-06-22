package me.augu.Astolfo;

public class AstolfoInfo {
    public static final String VERSION = "@version@";
    public static final String USER_AGENT = "JDA/DiscordBot (v" + VERSION + ")";

    public static String getVersion() {
        return VERSION;
    }

    public static String getUserAgent() {
        return USER_AGENT;
    }
}