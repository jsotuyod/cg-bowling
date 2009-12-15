package bowling.main;

import bowling.audio.AudioManager;
import bowling.state.BowlingGameState;
import bowling.state.EndGameMenuState;
import bowling.state.ExitConfirmationMenuState;
import bowling.state.MainMenuState;
import bowling.state.OptionsMenuState;

import com.jme.input.KeyBindingManager;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.physics.util.SimplePhysicsGame;

public class Bowling extends SimplePhysicsGame {

	private static AudioManager audioManager = new AudioManager();
	
	/**
	 * Init routine for the game.
	 */
	@Override
	protected void simpleInitGame() {
		// Creates the GameStateManager. Only needs to be called once.
		GameStateManager.create();
		
		this.setUpMainMenu();
		this.setUpExitMenu();
		this.setUpEndGameMenu();
		this.setUpOptionsMenu();
		this.setUpGame();
		
		this.setUpMusic();
		
		this.removeInheritedBindings();
    }
	
	/**
	 * Adds the end game menu state.
	 */
	private void setUpEndGameMenu() {
		GameState menu = EndGameMenuState.getState();
		GameStateManager.getInstance().attachChild(menu);
	}

	/**
	 * Adds the options menu state.
	 */
	private void setUpOptionsMenu() {
		GameState menu = OptionsMenuState.getState();
		GameStateManager.getInstance().attachChild(menu);
	}

	/**
	 * Adds the exit menu game state.
	 */
	private void setUpExitMenu() {
		GameState menu = ExitConfirmationMenuState.getState();
		GameStateManager.getInstance().attachChild(menu);
	}

	/*
	 * (non-Javadoc)
	 * @see com.jmex.physics.util.SimplePhysicsGame#preRender()
	 */
	@Override
	protected void preRender() {
		GameStateManager.getInstance().update(tpf);
		GameStateManager.getInstance().render(tpf);
    }
	
	/**
	 * Adds the main menu game state.
	 */
    private void setUpMainMenu() {
		GameState menu = MainMenuState.getState();
		menu.setActive(true);
		GameStateManager.getInstance().attachChild(menu);
	}
    
    /**
     * Adds the bowling game state.
     */
    private void setUpGame() {
    	BowlingGameState game = BowlingGameState.getState();
    	game.setInputHandler(this.input);
    	GameStateManager.getInstance().attachChild(game);
    }

    /**
     * Adds the music to the game.
     */
    private void setUpMusic(){
    	audioManager.startMusic();
    }
    
    @Override
    protected void simpleUpdate() {
    	audioManager.updateMusicState();
    }

    /**
     * Game audio manager singleton instance
     * @return {@link AudioManager} instance
     */
    public static AudioManager getGameAudioManager(){
    	return audioManager;
    }
    
	private void removeInheritedBindings() {
		KeyBindingManager.getKeyBindingManager().remove("exit");
		KeyBindingManager.getKeyBindingManager().remove("camera_out");
		KeyBindingManager.getKeyBindingManager().remove("toggle_depth");
		
		// Make camera still
		input.removeFromAttachedHandlers(cameraInputHandler);
	}
    
    /**
     * The main method to allow starting this class as application.
     * @param args command line arguments
     */
    public static void main( String[] args ) {
    	new Bowling().start();
    }
}
