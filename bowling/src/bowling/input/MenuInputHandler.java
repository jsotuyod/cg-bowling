package bowling.input;

import com.jme.input.InputHandler;
import com.jmex.game.state.GameState;

public class MenuInputHandler extends InputHandler {

	protected GameState curState;

    public MenuInputHandler(GameState curState) {
        setKeyBindings();
        this.curState = curState;
    }

	protected void setKeyBindings() {
		
	}
}
