package bowling.logic.score;



public class Point {
	
	public static final int SPARE = 11;
	public static final int STRIKE = 12;
	
	protected static final int EMPTY_SLOT = -1;

	protected static final int X = 0;
	protected static final int Y = 1;
	
	private int[] values;
	
	public Point() {
		this.values = new int[LabelType.values().length];
		
		for (int i = 0; i < LabelType.values().length; i++) {
			this.values[i] = EMPTY_SLOT;
		}
		
	}


	private String valueToStr(int value) {
		if(value == 0) {
			return "-";
		} else if (value == SPARE) {
			return "/";
		} else if (value == STRIKE) {
			return "X";
		} else {
			return new Integer(value).toString();
		}
	}
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		if (this.getSecondShootValue() == STRIKE) {
			str.append(valueToStr(getSecondShootValue()));
		}
		else {
			str.append(valueToStr(this.getFirstShootValue()));
			str.append(" ");
			str.append(valueToStr(this.getSecondShootValue()));
		}
		/*if (this.getSubTotalValue() != 0) {
			str.append(" Subtotal: " + this.getSubTotalValue());
		}*/
		return (str.toString());
	}
		


	public int getSecondShootValue() {
		int value = this.values[LabelType.SECOND_SHOOT.ordinal()];
		
		if (value == EMPTY_SLOT) {
			return 0;
		}
		
		return value;
	}
	
	public int getFirstShootValue() {
		int value = this.values[LabelType.FIRST_SHOOT.ordinal()];
		
		if (value == EMPTY_SLOT) {
			return 0;
		}
		
		return value;
	}

	public int getSubTotalValue() {
		int value = this.values[LabelType.SUBTOTAL.ordinal()];
		
		if (value == EMPTY_SLOT) {
			return 0;
		}
		
		return value;
	}
	
	public void updateValue(LabelType labelType, int value) {
		int index = labelType.ordinal();
		
		this.values[index] = value;
		
		
	}
	
}
