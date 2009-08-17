package bowling.input;

import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jme.scene.Text;
import com.jmex.physics.DynamicPhysicsNode;

public class InputHandler {

	private com.jme.input.InputHandler input;
	
	private int forceMagnitude;
	
	public InputHandler(com.jme.input.InputHandler input) {
		this.input = input;
	}
	
	public void setUp(DynamicPhysicsNode target) {
		
		input.addAction( new ApplyForceAction(target), com.jme.input.InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, com.jme.input.InputHandler.AXIS_NONE, false );
        input.addAction( new SetForceAction(), com.jme.input.InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_F, com.jme.input.InputHandler.AXIS_NONE, true );
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
