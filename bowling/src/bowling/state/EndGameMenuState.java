package bowling.state;

import java.util.LinkedList;
import java.util.List;

import bowling.input.GameInputHandler;
import bowling.input.MenuItemListener;
import bowling.menu.Menu;
import bowling.menu.MenuItem;

import com.jme.input.KeyInput;
import com.jme.input.action.InputActionEvent;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Text;
import com.jmex.game.state.GameState;

/**
 * The end game menu state
 */
public class EndGameMenuState {

	private static GameState state;
	
	private static GameInputHandler inputHandler;

	/**
	 * End game Menu State constructor. Private, should never be instanced.
	 */
	private EndGameMenuState() {
		
	}
	
	/**
	 * Retrieves the end game menu game state.
	 * @return The main menu game state.
	 */
	public static GameState getState() {
		
		if (state == null) {
			state = createState();
		}
		
		return state;
	}
	
	public static void setInputHandler(GameInputHandler handler){
		inputHandler = handler;
	}
	
	/**
	 * Creates a new instance of the end game menu game state.
	 * @return The newly created instance of the end game menu game state.
	 */
	private static GameState createState() {
		return new Menu("end game menu", getMenuItemList(null));
	}
	
	/**
	 * Retrieves hte lsit of menu items according to the winner.
	 * @param winner The name of the winner, or nulll if a tie.
	 * @return The lsit of menu items to be used in the menu.
	 */
	private static List<MenuItem> getMenuItemList(String winner) {
		List<MenuItem> menuItems = new LinkedList<MenuItem>();
		
		menuItems.add(new MenuItem("winner", winner != null ? "El ganador es el " + winner : "Es un empate", null));
		
    	menuItems.add(new MenuItem("start_end", "Comenzar nuevamente", new MenuItemListener(KeyInput.KEY_RETURN) {
			
			@Override
			public void performAction(InputActionEvent evt) {
				// Hide the main menu, start the game!
				EndGameMenuState.getState().setActive(false);
				BowlingGameState game = BowlingGameState.getState();
				game.setInputHandler(inputHandler.getHandler());
				game.setActive(true);
				
				// Reset the camera
				BowlingGameState.getState().setupCamera();
			}
		}));
    	
    	menuItems.add(new MenuItem("exit_end", "Salir", new MenuItemListener(KeyInput.KEY_ESCAPE) {
			
			@Override
			public void performAction(InputActionEvent evt) {
				System.exit(0);
			}
		}));
    	
    	return menuItems;
	}

	/**
	 * Sets the menu item lists displaying the winner
	 * @param winner The name of the winner, null in case of a tie.
	 */
	public static void setWinner(String winner) {
		Menu menu = (Menu) state;
		
		menu.setMenuItems(getMenuItemList(winner));
		
		Text text = (Text) menu.getRootNode().getChild("winner");
		text.setTextColor(new ColorRGBA(1, 0, 0, 1));
		menu.getRootNode().updateRenderState();
	}

}
