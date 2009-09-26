package bowling.input;

import com.jme.input.KeyInput;
import com.jme.input.action.InputActionEvent;

public class CancelMenuItemListener extends MenuItemListener {

	public CancelMenuItemListener() {
		this.bindedKey = KeyInput.KEY_ESCAPE;
	}
	
	@Override
	public void performAction(InputActionEvent evt) {

		System.out.println("CANCELAMOS!");
	}

}
