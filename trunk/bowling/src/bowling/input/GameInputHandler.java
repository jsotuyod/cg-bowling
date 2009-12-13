package bowling.input;

import bowling.state.BowlingGameState;

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;
import com.jme.scene.Text;

/**
 * The game input handler
 */
public class GameInputHandler {

	private InputHandler input;
	
	private InputActionInterface barStopedAction;
	
	/**
	 * Creates a new game input handler.
	 * @param input The input from which to receive events.
	 */
	public GameInputHandler(InputHandler input) {
		this.input = input;
	}
	
	/**
	 * Sets all action listeners.
	 * @param target The bowling game state to which to notify events.
	 */
	public void setUp(BowlingGameState target) {
		
		this.clear();
		
		this.barStopedAction = new BarStopedAction(target);
		
		input.addAction(this.barStopedAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, InputHandler.AXIS_NONE, false );
	}
	
	/**
	 * Removes all previously added action listeners.
	 */
	public void clear() {
		if (this.barStopedAction != null) {
			input.removeAction(this.barStopedAction);
		}
	}
	
	public Text getInstructions() {
		Text label = Text.createDefaultTextLabel( "instructions", "[space bar] to stop the power, direction and angle meters." );
        label.setLocalTranslation( 0, 20, 0 );
        
        return label;
	}
	
	/**
	 * Handler for stoping the bar.
	 */
	private class BarStopedAction extends InputAction {

		private BowlingGameState target;
		
		/**
		 * Creates a new BarStopedAction.
		 * @param target The bowling game state to receive notifications.
		 */
        public BarStopedAction(BowlingGameState target) {
        	this.target = target;
        }

        /**
         * Handles the event.
         * @param evt The event object.
         */
        public void performAction(InputActionEvent evt) {
            if ( evt.getTriggerPressed() ) {
            	this.target.barStoped();
            }
        }
    }
}
