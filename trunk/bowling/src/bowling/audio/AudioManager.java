package bowling.audio;

import java.net.MalformedURLException;
import java.net.URL;

import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.AudioTrack.TrackType;
import com.jmex.audio.MusicTrackQueue.RepeatType;

/**
 * This class manages all the application sounds and music
 * 
 */
public class AudioManager{

	// All the songs that will be included in the queue
	private final static String[] audioTracks = { "file:resources/audio/mantra.ogg" };

	private boolean enableBackgroundMusic = true;
	private boolean enableBowlingSound = true;

	private AudioSystem audioSystem;

	private AudioTrack ballShotSound;
	
	private AudioTrack pinsSound;
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
			ballShotSound = AudioSystem.getSystem().createAudioTrack(new URL("file:resources/audio/ball.ogg"), true);
			ballShotSound.setLooping(true);
			pinsSound = AudioSystem.getSystem().createAudioTrack(new URL("file:resources/audio/pins.ogg"), true);
			gutterSound = AudioSystem.getSystem().createAudioTrack(new URL("file:resources/audio/gutter.ogg"), true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void setBackGroundMusic() {
		// Enqueue al background music
		audioSystem.getMusicQueue().setRepeatType(RepeatType.ALL);
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
		KeyBindingManager.getKeyBindingManager().set("NEXT_TRACK", KeyInput.KEY_U);
		KeyBindingManager.getKeyBindingManager().set("MUTE", KeyInput.KEY_K);
	}

	private AudioTrack getMusicFromURL(URL musicURL) {
		AudioTrack audioTrack = AudioSystem.getSystem().createAudioTrack(musicURL,
				true);
		audioTrack.setType(TrackType.MUSIC);
		audioTrack.setRelative(true);
		audioTrack.setTargetVolume(.5f);
		audioTrack.setLooping(false);
		return audioTrack;
	}

	/**
	 * Start playing music
	 */
	public void startMusic() {
		 audioSystem.getMusicQueue().setCrossfadeinTime(0.5f);
		 audioSystem.getMusicQueue().setCrossfadeoutTime(0.5f);

		// start playing!
		if (enableBackgroundMusic) {
			audioSystem.getMusicQueue().play();
		}
	}

	/**
	 * Enable/disable sounds
	 */
	public void toggleBowlingSound() {
		enableBowlingSound = !enableBowlingSound;
	}

	/**
	 * Enable/disable music
	 */
	public void toggleBackgroundMusic() {
		if (!enableBackgroundMusic) {
			audioSystem.getMusicQueue().play();
			enableBackgroundMusic = true;
		} else {
			audioSystem.getMusicQueue().stop();
			enableBackgroundMusic = false;
		}
	}
	
	/**
	 * Make the ball sound
	 */
	public void playBallShotSound() {
		if (enableBowlingSound) {
			if(!ballShotSound.isPlaying()){
				ballShotSound.play();
			}else {
				ballShotSound.setVolume(1.0f);
			}
		}
	}
	
	/**
	 * Make the pins sound
	 */
	public void playPinsSound(int pinsUp) {
		if (enableBowlingSound) {
			if(!pinsSound.isPlaying()){
				pinsSound.play();
			}else{
				pinsSound.setVolume(pinsUp/10.0f);
			}
		}
	}
	
	/**
	 * Make th gutter sound
	 */
	public void playGutterSound() {
		if (enableBowlingSound) {
			if(!gutterSound.isPlaying()){
				gutterSound.play();
			}
		}
	}
	
	/**
	 * Stop all sound effects
	 */
	public void stopAllSounds(){
		if(enableBowlingSound){
			if(ballShotSound.isPlaying()){
				ballShotSound.mute();
			}
			if(pinsSound.isPlaying()){
				pinsSound.mute();
			}
			gutterSound.stop();
		}
	}

	/**
	 * This method should be called to update the audio system and make everithing work
	 */
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
}
