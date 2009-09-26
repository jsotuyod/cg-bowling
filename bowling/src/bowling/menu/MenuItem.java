package bowling.menu;

import bowling.input.MenuItemListener;

public class MenuItem {

	protected String name;
	protected String displayText;
	protected MenuItemListener listener;
	
	/**
	 * Creates a new menu item.
	 * @param name The name of the menu item.
	 * @param displayText The text being displayed with the item.
	 * @param listener The listener for the item.
	 */
	public MenuItem(String name, String displayText, MenuItemListener listener) {
		super();
		this.name = name;
		this.displayText = displayText;
		this.listener = listener;
	}
	
	public String getName() {
		return name;
	}
	public String getDisplayText() {
		return displayText;
	}
	public MenuItemListener getListener() {
		return listener;
	}

}
