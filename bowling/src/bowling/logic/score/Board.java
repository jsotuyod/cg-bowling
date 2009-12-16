package bowling.logic.score;

import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.system.DisplaySystem;

public class Board {

	private Node scoreNode;
	private Score player1Score;

	public Board(String userName) {
		
		this.scoreNode = new Node("score node");
		
		this.reset(userName);
		
		this.scoreNode.setLightCombineMode(Spatial.LightCombineMode.Off);
		this.scoreNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		this.scoreNode.updateRenderState();
		this.scoreNode.updateGeometricState(0, true);
	}
	
	public void refreshText() {
		
		this.scoreNode.detachAllChildren();
		
		player1Score.calcNameLine();
		Text nameLine = player1Score.getNameLine();
		
		this.scoreNode.attachChild(nameLine);
		
		player1Score.calcFirstLine();
		Text[] firstLine = player1Score.getFirstLine();
		for (Text line : firstLine) {
			this.scoreNode.attachChild(line);	
		}
		
		player1Score.calcSecondLine();
		Text[] secondLine = player1Score.getSecondLine();
		for (Text line : secondLine) {
			this.scoreNode.attachChild(line);	
		}
		
		player1Score.calcThirdLine();
		Text[] thirdLine = player1Score.getThirdLine();
		for (Text line : thirdLine) {
			this.scoreNode.attachChild(line);
		}
		
		this.scoreNode.updateRenderState();
	}
	
	public void update(float tpf) {
		this.scoreNode.updateGeometricState(tpf, true);
	}
	
	public PinsAction score(int pinsDown) {
		PinsAction p = player1Score.score(pinsDown);
		refreshText();
		return p;
	}

	/**
	 * Resets the score board.
	 * @param userName The name of the user whose score is being kept track of.
	 */
	public void reset(String userName) {
		this.player1Score = new Score(userName);
		
		refreshText();
	}

	public void render(float tpf) {
		DisplaySystem.getDisplaySystem().getRenderer().draw(this.scoreNode);
	}
}
