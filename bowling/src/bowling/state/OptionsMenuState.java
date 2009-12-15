package bowling.state;

import java.util.LinkedList;
import java.util.List;

import bowling.audio.AudioManager;
import bowling.input.MenuItemListener;
import bowling.main.Bowling;
import bowling.menu.Menu;
import bowling.menu.MenuItem;

import com.jme.input.KeyInput;
import com.jme.input.action.InputActionEvent;
import com.jmex.game.state.GameState;

/**
 * The options menu state
 */
public class OptionsMenuState {

	private static GameState state;
	
	/**
	 * Options Menu State constructor. Private, should never be instanced.
	 */
	private OptionsMenuState() {
		
	}
	
	/**
	 * Retrieves the options menu game state.
	 * @return The options menu game state.
	 */
	public static GameState getState() {
		
		if (state == null) {
			state = createState();
		}
		
		return state;
	}
	
	/**
	 * Creates a new instance of the options menu game state.
	 * @return The newly created instance of the options menu game state.
	 */
	private static GameState createState() {
		final AudioManager gameAudioManager = Bowling.getGameAudioManager();
		
		List<MenuItem> menuItems = new LinkedList<MenuItem>();
		
    	menuItems.add(new MenuItem("music", "[M] Musica On/Off" , new MenuItemListener(KeyInput.KEY_M) {
			
			@Override
			public void performAction(InputActionEvent evt){
				if(evt.getTriggerPressed()){
					gameAudioManager.toggleBackgroundMusic();
				}
			}
		}));
    	
    	menuItems.add(new MenuItem("sound", "[S] Sonido de efectos On/Off", new MenuItemListener(KeyInput.KEY_S) {
			
			@Override
			public void performAction(InputActionEvent evt) {
				if(evt.getTriggerPressed()){
					gameAudioManager.toggleBowlingSound();
				}
			}
		}));
    	
    	menuItems.add(new MenuItem("back", "[V] Volver al menu principal", new MenuItemListener(KeyInput.KEY_V) {
			
			@Override
			public void performAction(InputActionEvent evt) {
				if(evt.getTriggerPressed()){
					// Hide the options menu, go back to the main menu
					OptionsMenuState.getState().setActive(false);
					MainMenuState.getState().setActive(true);
				}
			}
		}));
		
		return new Menu("options menu", menuItems);
	}
}
