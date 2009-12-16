package bowling.logic.score;



public class Point {
	
	public static final int SPARE = 11;
	public static final int STRIKE = 12;
	
	protected static final int EMPTY_SLOT = -1;
	protected static final int EMPTY_THIRD_SHOOT_SLOT = -2;
	
	protected static final int X = 0;
	protected static final int Y = 1;
	
	private int[] values;
	
	public Point() {
		this.values = new int[LabelType.values().length];
		
		for (int i = 0; i < LabelType.values().length; i++) {
			if (i != LabelType.THIRD_SHOOT.ordinal()) {
				this.values[i] = EMPTY_SLOT;
			}
			else {
				this.values[i] = EMPTY_THIRD_SHOOT_SLOT;
			}
		}
		
	}


	private String valueToStr(int value) {
		if(value == 0) {
			return "-";
		} else if (value == SPARE) {
			return "/";
		} else if (value == STRIKE) {
			return "X";
		} else if (value == EMPTY_THIRD_SHOOT_SLOT) {
			return "";
		}		
		else {
			return new Integer(value).toString();
		}
	}
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		if (this.getFirstShootValue() == STRIKE) {
			str.append(valueToStr(getFirstShootValue()));
			str.append(" ");
			if (this.getSecondShootValue() == STRIKE) {
				str.append(valueToStr(getSecondShootValue()));
			}
			else {
				str.append(valueToStr(this.getSecondShootValue()));
				if (this.getThirdShootValue() != EMPTY_THIRD_SHOOT_SLOT) {
					str.append(" ");
					str.append(valueToStr(this.getThirdShootValue()));
				}
			}
		}
		else {
			if (this.getSecondShootValue() == STRIKE) {
				str.append(valueToStr(getSecondShootValue()));
			}
			else {
				str.append(valueToStr(this.getFirstShootValue()));
				str.append(" ");
				str.append(valueToStr(this.getSecondShootValue()));
				if (this.getThirdShootValue() != EMPTY_THIRD_SHOOT_SLOT) {
					str.append(" ");
					str.append(valueToStr(this.getThirdShootValue()));
				}
			}
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

	public int getThirdShootValue() {
		int value = this.values[LabelType.THIRD_SHOOT.ordinal()];
		
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
