package bowling.input;

import com.jme.input.KeyInput;
import com.jme.input.action.InputActionEvent;

/**
 * A cancel menu item listener.
 */
public class CancelMenuItemListener extends MenuItemListener {

	/**
	 * Creates a new CancelMenuItemListener instance.
	 */
	public CancelMenuItemListener() {
		super(KeyInput.KEY_ESCAPE);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jme.input.action.InputActionInterface#performAction(com.jme.input.action.InputActionEvent)
	 */
	@Override
	public void performAction(InputActionEvent evt) {

		System.out.println("CANCELAMOS!");
	}
}
