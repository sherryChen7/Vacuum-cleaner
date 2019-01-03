package Grid;
/* A case which can contain dust, jewels, and/or a robot
 */
public class Case {
	
	private int dustLevel;
	private boolean jewel;
	private boolean robot;
	private boolean charge;
	private boolean block;
	private boolean hide_block;
	private boolean keypoint;
	
	/** Default constructor
	 */
	public Case() {
		this.dustLevel = 0;
		this.jewel = false;
		this.robot = false;
		this.charge = false;
		this.block = false;
		this.hide_block = false;
		this.keypoint = false;		
	}
	
	/**
	 * @param dustLevel
	 * @param jewel
	 * @param robot
	 */
	public Case(int dustLevel, boolean jewel, boolean robot, boolean charge) {
		this.dustLevel = dustLevel;
		this.jewel = jewel;
		this.robot = robot;
		this.block = block;
		this.hide_block = hide_block;
		this.charge=charge;
	}
	
	/** Copy constructor, used to create an independent path for the robot
	 * @param c
	 */
	public Case(Case c) {
		this.dustLevel = c.dustLevel;
		this.jewel = c.jewel;
		this.robot = c.robot;
		this.block = c.block;
		this.hide_block = c.hide_block;
		this.keypoint = c.keypoint;
		this.charge=c.charge;
	}
	
	/**
	 * @return dustLevel
	 */
	public int getDustLevel() {
		return this.dustLevel;
	}
	
	/** Adds some dust (1 level) to the case
	 * @return dustLevel
	 */
	public int addDust() {
		return ++this.dustLevel;
	}
	
	/** Decreases the dust level of a case by 1, if possible
	 * @return dustLevel
	 */
	public int removeDust() {
		if (this.dustLevel >= 1) {
			return --this.dustLevel;
		} else {
			return 0;
		}
	}
	
	/**
	 * @return jewel
	 */
	public boolean hasJewel() {
		return this.jewel;
	}
	
	/** Adds a jewel to the case
	 * @return jewelAdded
	 * True if a jewel has been added, else false
	 */
	public boolean addJewel() {
		boolean jewelAdded = this.jewel;
		this.jewel = true;
		return jewelAdded;
	}
	
	/** Removes the jewel from a case
	 * @return jewelRemoved
	 * True if a jewel has been removed, else false
	 */
	public boolean removeJewel() {
		boolean jewelRemoved = this.jewel;
		this.jewel = false;
		return jewelRemoved;
	}
	
	/**
	 * @return robot
	 */
	public boolean isRobot() {
		return this.robot;
	}
	
	public boolean isCharge() {
		return this.charge;
	}
	
	public boolean isBlock() {
		return this.block;
	}
	
	public boolean isHide_Block() {
		return this.hide_block;
	}
	
	public boolean isKeypoint() {
		return this.keypoint;
	}
	
	/**
	 * @param robot
	 */
	public void setRobot(boolean robot) {
		this.robot = robot;
	}
	
	public void setCharge(boolean charge) {
		this.charge = charge;
	}
	
	/**
	 * @param block
	 */
	public void setBlock(boolean block) {
		this.block = block;
	}
	
	public void setHide_Block(boolean hide_block) {
		this.hide_block = hide_block;
	}
	
	public void setKeypoint(boolean keypoint) {
		this.keypoint = keypoint;
	}
	
	/** Overwriting of the toString method, debug use
	 */
	public String toString() {
		return "DL : " + this.dustLevel + " ; J : " + this.jewel + " ; R : " + this.robot;
	}
	
}