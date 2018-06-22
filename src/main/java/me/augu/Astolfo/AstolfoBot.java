package me.augu.Astolfo;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import com.mongodb.MongoClient;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.managers.AudioManager;
import me.augu.Astolfo.audio.AstolfoMusicManager;
import me.augu.Astolfo.commands.CommandManager;
import me.augu.Astolfo.commands.animals.*;
// import me.augu.Astolfo.commands.fun.*;
// import me.augu.Astolfo.commands.general.*;
// import me.augu.Astolfo.commands.music.*;
// import me.augu.Astolfo.commands.owner.*;
// import me.augu.Astolfo.commands.reactions.*;
// import me.augu.Astolfo.commands.utility.*;
// import me.augu.Astolfo.commands.weebs.*;
import me.augu.Astolfo.listeners.EventListener;
import me.augu.Astolfo.utils.DatabaseUtils;
import me.augu.Astolfo.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.util.concurrent.ConcurrentHashMap;
import java.io.File;

public class AstolfoBot {
    public static CommandManager commandManager = new CommandManager();
    public static Logger log = LoggerFactory.getLogger("AstolfoBot");
    public static Color color = Color.decode("#FFB6C1");
    public static Long owner = 280158289667555328L;
    public static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private static ConcurrentHashMap<Long, AstolfoMusicManager> players = new ConcurrentHashMap<>();
    public static AstolfoConfig config;
    public static JDA jda;

    public static void main(String[] args) throws Exception {
        Thread.currentThread().setName("Astolfo-Main");
        printBanner();
        config = IOUtils.readConfig(AstolfoConfig.class, new File("config.json"));

        MongoClient client = DatabaseUtils.client;
        Thread.sleep(1000);

        playerManager.setPlayerCleanupThreshold(30000);
        playerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.LOW);
        playerManager.getConfiguration().setOpusEncodingQuality(9);
        playerManager.getConfiguration().setFilterHotSwapEnabled(true);

        YoutubeAudioSourceManager yt = new YoutubeAudioSourceManager();
        yt.setPlaylistPageCount(Integer.MAX_VALUE);
        playerManager.registerSourceManager(yt);
        AudioSourceManagers.registerRemoteSources(playerManager);

        JDABuilder builder = new JDABuilder(AccountType.BOT)
                .setToken(config.getToken())
                .addEventListener(new EventListener())
                .setGame(Game.listening(config.getPrefix() + "help | " + config.getPrefix() + "inviteme"));

        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch");

        if ((os.contains("windows") && os.contains("linux")) && !arch.equalsIgnoreCase("arm") && !arch.equalsIgnoreCase("arm-linux")) {
            log.info("System supports NAS! Now enabling NAS....");
            builder.setAudioSendFactory(new NativeAudioSendFactory());
        }

        jda = builder.buildAsync();
        // registerCommands();
    }

    private static void printBanner() {
        String banner = IOUtils.readFile("banner.txt");
        String os = System.getProperty("os.name");
        String arch = System.getProperty("os.arch");

        log.info("\n" + banner + "\n" +
                "Astolfo " + AstolfoInfo.getVersion() +
                " | JDA " + JDAInfo.VERSION +
                " | Lavaplayer " + PlayerLibrary.VERSION +
                " | " + System.getProperty("sun.arch.data.model") + "-bit JVM" +
                " | " + os + " " + arch + "\n");
    }


    private static void registerCommands() {
        // Animals
        commandManager.register(new AlpacaCommand());
            /* .register(new BirbCommand())
            .register(new CatCommand())
            .register(new DogCommand())
            .register(new LionCommand())
            .register(new PandaCommand())
            .register(new PenguinCommand())
            .register(new RedPandaCommand())
            .register(new TigetCommand());

        // Fun
        commandManager.register(new AkinatorCommand())
            .register(new BeerCommand())
            .register(new DadJokeCommand())
            .register(new DiscordMemesCommand())
            .register(new NekosCommand())
            .register(new WhyCommand())
            .register(new XKDCCommand());

        // General
        commandManager.register(new AboutCommand())
            .register(new HelpCommand())
            .register(new InviteMeCommand())
            .register(new PingCommand())
            .register(new SettingsCommands()
                .new SetPrefixCommand()
                .new ResetPrefixCommand()
            )
            .register(new StatisticsCommand());

        // Music
        commandManager.register(new ClearQueueCommand())
            .register(new MoveCommand())
            .register(new NowPlayingCommand())
            .register(new PlayCommand())
            .register(new QueueCommand())
            .register(new SeekCommand())
            .register(new SelectCommand())
            .register(new ShuffleCommand())
            .register(new SkipCommand())
            .register(new StopCommand())
            .register(new UnqueueCommand())
            .register(new VolumeCommand());

        // Owner
        commandManager.register(new OwnerCommands()
            .new EvalCommand()
        );

        // Reactions
        CommandManager.register(new CuddleCommand())
            .register(new FeedCommand())
            .register(new HugCommand())
            .register(new KissCommand())
            .register(new PatCommand())
            .register(new PokeCommand());

        // Utility
        commandManager.register(new ColorCommand())
            .register(new RoleInfoCommand())
            .register(new ServerinfoCommand())
            .register(new UserInfoCommand());

        // Weebs
        commandManager.register(new AstolfoCommand())
            .register(new MikuCommand())
            .register(new NoelCommand())
            .register(new RemCommand())
            .register(new TakagiCommand());*/
    }

    public static ConcurrentHashMap<Long, AstolfoMusicManager> getPlayers() {
        return players;
    }

    public static boolean hasPlayer(long guildId) {
        return players.containsKey(guildId);
    }

    public static AstolfoMusicManager getPlayer(AudioManager manager) {
        AstolfoMusicManager musicManager = players.computeIfAbsent(manager.getGuild().getIdLong(),
                v -> new AstolfoMusicManager(manager.getGuild().getIdLong(), playerManager.createPlayer()));

        if (manager.getSendingHandler() == null) manager.setSendingHandler(musicManager);

        return musicManager;

    }

    public static void removePlayer(long guildId) {
        if (AstolfoBot.hasPlayer(guildId)) {
            players.remove(guildId).cleanup();
        }
    }
}