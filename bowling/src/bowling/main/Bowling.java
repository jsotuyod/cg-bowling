package bowling.main;

import bowling.state.BowlingGameState;
import bowling.state.MainMenuState;

import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.physics.util.SimplePhysicsGame;

public class Bowling extends SimplePhysicsGame {

	/**
	 * Init routine for the game.
	 */
	protected void simpleInitGame() {
		// Creates the GameStateManager. Only needs to be called once.
		GameStateManager.create();
		
		this.setUpMainMenu();
		// TODO : add other game states to the manager
		this.setUpGame();
		
		// TODO : DEBUG ONLY!
		this.showPhysics = true;
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
     * The main method to allow starting this class as application.
     * @param args command line arguments
     */
    public static void main( String[] args ) {
    	new Bowling().start();
    }
}
