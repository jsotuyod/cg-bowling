package bowling.input;

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;
import com.jme.math.Vector3f;
import com.jme.scene.Text;
import com.jmex.physics.DynamicPhysicsNode;

public class GameInputHandler {

	private InputHandler input;
	
	private int forceMagnitude;
	
	private InputActionInterface applyForceAction;
	private InputActionInterface setForceAction;
	
	public GameInputHandler(InputHandler input) {
		this.input = input;
	}
	
	/**
	 * Sets all action listeners.
	 * @param target The node to be targeted by the actions.
	 */
	public void setUp(DynamicPhysicsNode target) {
		
		this.clear();
		
		this.applyForceAction = new ApplyForceAction(target);
		this.setForceAction = new SetForceAction();
		
		input.addAction(this.applyForceAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, InputHandler.AXIS_NONE, false );
        input.addAction(this.setForceAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_F, InputHandler.AXIS_NONE, true );
	}
	
	/**
	 * Removes all previously added action listeners.
	 */
	public void clear() {
		if (this.applyForceAction != null) {
			input.removeAction(this.applyForceAction);
		}
		
		if (this.setForceAction != null) {
			input.removeAction(this.setForceAction);
		}
	}
	
	public Text getInstructions() {
		Text label = Text.createDefaultTextLabel( "instructions", "[f] to increase force. [space bar] to throw the ball." );
        label.setLocalTranslation( 0, 20, 0 );
        
        return label;
	}
	
	private class ApplyForceAction extends InputAction {

		private DynamicPhysicsNode target;
		
        public ApplyForceAction(DynamicPhysicsNode target) {
        	this.target = target;
        }

        public void performAction( InputActionEvent evt ) {
            if ( evt.getTriggerPressed() ) {
                // key goes down - apply motion
            	this.target.addForce( new Vector3f(0, 0, forceMagnitude) );
            }
        }
    }
	
	//TODO: sacar esta clase afuera, meterla en un package aparte	
	private class SetForceAction extends InputAction {
		private static final int FORCE_STEP = 50;
		private static final int FORCE_MIN = 0;
		private static final int FORCE_MAX = 20000;

		public SetForceAction( ) {
			forceMagnitude = FORCE_MIN;
        }

        public void performAction( InputActionEvent evt ) {
            if ( evt.getTriggerAllowsRepeats() && forceMagnitude < FORCE_MAX) {
            	forceMagnitude += FORCE_STEP;
            }
        }
	}
}
