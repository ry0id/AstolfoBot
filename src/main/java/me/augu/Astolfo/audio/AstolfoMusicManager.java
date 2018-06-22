package me.augu.Astolfo.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.AudioManager;
import me.augu.Astolfo.AstolfoBot;

import java.util.LinkedList;
import java.util.Random;

public class AstolfoMusicManager extends AudioEventAdapter implements AudioSendHandler {
    public AudioPlayer player;
    private AudioFrame frame;
    private final Random selector = new Random();
    private final LinkedList<AudioTrack> queue = new LinkedList<>();
    private Long channelID;
    private Long guildID;
    private boolean shouldAnnounce = true;
    private RepeatMode mode = RepeatMode.NONE;
    private boolean shuffle = false;
    private AudioTrack current = null;
    public int trackPacketLoss = 0;
    public int trackPackets = 0;

    public AstolfoMusicManager(Long guildId, AudioPlayer player) {
        this.guildID = guildId;
        this.player = player;
        player.addListener(this);
    }

    public boolean addToQueue(AudioTrack track, Long userId) {
        track.setUserData(userId);

        if (!player.startTrack(track, true)) {
            queue.offer(track);
            return true;
        }

        return false;
    }

    public void stop() {
        if (isPlaying()) {
            queue.clear();
            next();
        }
    }

    public boolean isPlaying() {
        return player.getPlayingTrack() != null;
    }

    public LinkedList<AudioTrack> getQueue() {
        return queue;
    }

    public String getRepeatMode() {
        return mode.toString().toLowerCase();
    }

    public boolean isShuffled() {
        return shuffle;
    }

    public boolean toggleShuffle() {
        return shuffle = !shuffle;
    }

    public void setRepeat(RepeatMode owo) {
        owo = mode;
    }

    public void setChannel(Long channelId) {
        this.channelID = channelId;
    }

    public void setAnnounce(Boolean owo) {
        this.shouldAnnounce = owo;
    }

    public void next() {
        AudioTrack track = null;

        if (mode == RepeatMode.ALL && current != null) {
            AudioTrack r = current.makeClone();
            r.setUserData(current.getUserData());
            queue.offer(r);
        }

        if (mode == RepeatMode.SINGLE && current != null) {
            track = current.makeClone();
            track.setUserData(current.getUserData());
        } else if (!queue.isEmpty()) {
            track = shuffle ? queue.remove(selector.nextInt(queue.size())) : queue.poll();
        }

        if (track != null) {
            player.startTrack(track, false);
        } else {
            track = null;
            player.stopTrack();

            Guild guild = AstolfoBot.jda.getGuildById(guildID);

            if (guild == null) {
                // AstolfoBot.removePlayer(guildID);
                return;
            }

            AudioManager audioManager = guild.getAudioManager();

            if (audioManager.isConnected() || audioManager.isAttemptingToConnect()) {
                audioManager.closeAudioConnection();

                announce("Queue has ended!", ":eject: **-** If you like my Music module, (If you want) Donate to me on [Patreon](https://patreon.com/ohlookitsAugust) for perks and stuff:tm:");
            }
        }
    }

    private void announce(String title, String description) {
        if (!shouldAnnounce || channelID == null)
            return;

        TextChannel channel = AstolfoBot.jda.getTextChannelById(channelID);

        if (channel == null)
            return;

        channel.sendMessage(new EmbedBuilder()
            .setColor(AstolfoBot.color)
            .setTitle(title)
            .setDescription(description)
            .build()).queue(null, error -> AstolfoBot.log.error("Error has occured while posting a track announcement:", error));
    }

    public void cleanup() {
        queue.clear();
        player.destroy();

        Guild guild = AstolfoBot.jda.getGuildById(guildID);

        if (guild != null) {
            guild.getAudioManager().setSendingHandler(null);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason reason) {
        trackPacketLoss = 0;
        trackPackets = 0;

        if (reason.mayStartNext)
            next();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        player.setPaused(false);

        if (track == null || !track.getIdentifier().equals(track.getIdentifier())) {
            track = track;
            announce("Now Playing", track.getInfo().title);
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException err) {
        if (mode != RepeatMode.NONE)
            mode = RepeatMode.NONE;

        announce("Error while playing", "Playback of **" + track.getInfo().title + "** has errored:\n" + err.getLocalizedMessage());
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long threasholdMs) {
        if (mode != RepeatMode.NONE)
            mode = RepeatMode.NONE;

        announce("A track has been stuck", "Astolfo has detected a stuck track, now playing the next song in the queue.");
        next();
    }

    @Override
    public boolean canProvide() {
        frame = player.provide();

        if (!player.isPaused()) {
            if (frame == null) {
                trackPacketLoss++;
            } else {
                trackPackets++;
            }
        }

        return frame != null;
    }

    @Override
    public byte[] provide20MsAudio() {
        return frame.getData();
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    public enum RepeatMode {
        NONE, SINGLE, ALL
    }
}