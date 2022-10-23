package di.uniba.map.b.adventure.sound;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import di.uniba.map.b.adventure.utility.Wrapper;

public class SoundManager {
    public static final String PICKUP_SOUND_PATH = "./resources/sound/pickUp.wav";
    public static final String DOOR_CLOSE_SOUND_PATH = "./resources/sound/closeDoor.wav";
    public static final String DOOR_OPEN_SOUND_PATH = "./resources/sound/openDoor.wav";
    public static final String DOOR_UNLOCK_OPEN_SOUND_PATH =
            "./resources/sound/unlockingAndOpeningDoor.wav";

    private static String currentMusicPath;

    private static final Map<String, Wrapper<Clip>> defaultSounds;

    private static Wrapper<Clip> musicClip;

    public enum Mode {
        MUSIC,
        SOUND
    }

    static {
        musicClip = new Wrapper<Clip>(null);

        defaultSounds = new HashMap<>();
        defaultSounds.put(PICKUP_SOUND_PATH, new Wrapper<Clip>(null));
        defaultSounds.put(DOOR_CLOSE_SOUND_PATH, new Wrapper<Clip>(null));
        defaultSounds.put(DOOR_OPEN_SOUND_PATH, new Wrapper<Clip>(null));
        defaultSounds.put(DOOR_UNLOCK_OPEN_SOUND_PATH, new Wrapper<Clip>(null));

        for (String key : defaultSounds.keySet()) {
            openWav(key, defaultSounds.get(key));
        }
    }

    public static void playWav(String path, Mode mode) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(mode);

        switch (mode) {
            case MUSIC:
                playMusic(path);
                break;
            case SOUND:
                playSound(path);
                break;
            default:
                break;
        }
    }

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

    private static void playSound(String wavPath) {
        if (defaultSounds.containsKey(wavPath)) {
            Clip defaultSoundClip = defaultSounds.get(wavPath).getObj();

            if (defaultSoundClip.isRunning())
                defaultSoundClip.stop();

            defaultSoundClip.flush();
            defaultSoundClip.setFramePosition(0);

            defaultSoundClip.start();
        } else {
            throw new RuntimeException("Sound unavailable.");
        }
    }

    private static void openWav(String path, Wrapper<Clip> target) {
        try {
            File file = new File(path);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            DataLine.Info info = new DataLine.Info(Clip.class, audioStream.getFormat());
            target.setObj((Clip) AudioSystem.getLine(info));
            target.getObj().open(audioStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException("An error has occurred on opening of " + path);
        }

    }
}
