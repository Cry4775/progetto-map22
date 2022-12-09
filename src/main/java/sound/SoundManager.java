package sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import utility.Wrapper;

/** Allows control of sounds. */
public class SoundManager {
    public static final String PICKUP_SOUND_PATH = "/resources/sound/pickUp.wav";
    public static final String DOOR_CLOSE_SOUND_PATH = "/resources/sound/closeDoor.wav";
    public static final String DOOR_OPEN_SOUND_PATH = "/resources/sound/openDoor.wav";
    public static final String DOOR_UNLOCK_OPEN_SOUND_PATH = "/resources/sound/unlockingAndOpeningDoor.wav";
    public static final String PULL_SOUND_PATH = "/resources/sound/leverPull.wav";
    public static final String INVENTORY_SOUND_PATH = "/resources/sound/inventory.wav";
    private static String currentMusicPath;

    private static final Map<String, Wrapper<Clip>> defaultSounds;
    private static Wrapper<Clip> musicClip;

    static {
        musicClip = new Wrapper<Clip>(null);

        defaultSounds = new HashMap<>();
        Map<String, InputStream> temp = new HashMap<>();

        temp.put(PICKUP_SOUND_PATH, SoundManager.class.getResourceAsStream(PICKUP_SOUND_PATH));
        temp.put(DOOR_CLOSE_SOUND_PATH, SoundManager.class.getResourceAsStream(DOOR_CLOSE_SOUND_PATH));
        temp.put(DOOR_OPEN_SOUND_PATH, SoundManager.class.getResourceAsStream(DOOR_OPEN_SOUND_PATH));
        temp.put(DOOR_UNLOCK_OPEN_SOUND_PATH, SoundManager.class.getResourceAsStream(DOOR_UNLOCK_OPEN_SOUND_PATH));
        temp.put(PULL_SOUND_PATH, SoundManager.class.getResourceAsStream(PULL_SOUND_PATH));
        temp.put(INVENTORY_SOUND_PATH, SoundManager.class.getResourceAsStream(INVENTORY_SOUND_PATH));

        defaultSounds.put(PICKUP_SOUND_PATH, new Wrapper<Clip>(null));
        defaultSounds.put(DOOR_CLOSE_SOUND_PATH, new Wrapper<Clip>(null));
        defaultSounds.put(DOOR_OPEN_SOUND_PATH, new Wrapper<Clip>(null));
        defaultSounds.put(DOOR_UNLOCK_OPEN_SOUND_PATH, new Wrapper<Clip>(null));
        defaultSounds.put(PULL_SOUND_PATH, new Wrapper<Clip>(null));
        defaultSounds.put(INVENTORY_SOUND_PATH, new Wrapper<Clip>(null));

        for (String key : defaultSounds.keySet()) {
            openWav(temp.get(key), defaultSounds.get(key));
        }
    }

    public enum Channel {
        MUSIC,
        EFFECTS
    }

    /**
     * Plays a {@code wav} file.
     * 
     * @param path the path of the wav file.
     * @param channel the channel the wav file should be played on.
     */
    public static void playWav(String path, Channel channel) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(channel);

        switch (channel) {
            case MUSIC:
                playMusic(path);
                break;
            case EFFECTS:
                playSound(path);
                break;
            default:
                break;
        }
    }

    /**
     * Plays the wav file on {@code MUSIC} channel.
     * 
     * @param path the path of the wav file.
     */
    private static void playMusic(String path) {
        if (currentMusicPath != null && currentMusicPath.equals(path))
            return;

        currentMusicPath = path;

        if (musicClip.getObj() != null) {
            if (musicClip.getObj().isRunning())
                musicClip.getObj().stop();

            if (musicClip.getObj().isOpen())
                musicClip.getObj().close();
        }

        openWav(path, musicClip);
        musicClip.getObj().loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Plays the wav file on {@code EFFECTS} channel.
     * 
     * @param wavPath the path of the wav file.
     */
    private static void playSound(String wavPath) {
        if (defaultSounds.containsKey(wavPath)) {
            Clip defaultSoundClip = defaultSounds.get(wavPath).getObj();

            if (defaultSoundClip.isActive() || defaultSoundClip.isRunning())
                defaultSoundClip.stop();

            defaultSoundClip.flush();
            defaultSoundClip.addLineListener(event -> {
                if (event.getType().equals(LineEvent.Type.STOP)) {
                    defaultSoundClip.setFramePosition(0);
                }
            });

            defaultSoundClip.start();
        } else {
            throw new Error("Sound unavailable.");
        }
    }

    /**
     * Opens the {@code wav} file on the specified {@link javax.sound.sampled.Clip Clip}.
     * 
     * @param path the path of the wav file.
     * @param target the clip where the wav file gets opened.
     */
    private static void openWav(String path, Wrapper<Clip> target) {
        try {
            File file = new File(path);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            DataLine.Info info = new DataLine.Info(Clip.class, audioStream.getFormat());
            target.setObj((Clip) AudioSystem.getLine(info));
            target.getObj().open(audioStream);
        } catch (Exception e) {
            throw new Error("An error has occurred on opening of " + path);
        }
    }

    /**
     * /**
     * Opens the {@code wav} file on the specified {@link javax.sound.sampled.Clip Clip}.
     * 
     * @param inputStream the input stream of the wav file.
     * @param target the clip where the wav file gets opened.
     */
    private static void openWav(InputStream inputStream, Wrapper<Clip> target) {
        try {
            InputStream bufferedIn = new BufferedInputStream(inputStream);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            DataLine.Info info = new DataLine.Info(Clip.class, audioStream.getFormat());
            target.setObj((Clip) AudioSystem.getLine(info));
            target.getObj().open(audioStream);
        } catch (Exception e) {
            throw new Error("An error has occurred upon opening of default sound.");
        }
    }
}
