package bowling.logic.score;

public class PendingPoint {
	Point point;
	int ttl;
	int extraPoints;
	
	public PendingPoint(Point point, int ttl) {
		this.point = point;
		this.ttl = ttl;
	}
	
	public int isPending(int points) {
		this.ttl--;
		extraPoints += points;
		
		if (this.ttl == 0) {
			return extraPoints + 10;
		} else {
			return 0;
		}
	}
	
	public Point getPoint() {
		return point;
	}
	
}
