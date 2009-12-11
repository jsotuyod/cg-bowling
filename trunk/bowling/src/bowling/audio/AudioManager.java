package bowling.audio;

import java.net.MalformedURLException;
import java.net.URL;

import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.AudioTrack.TrackType;
import com.jmex.audio.MusicTrackQueue.RepeatType;

public class AudioManager {

	private final static String[] audioTracks = { "file:resources/audio/mantra.ogg"};

	private boolean enableBackgroundMusic = true;
	private boolean enableBowlingSound = true;

	private AudioSystem audioSystem;

	private AudioTrack ballShotSound;
	private AudioTrack gutterSound;

	public AudioManager() {
		this.audioSystem = AudioSystem.getSystem();

		setBowlingSounds();

		setBackGroundMusic();
		
		setKeyBindings();
	}

	private void setBowlingSounds() {
		// Set bowling sounds
		try {
			ballShotSound = AudioSystem.getSystem().createAudioTrack(new URL("file:resources/audio/ballshot.ogg"), true);
			gutterSound = AudioSystem.getSystem().createAudioTrack(new URL("file:resources/audio/gutter.ogg"), true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void setBackGroundMusic() {
		// Enqueue al background music
		for (String audioTrack : audioTracks) {
			try {
				audioSystem.getMusicQueue().addTrack(getMusicFromURL(new URL(audioTrack)));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	private void setKeyBindings() {
		KeyBindingManager.getKeyBindingManager().set("PREVIOUS_TRACK", KeyInput.KEY_J);
		KeyBindingManager.getKeyBindingManager().set("NEXT_TRACK", KeyInput.KEY_K);
		KeyBindingManager.getKeyBindingManager().set("MUTE", KeyInput.KEY_M);
	}

	private AudioTrack getMusicFromURL(URL musicURL) {
		AudioTrack audioTrack = AudioSystem.getSystem().createAudioTrack(musicURL,
				true);
		audioTrack.setType(TrackType.MUSIC);
		audioTrack.setRelative(true);
		audioTrack.setTargetVolume(1f);
		audioTrack.setLooping(false);
		return audioTrack;
	}

	public void startMusic() {
		 audioSystem.getMusicQueue().setRepeatType(RepeatType.ALL);
		 audioSystem.getMusicQueue().setCrossfadeinTime(1.5f);
		 audioSystem.getMusicQueue().setCrossfadeoutTime(1.5f);

		// start playing!
		if (enableBackgroundMusic) {
			//TODO ver porque no funciona la cola!!
//			audioSystem.getMusicQueue().play();
			try {
				AudioSystem.getSystem().createAudioTrack(new URL("file:resources/audio/mantra.ogg"), true).play();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
		}
	}

	public void toggleBowlingSound() {
		enableBowlingSound = !enableBowlingSound;
	}

	public void toggleBackgroundMusic() {
		if (!enableBackgroundMusic) {
			audioSystem.getMusicQueue().play();
			enableBackgroundMusic = true;
		} else {
			audioSystem.getMusicQueue().stop();
			enableBackgroundMusic = false;
		}
	}
	
	public void playBallShotSound() {
		if (enableBowlingSound) {
			ballShotSound.play();
		}
	}
	
	public void playGutterSound() {
		if (enableBowlingSound) {
			gutterSound.play();
		}
	}

	public void updateMusicState() {
		if (enableBackgroundMusic) {
			if (KeyBindingManager.getKeyBindingManager().isValidCommand("PREVIOUS_TRACK", false)) {
				audioSystem.getMusicQueue().prevTrack();
			}
			
			if (KeyBindingManager.getKeyBindingManager().isValidCommand("NEXT_TRACK", false)) {
				audioSystem.getMusicQueue().nextTrack();
			}
			if (KeyBindingManager.getKeyBindingManager().isValidCommand("MUTE", false)) {
				if (audioSystem.isMuted()) {
					audioSystem.unmute();
				} else {
					audioSystem.mute();
				}
			}
			audioSystem.update();
		}
	}
	
	public boolean isEnableBackgroundMusic() {
		return enableBackgroundMusic;
	}
	
	public boolean isEnableBowlingSound() {
		return enableBowlingSound;
	}
	
	public static void main(String[] args) {
		AudioManager audioManager = new AudioManager();
		
		System.out.println("Playing ball shot sound....");
		audioManager.playBallShotSound();
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Playing gutter sound...");
		audioManager.playGutterSound();
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Playing background music....");
		audioManager.startMusic();
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
