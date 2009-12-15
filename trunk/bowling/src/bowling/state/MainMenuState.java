package bowling.state;

import java.util.LinkedList;
import java.util.List;

import bowling.input.MenuItemListener;
import bowling.menu.Menu;
import bowling.menu.MenuItem;

import com.jme.input.KeyInput;
import com.jme.input.action.InputActionEvent;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;

/**
 * The main menu state
 */
public class MainMenuState {

	private static GameState state;
	
	/**
	 * Main Menu State constructor. Private, should never be instanced.
	 */
	private MainMenuState() {
		
	}
	
	/**
	 * Retrieves the main menu game state.
	 * @return The main menu game state.
	 */
	public static GameState getState() {
		
		if (state == null) {
			state = createState();
		}
		
		return state;
	}
	
	/**
	 * Creates a new instance of the main menu game state.
	 * @return The newly created instance of the main menu game state.
	 */
	private static GameState createState() {
		List<MenuItem> menuItems = new LinkedList<MenuItem>();
		
    	menuItems.add(new MenuItem("start", "Comenzar Juego", new MenuItemListener(KeyInput.KEY_RETURN) {
			
			@Override
			public void performAction(InputActionEvent evt) {
				// Hide the main menu, start the game!
				MainMenuState.getState().setActive(false);
				BowlingGameState.getState().setActive(true);
				
				// Reset the camera
				BowlingGameState.getState().setupCamera();
			}
		}));
    	
    	menuItems.add(new MenuItem("options", "Opciones", new MenuItemListener(KeyInput.KEY_O){

			@Override
			public void performAction(InputActionEvent evt) {
				MainMenuState.getState().setActive(false);
				GameState menu = OptionsMenuState.getState();
				menu.setActive(true);
				GameStateManager.getInstance().attachChild(menu);
			}
    	}));
    	
    	menuItems.add(new MenuItem("exit", "Salir", new MenuItemListener(KeyInput.KEY_ESCAPE) {
			
			@Override
			public void performAction(InputActionEvent evt) {
				MainMenuState.getState().setActive(false);
				GameState menu = ExitConfirmationMenuState.getState();
				menu.setActive(true);
				GameStateManager.getInstance().attachChild(menu);
			}
		}));
		
		return new Menu("main menu", menuItems);
	}
}
