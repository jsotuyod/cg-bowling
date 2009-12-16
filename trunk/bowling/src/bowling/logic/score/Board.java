package bowling.logic.score;

import com.jme.scene.Text;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;

public class Board {

	private Score player1Score;
	private static DebugGameState gameState;

	private Text line1;



	public Board() {
		
		this.player1Score = new Score("Player 1");
		
		gameState = new DebugGameState();
		GameStateManager.getInstance().attachChild(gameState);
		
		refreshText();
	}
	
	private void refreshText() {
		
		Text nameLine = player1Score.getNameLine();
		if (nameLine != null) {
			gameState.getRootNode().detachChild(nameLine);	
		}
		player1Score.calcNameLine();
		nameLine = player1Score.getNameLine();
		gameState.getRootNode().attachChild(nameLine);
		
		Text[] firstLine = player1Score.getFirstLine();
		
		if (firstLine != null) {
			for (Text line : firstLine) {
				if (line != null) {
					gameState.getRootNode().detachChild(line);
				}
			}
		}
		player1Score.calcFirstLine();
		firstLine = player1Score.getFirstLine();
		for (Text line : firstLine) {
			gameState.getRootNode().attachChild(line);	
		}
		
		Text[] secondLine = player1Score.getSecondLine();
		if (secondLine != null) {
			for (Text line : secondLine) {
				if (line != null) {
					gameState.getRootNode().detachChild(line);
				}
			}
		}
		player1Score.calcSecondLine();
		secondLine = player1Score.getSecondLine();
		for (Text line : secondLine) {
			gameState.getRootNode().attachChild(line);	
		}
		
		Text[] thirdLine = player1Score.getThirdLine();
		if (thirdLine != null) {
			for (Text line : thirdLine) {
				if (line != null) {
					gameState.getRootNode().detachChild(line);
				}
			}
		}
		player1Score.calcThirdLine();
		thirdLine = player1Score.getThirdLine();
		for (Text line : thirdLine) {
			gameState.getRootNode().attachChild(line);	
		}
		
	}
	public static void setEnabled (boolean flag) {
		gameState.setActive(flag);	
	}
	
	public PinsAction score(int pinsDown) {
		PinsAction p = player1Score.score(pinsDown);
		refreshText();
		return p;
	}
	
}
