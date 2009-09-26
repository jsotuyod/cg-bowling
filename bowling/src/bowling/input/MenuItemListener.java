package bowling.input;

import com.jme.input.action.InputAction;

/**
 * Handler for menu item selections.
 * @author jsotuyod
 */
public abstract class MenuItemListener extends InputAction {

	protected Integer bindedKey = null;

	/**
	 * Retrieves the binded key for this menu.
	 * @return The binded key for this menu.
	 */
	public Integer getBindedKey() {
		return bindedKey;
	}
}
