package bowling.logic.score;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.system.DisplaySystem;

public class Board {

	private Node scoreNode;
	
	private Node scorePlayer1Node;
	private Node scorePlayer2Node;
	
	private Score player1Score;
	private Score player2Score;
	
	private boolean firstPlayerTurn;

	public Board(String userName1, String userName2) {
		
		this.scoreNode = new Node("score node");
		
		this.scorePlayer1Node = new Node("score player1 node");
		this.scorePlayer2Node = new Node("score player2 node");
		
		this.scoreNode.attachChild(this.scorePlayer1Node);
		this.scoreNode.attachChild(this.scorePlayer2Node);
		
		this.scorePlayer2Node.getLocalTranslation().y = -100;
		
		this.reset(userName1, userName2);
		
		this.scoreNode.setLightCombineMode(Spatial.LightCombineMode.Off);
		this.scoreNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		this.scoreNode.updateRenderState();
		this.scoreNode.updateGeometricState(0, true);
	}
	
	public void refreshText() {
		
		this.refreshScore(player1Score, scorePlayer1Node, firstPlayerTurn);
		this.refreshScore(player2Score, scorePlayer2Node, !firstPlayerTurn);
	}
	
	private void refreshScore(Score score, Node node, boolean isActive) {
		node.detachAllChildren();
		
		score.calcNameLine();
		Text nameLine = score.getNameLine();
		
		if (isActive) {
			nameLine.setTextColor(new ColorRGBA(1, 0, 0, 1));
		} else {
			nameLine.setTextColor(new ColorRGBA(1, 1, 1, 1));
		}
		
		node.attachChild(nameLine);
		
		score.calcFirstLine();
		Text[] firstLine = score.getFirstLine();
		for (Text line : firstLine) {
			node.attachChild(line);	
		}
		
		score.calcSecondLine();
		Text[] secondLine = score.getSecondLine();
		for (Text line : secondLine) {
			node.attachChild(line);	
		}
		
		score.calcThirdLine();
		Text[] thirdLine = score.getThirdLine();
		for (Text line : thirdLine) {
			node.attachChild(line);
		}
		
		node.updateRenderState();
	}
	
	public void update(float tpf) {
		this.scoreNode.updateGeometricState(tpf, true);
	}
	
	public PinsAction score(int pinsDown) {
		
		Score score;
		
		if (firstPlayerTurn) {
			score = player1Score;
		} else {
			score = player2Score;
		}
		
		PinsAction p = score.score(pinsDown);
		
		if (p == PinsAction.GAME_ENDED && firstPlayerTurn) {
			// Fake it! Turn is over, not game yet
			p = PinsAction.TURN_ENDED;
		}
		
		if (p == PinsAction.TURN_ENDED) {
			firstPlayerTurn = !firstPlayerTurn;
		}
		
		refreshText();
		
		return p;
	}

	/**
	 * Resets the score board.
	 * @param userName The name of the user whose score is being kept track of.
	 */
	public void reset(String userName1, String userName2) {
		this.player1Score = new Score(userName1);
		this.player2Score = new Score(userName2);
		
		firstPlayerTurn = true;
		
		refreshText();
	}

	public void render(float tpf) {
		DisplaySystem.getDisplaySystem().getRenderer().draw(this.scoreNode);
	}

	/**
	 * Retrieves the winner player's name, or null if it's a tie.
	 * @return The winner player if any, null if a tie.
	 */
	public String getWinner() {
		
		if (this.player1Score.getScore() > this.player2Score.getScore()) {
			return this.player1Score.getPlayerName();
		} else if (this.player1Score.getScore() < this.player2Score.getScore()) {
			return this.player2Score.getPlayerName();
		}
		
		// It's a draw
		return null;
	}
}
