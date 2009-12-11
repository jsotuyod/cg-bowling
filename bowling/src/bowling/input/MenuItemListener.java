package bowling.input;

import com.jme.input.action.InputAction;

/**
 * Handler for menu item selections.
 */
public abstract class MenuItemListener extends InputAction {

	protected Integer bindedKey;
	
	/**
	 * Creates a new MenuItemListener.
	 */
	public MenuItemListener() {
		this(null);
	}
	
	/**
	 * Creates a new MenuItemListener.
	 * @param bindedKey The key to which to bind the listener.
	 */
	public MenuItemListener(Integer bindedKey) {
		this.bindedKey = bindedKey;
	}

	/**
	 * Retrieves the binded key for this menu.
	 * @return The binded key for this menu.
	 */
	public Integer getBindedKey() {
		return bindedKey;
	}
}
