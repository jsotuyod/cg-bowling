package bowling.logic.score;

import java.util.ArrayList;


public class Score {

	private int totalPoints;
	private int currRound;
	private int currShoot;
	
	private String playerName;
	
	private static final int TOTAL_ROUNDS = 10;
	private static final int TOTAL_PINS = 10;
	
	private ArrayList<Point> points;
	private ArrayList<PendingPoint> pendingPoints;
	
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
	
	public Score(String playerName) {
		
		this.playerName = playerName;
		this.points = new ArrayList<Point>();
		this.pendingPoints = new ArrayList<PendingPoint>();
		
		this.totalPoints = 0;
		this.currRound = 0;
		this.currShoot = 0;
		
		
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
		
		int[] droppedPins = {9, 1, 4, 3};
		Score score = new Score("Ale");
		for (int droppedPin : droppedPins) {
			System.out.println ("Round " + score.getRound() + ": " + droppedPin + " pins down. Action: " + score.score(droppedPin));
		}
		
		score.getScore();
		
	}
	
	public void getScore() {
		for (int i = 0; i < TOTAL_ROUNDS; i++) {

			System.out.println (this.points.get(i));

		}
	}
}
