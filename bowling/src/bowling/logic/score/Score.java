package bowling.logic.score;

import java.util.ArrayList;

import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.system.DisplaySystem;


import bowling.utils.Util;

public class Score {

	private int totalPoints;
	private int currRound;
	private int currShoot;
	
	private String playerName;
	
	private static final int TOTAL_ROUNDS = 10;
	private static final int TOTAL_PINS = 10;
	
	private ArrayList<Point> points;
	private ArrayList<PendingPoint> pendingPoints;
	
	private Node scoreNode;
	private Node totalNode;
	
	private Text nameLine;
	private Text[] line1;
	private Text[] line2;
	private Text[] line3;
	
	public Node getScoreNode() {
		return scoreNode;
	}
	
	
	public void updateTotalNode() {
		if (totalNode != null) {
			this.scoreNode.detachChild(this.totalNode);
		}
		

		
	}
	
	public PinsAction score(int pinsDown) {
		if (currRound == TOTAL_ROUNDS) {
			throw new RuntimeException("Invalid round score!");
		}
		Point point = this.points.get(currRound);
		
		for (PendingPoint pendingPoint : pendingPoints) {
			
			int result = pendingPoint.isPending(pinsDown);
			
			if (result != 0) {
				totalPoints += result;
				pendingPoint.getPoint().updateValue(LabelType.SUBTOTAL, this.totalPoints);
				pendingPoints.remove(pendingPoints);
			}
		}
		
		if (currRound != TOTAL_ROUNDS - 1) {
			if (currShoot == 0) {
				if (pinsDown == TOTAL_PINS) {
					point.updateValue(LabelType.SECOND_SHOOT, Point.STRIKE);
					pendingPoints.add(new PendingPoint(point, 2));
					currRound++;
					return PinsAction.TURN_ENDED;
				} else {
					point.updateValue(LabelType.FIRST_SHOOT, pinsDown);
					currShoot++;
					return PinsAction.DO_NOTHING;
				}
			} else if (currShoot == 1) {
				int roundPoints = pinsDown + point.getFirstShootValue();

				if (roundPoints == TOTAL_PINS) {
					point.updateValue(LabelType.SECOND_SHOOT, Point.SPARE);
					pendingPoints.add(new PendingPoint(point, 1));
				} else {
					point.updateValue(LabelType.SECOND_SHOOT, pinsDown);
					this.totalPoints += roundPoints;
					point.updateValue(LabelType.SUBTOTAL, this.totalPoints);
				}

				currShoot = 0;
				currRound++;

				return PinsAction.TURN_ENDED;
			}  else {
				throw new RuntimeException("Invalid current shot!");
			}
		} else {
			if (currShoot == 0) {
				currShoot++;

				if (pinsDown == TOTAL_PINS) {
					point.updateValue(LabelType.FIRST_SHOOT, Point.STRIKE);
					pendingPoints.add(new PendingPoint(point, 2));
					return PinsAction.RESET_PINS_TURN_NOT_ENDED;
				} else {
					point.updateValue(LabelType.FIRST_SHOOT, pinsDown);
					return PinsAction.DO_NOTHING;
				}
			} else if (currShoot == 1) {
				int firstShoot = point.getFirstShootValue();
				currShoot++;

				if (firstShoot == Point.STRIKE) {
					if (pinsDown == TOTAL_PINS) {
						point.updateValue(LabelType.SECOND_SHOOT, Point.STRIKE);
					} else {
						point.updateValue(LabelType.SECOND_SHOOT, pinsDown);
					}
					
					return PinsAction.RESET_PINS_TURN_NOT_ENDED;
				} else {
					int roundPoints = pinsDown + firstShoot;
					
					if (roundPoints == TOTAL_PINS) {
						point.updateValue(LabelType.SECOND_SHOOT, Point.SPARE);
						pendingPoints.add(new PendingPoint(point, 1));
						return PinsAction.RESET_PINS_TURN_NOT_ENDED;
					} else {
						point.updateValue(LabelType.SECOND_SHOOT, pinsDown);
						this.totalPoints += roundPoints;
						point.updateValue(LabelType.SUBTOTAL, this.totalPoints);
						return PinsAction.GAME_ENDED;
					}
				}
			} else if (currShoot == 2) {
				int secondShoot = point.getSecondShootValue();
				
				if (secondShoot == Point.STRIKE || secondShoot == Point.SPARE) {
					if (pinsDown == TOTAL_PINS) {
						point.updateValue(LabelType.THIRD_SHOOT, Point.STRIKE);
					} else {
						point.updateValue(LabelType.THIRD_SHOOT, pinsDown);
					}
				} else {
					int roundPoints = pinsDown + secondShoot;

					if (roundPoints == TOTAL_PINS) {
						point.updateValue(LabelType.THIRD_SHOOT, Point.SPARE);
					} else {
						point.updateValue(LabelType.THIRD_SHOOT, pinsDown);
					}
				}
				
				return PinsAction.GAME_ENDED;
			} else {
				throw new RuntimeException("Invalid current shot!");
			}
			
		}
	}
	
	public void calcNameLine() {
		nameLine = Util.createText (this.getPlayerName(),0, DisplaySystem.getDisplaySystem().getHeight() - 20);
	}
	
	public void calcFirstLine() {
		int limit = currRound + 1;
		Text[] t = new Text[limit];
		
		for (int i = 0; i < limit; i++) {
			t[i] = Util.createText(new Integer(i+1).toString(), 10 + (i*60), DisplaySystem.getDisplaySystem().getHeight()-40);
			

		}
		line1 = t;
	}
	
	public void calcSecondLine() {
		int limit = currRound + 1;
		Text[] t = new Text[limit];
		
		for (int i = 0; i < limit; i++) {
			t[i] = Util.createText(this.points.get(i).toString(), i*60, DisplaySystem.getDisplaySystem().getHeight()-60);
			

		}
		line2 = t;
	}

	public void calcThirdLine() {
		int limit = currRound + 1;
		Text[] t = new Text[limit];
		
		for (int i = 0; i < limit; i++) {
			int subtotal = this.points.get(i).getSubTotalValue();
			if (subtotal != 0) {
				t[i] = Util.createText(new Integer(this.points.get(i).getSubTotalValue()).toString(),
					5 + (i*60), DisplaySystem.getDisplaySystem().getHeight()-80);
			}
			else {
				t[i] = Util.createText("",
						5 + (i*60), DisplaySystem.getDisplaySystem().getHeight()-80);
			}
			

		}
		line3 = t;
	}
	
	public Text getNameLine() {
		return nameLine;
	}
	
	public Text[] getFirstLine() {
		return line1;
	}
	
	public Text[] getSecondLine() {
		return line2;
	}
	
	public Text[] getThirdLine() {
		return line3;
	}
	
	public Score(String playerName) {
		
		this.playerName = playerName;
		this.points = new ArrayList<Point>();
		this.pendingPoints = new ArrayList<PendingPoint>();
		this.scoreNode = new Node("Score node");
		
		this.totalPoints = 0;
		this.currRound = 0;
		this.currShoot = 0;
		

/*		Font3D font = new Font3D(new Font("Arial", Font.PLAIN, 24), 0.001f, true, true, true);
		Text3D text = font.createText("Testing 1, 2, 3", 50.0f, 0);
		text.setLocalScale(new Vector3f(5.0f, 5.0f, 0.01f));
		debug.getRootNode().attachChild(text);*/
		
		/*Text t = Text.createDefaultTextLabel("Text",
        	"----E: debug show/hide reflection and refraction textures");
		t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		t.setLightCombineMode(Spatial.LightCombineMode.Off);
		t.setLocalTranslation(new Vector3f(100, 100, 5));
		debug.getRootNode().attachChild(t);
		
		t = Text.createDefaultTextLabel("Text",
    	"Otra prueba");
	t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
	t.setLightCombineMode(Spatial.LightCombineMode.Off);
	t.setLocalTranslation(new Vector3f(100, 50, 5));
	debug.getRootNode().attachChild(t);
	debug.getRootNode().detachChild(t);*/
		for (int i = 0; i < TOTAL_ROUNDS; i++) {

			Point point = new Point();

			this.points.add(point);
 
		}
		
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public int getRound() {
		return currRound;
	}

	public static void main(String[] args) {
		
		int[] droppedPins = {8, 2, 4, 3};
		Score score = new Score("Ale");
		for (int droppedPin : droppedPins) {
			System.out.println ("Round " + score.getRound() + ": " + droppedPin + " pins down. Action: " + score.score(droppedPin));
			score.getScore();
		}
		
		score.getScore();
		
	}
	
	public void getScore() {
		for (int i = 0; i < TOTAL_ROUNDS; i++) {

			System.out.println (this.points.get(i));

		}
	}
}
