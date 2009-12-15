package bowling.state;

import java.util.LinkedList;
import java.util.List;

import bowling.input.MenuItemListener;
import bowling.menu.Menu;
import bowling.menu.MenuItem;

import com.jme.input.KeyInput;
import com.jme.input.action.InputActionEvent;
import com.jmex.game.state.GameState;

/**
 * The exit confirmation menu state
 */
public class ExitConfirmationMenuState {

	private static GameState state;
	
	private static boolean isPlaying = false;
	
	/**
	 * Exit confirmation Menu State constructor. Private, should never be instanced.
	 */
	private ExitConfirmationMenuState() {
		
	}
	
	/**
	 * Retrieves the exit confirmation menu game state.
	 * @return The options menu game state.
	 */
	public static GameState getState() {
		
		if (state == null) {
			state = createState();
		}
		
		return state;
	}
	
	public static void setPlaying(boolean playing){
		isPlaying = playing;
	}
	
	/**
	 * Creates a new instance of the options menu game state.
	 * @return The newly created instance of the options menu game state.
	 */
	private static GameState createState() {
		
		List<MenuItem> menuItems = new LinkedList<MenuItem>();
		
    	menuItems.add(new MenuItem("message", "Esta seguro que desea salir?" , new MenuItemListener(KeyInput.KEY_S) {
			
			@Override
			public void performAction(InputActionEvent evt){
			}
		}));
    	
    	menuItems.add(new MenuItem("yes", "Si", new MenuItemListener(KeyInput.KEY_Y) {
			
			@Override
			public void performAction(InputActionEvent evt) {
				System.exit(0);
			}
		}));
    	
    	menuItems.add(new MenuItem("back", "No", new MenuItemListener(KeyInput.KEY_N) {
			
			@Override
			public void performAction(InputActionEvent evt) {
				ExitConfirmationMenuState.getState().setActive(false);
				if(isPlaying){
					BowlingGameState.getState().setActive(true);
				}else{
					GameState menu = MainMenuState.getState();
					menu.setActive(true);
				}
			}
		}));
		
		return new Menu("exit confirmation menu", menuItems);
	}
}
