package Agents;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import GUI.GridFrame;
import Grid.Coordinates;
import Grid.Direction;
import Grid.Grid;

public class Manor implements Runnable {

	private Grid rooms;
	private GridFrame gui;
	private Coordinates robotPosition;
	private Coordinates chargePosition;
	private Coordinates blockPosition;
	private Coordinates keypointPosition;
	private int lostPoints;
	private int block_postion [][];

	public Manor(int sizeX, int sizeY) {
		this.rooms = new Grid(sizeX, sizeY);
		this.gui = null;
		this.lostPoints = 0;
	}

	/**
	 * @return rooms
	 */
	public Grid getRooms() {
		return this.rooms;
	}
	
	/** Returns a copy of the rooms of the manor
	 * @return roomsCopy
	 */
	public Grid getRoomsCopy() {
		return new Grid(this.rooms);
	}
	
	/**
	 * @return gui
	 */
	public GridFrame getGui() {
		return gui;
	}

	/**
	 * @param gui
	 */
	public void setGui(GridFrame gui) {
		this.gui = gui;
	}
	
	/** Sends the actualized version of the map to the GUI whenever it is modified,
	 * if a GUI exists
	 */
	public synchronized void notifyGUI() {
		if (this.gui != null) {
			System.out.println("notifyGUI");
			this.gui.update(this.rooms);
			
		}
	}

	/** Changes the refresh rate displayed on the GUI, if it exists
	 * @param refreshRate
	 */
	public synchronized void notifyRefreshRateGUI(int refreshRate) {
		if (this.gui != null) {
			this.gui.updateRefreshRate(refreshRate);
		}
	}

	/** Increments the counter of lost jewels on the GUI, if it exists
	 */
	public synchronized void notifyJewelsGUI() {
		if (this.gui != null) {
			this.gui.updateLostJewels();
		}
	}
	
	/** Sets the position of the robot on the map
	 * @param x
	 * @param y
	 * @return isValid
	 * True if the position is valid, else false
	 */
	public boolean setRobotPosition(int x, int y) {
		if (this.isValidCase(x, y)) {
			this.robotPosition = new Coordinates(x, y);
			this.getRooms().getCase(x, y).setRobot(true);
			return true;
		}
		return false;
	}
	
	public boolean setChargePosition(int x, int y) {
		if (this.isValidCase(x, y)) {
			this.chargePosition = new Coordinates(x, y);
			this.getRooms().getCase(x, y).setCharge(true);
			return true;
		}
		return false;
	}
	
	
	public boolean setBlock() {
		//int block_postion [][] = {{2,3},{3,4},{4,3},{4,4},{5,4},{6,3}};
		this.block_postion  = new int [][]{{17,32},{17,33},{18,32},{18,33},{19,34},{20,34},
				                  {21,34},{22,34},{23,34},{24,34},{25,34},{19,35},
				                  {20,35},{21,35},{22,35},{23,35},{24,35},{25,35},
				                  {26,32},{26,33},{27,32},{27,33},{22,31},{22,32},
				                  {23,31},{23,32},
				                  {0,15},{0,16},{0,17},{0,18},{0,19},{0,20},
				                  {1,15},{1,16},{1,17},{1,18},{1,19},{1,20},
				                  {2,15},{2,16},{2,17},{2,18},{2,19},{2,20},
				                  {3,15},{3,16},{3,17},{3,18},{3,19},{3,20},
				                  {8,20},{8,21},{8,22},{8,23},{8,24},
				                  {9,20},{9,21},{9,22},{9,23},{9,24},
				                  {10,20},{10,21},{10,22},{10,23},{10,24},
				                  {11,20},{11,21},{11,22},{11,23},{11,24},
				                  {12,20},{12,21},{12,22},{12,23},{12,24},
				                  {9,16},{10,15},{10,16},{10,17},{11,14},{11,15},{11,16},{11,17},{11,18},{12,15},{12,16},{12,17},{13,16},
				                  {18,25},{19,25},{20,25},{21,25},{22,25},{23,25},{24,25},{25,25},{26,25},{27,25},{28,25},//«ÈÆU¥ªÀð
				                  {19,15},{19,16},{19,17},{19,18},{19,19},{19,20},
				                  {20,15},{20,16},{20,17},{20,18},{20,19},{20,20},
				                  {19,20},{20,20},{21,20},{22,20},{23,20},{24,20},{25,20},{26,20},{27,20},{28,20},
				                  {22,4},{22,5},{22,6},{22,7}, //§É(¥H¤U¥|¦æ)
				                  {23,4},{23,5},{23,6},{23,7},
				                  {24,4},{24,5},{24,6},{24,7},
				                  {25,4},{25,5},{25,6},{25,7},
				                  {19,10},{20,10},{21,10},{22,10},{23,10},{24,10},{25,10},{26,10},{27,10},{28,10},
				                  {19,11},{20,11},{21,11},{22,11},{23,11},{24,11},{25,11},{26,11},{27,11},{28,11},
				                  //{23,17},{23,18},{23,19},//ª×«Ç2§É
				                  {24,16},{24,17},{24,18},{24,19},//ª×«Ç2§É
				                  {25,16},{25,17},{25,18},{25,19},//ª×«Ç2§É
				                  {26,16},{26,17},{26,18},{26,19},//ª×«Ç2§É
				                  {27,16},{27,17},{27,18},{27,19},//ª×«Ç2§É
				                  {28,12},{28,13},{28,14},{28,15},{28,16},{28,17},{28,18},{28,19},//ª×«Ç2§É
				                  {24,12},{25,12},{26,12},{27,12},//ª×«Ç2§É¹ï­±Âd¤l
				                  {24,13},{25,13},{26,13},{27,13},//ª×«Ç2§É¹ï­±Âd¤l
				                  {15,4},{15,5},{15,6},{15,7},{15,8},{15,9},{15,10},{15,11},
				                  {0,0},{1,0},{2,0},{3,0},{4,0},{5,0},{6,0},{7,0},{8,0},{9,0},{10,0},{11,0},{12,0},{13,0},//¿ûµ^«Ç¥ªÀð
				                  {0,1},{1,1},{2,1},{3,1},{4,1},{5,1},{6,1},{7,1},
				                  {5,2},{6,2},{7,2},{5,3},{6,3},{7,3},
				                  {14,0},{14,1},{14,2},{14,3},
				                  {15,0},{15,1},{15,2},{15,3},
				                  {16,0},{16,1},{16,2},{16,3},
				                  {17,0},{17,1},{17,2},{17,3},
				                  {18,3}, {19,3}, {20,3}, {21,3}, {22,3}, {23,3}, {24,3}, {25,3}, {26,3}, {27,3}, {28,3},
				                  {8,25},{8,26},{8,27},{8,28},{8,29},{8,30},
				                  {8,31},{9,31},{10,31},{11,31}, //¥ÈÃö
				                  {11,32},{11,33},{11,34},{11,35},
				                  {6,4},{7,4},{7,5},{7,7},{7,10},{7,11},
				                  {8,5},{8,6},{8,7},{8,8},{8,9},
				                  {9,5},{9,6},{9,7},{9,8},{9,9},
				                  {10,5},{10,6},{10,7},{11,5},{11,6},
				                  {0,2},{0,3},{0,4},{0,5},{0,6},{0,7},{0,8},{0,9},{0,10},{0,11},{0,12},{0,13},{0,14},//¤WÀð
				                  {1,6},{1,7},{1,8},{1,9},{1,10},
				                  {2,6},{2,7},{2,8},{2,9},{2,10},
				                  {3,6},{3,7},{3,8},{3,9},{3,10},
				                  {4,20},{5,20},{6,20},{7,20},
				                  {29,3},{29,4},{29,5},{29,6},{29,7},{29,8},{29,9},{29,10},{29,11},{29,12},{29,13},{29,14},{29,15},
				                  {29,16},{29,17},{29,18},{29,19},{29,20},{29,21},{29,22},{29,23},{29,24},{29,25},{29,26},{29,27},{29,28},{29,29},{29,30},//³Ì¤UÀð
				                  {29,31},{29,32},{29,33},{29,34},{29,35},
				                  {7,15},{7,16},{7,17},{7,18},{7,19},
				                  {8,11},{9,11},{13,11},{14,11},
				                  {11,36},{12,36},{13,36},{14,36},{15,36},{16,36},{17,36},{18,36},{19,36},{20,36},{21,36},{22,36},{23,36},{24,36},{25,36},{26,36},{27,36},{28,36},{29,36},
				                  {7,6},{7,8},{7,9},{5,4}
		};

		
		for (int i = 0; i < this.block_postion.length; i++) {
			if (this.isValidCase(this.block_postion[i][0], this.block_postion[i][1])) {
				this.blockPosition = new Coordinates(this.block_postion[i][0], this.block_postion[i][1]);
				this.getRooms().getCase(this.block_postion[i][0], this.block_postion[i][1]).setBlock(true);
			} else {
				continue;
			}
		}
		
		int hide_block_postion [][] = {{19,22},{19,23},{19,24}, //¬~¦ç¶¡ÁôÀð
				{19,13},{19,14}, //ª×«Ç2ÁôÀð  
				{7,13},{7,14}  //¥Dª×«ÇÁôÀð
		};
		
		for (int i = 0; i < hide_block_postion.length; i++) {
			if (this.isValidCase(hide_block_postion[i][0], hide_block_postion[i][1])) {
				//this.blockPosition = new Coordinates(hide_block_postion[i][0], hide_block_postion[i][1]);
				this.getRooms().getCase(hide_block_postion[i][0], hide_block_postion[i][1]).setBlock(true);
				this.getRooms().getCase(hide_block_postion[i][0], hide_block_postion[i][1]).setHide_Block(true);
			} else {
				continue;
			}
		}
		
		return true;
	}
	
	public boolean setKeypoint() {
		int keypoint_postion [][] = {{21,28},{21,29},{21,30},{21,31},
				                     {22,28},{22,29},{22,30},
				                     {23,28},{23,29},{23,30},
				                     {24,28},{24,29},{24,30},{24,31},
				                     {5,12},{5,13},{5,14},{5,15},{5,16},{5,17},{5,18},
				                     //{6,12},{6,13},{6,14},{6,15},{6,16},{6,17},{6,18},
				                     //{7,12},{7,13},{7,14},{7,15},{7,16},{7,17},{7,18},				                     
				                     {14,12},{14,13},{14,14},{14,15},{14,16},{14,17},{14,18},{14,19},{14,20},
				                     {14,21},{14,22},{14,23},{14,24},{14,25},{14,26},{14,27},{14,28},{14,29},
				                     {15,12},{15,13},{15,14},{15,15},{15,16},{15,17},{15,18},{15,19},{15,20},
				                     {15,21},{15,22},{15,23},{15,24},{15,25},{15,26},{15,27},{15,28},{15,29},
				                     {16,12},{16,13},{16,14},{16,15},{16,16},{16,17},{16,18},{16,19},{16,20},
				                     {16,21},{16,22},{16,23},{16,24},{16,25},{16,26},{16,27},{16,28},{16,29}

		};
		
		for (int i = 0; i < keypoint_postion.length; i++) {
			if (this.isValidCase(keypoint_postion[i][0], keypoint_postion[i][1])) {
				this.keypointPosition = new Coordinates(keypoint_postion[i][0], keypoint_postion[i][1]);
				this.getRooms().getCase(keypoint_postion[i][0], keypoint_postion[i][1]).setKeypoint(true);
			} else {
				continue;
			}
		}
		return true;
	}
	
	
	public int[][] getBlock() {
		return this.block_postion;
	}
	
	/**
	 * @return xRobot
	 */
	public Coordinates getRobotPosition() {
		return this.robotPosition;
	}
	
	/**
	 * @return lostPoints
	 */
	public int getLostPoints() {
		return this.lostPoints;
	}
	
	/** Resets the number of points lost
	 */
	public void resetPoints() {
		this.lostPoints = 0;
	}
	
	/** Asks the environment to move the robot from one case in a given direction
	 * @param dir
	 * @return authorizedMove
	 * True if the move is legal, ie if the robot is not heading outside the grid,
	 * or staying at the same place
	 */
	public boolean moveRobot(Direction dir) {
		boolean authorizedMove = false;
		int oldX = this.robotPosition.x;
		int oldY = this.robotPosition.y;
		int dx = 0;
		int dy = 0;
		switch (dir) {
		case UP: dx--;
			break;
		case DOWN: dx++;
			break;
		case LEFT: dy--;
			break;
		case RIGHT: dy++;
			break;
		default: System.out.println(" # Where am I supposed to go ?!");
			break;
		}
		// The robot can't go outside the grid, or stay at the same place (when calling "move")
		if (this.isValidCase(oldX + dx, oldY + dy) && (dx != 0 || dy != 0)) {
			// Set robot to false in the previous location
			this.rooms.getCase(oldX, oldY).setRobot(false);
			// Set it to true in the destination case
			this.setRobotPosition(oldX + dx, oldY + dy);
			authorizedMove = true;
			// The GUI is notified of the changes
			System.out.println("movemovemovemove");
			this.notifyGUI();
		}
		return authorizedMove;
	}
	
	/**
	 * @param x
	 * @param y
	 * @return validCase
	 * True if the case with given coordinates exists, else false
	 */
	private boolean isValidCase(int x, int y) {
		return (x >= 0) && (y >= 0) && (x < this.rooms.getSizeX()) && (y < this.rooms.getSizeY());
	}
	
	/** Asks the environment to remove one unity of dust from the (x, y) case, and the
	 * jewel if one is present
	 * @param x
	 * @param y
	 */
	public void suckUp(int x, int y) {
		this.rooms.getCase(x, y).removeDust();
		if (this.rooms.getCase(x, y).removeJewel()) {
			this.lostPoints += 10;
			//System.out.println("Jewel lost !");
			this.notifyJewelsGUI();
		}
		// The GUI is notified of the changes
		System.out.println("suckupsuckup");
		this.notifyGUI();
	}
	
	/** Asks the environment to pick (remove) the jewel from the (x, y) case
	 * @param x
	 * @param y
	 */
	public void pickJewel(int x, int y) {
		this.rooms.getCase(x, y).removeJewel();
		// The GUI is notified of the changes
		System.out.println("pickpickpickpick");
		this.notifyGUI();
	}
	
	/** The environment generates a fixed amount of dust in randomly chosen cases
	 */
	public void generateDust(int x, int y) {
//		int dustQuantity = 2;
//		int randX = 0;
//		int randY = 0;
		//for (int i = 0; i < dustQuantity; i++) {
//			randX = ThreadLocalRandom.current().nextInt(0, this.rooms.getSizeX());
//			randY = ThreadLocalRandom.current().nextInt(0, this.rooms.getSizeY());
			this.rooms.getCase(x, y).addDust();
//			System.out.println("geneergengengenngen");
//			this.notifyGUI();
		//}
	}
	
	/** The environment generates a fixed amount of jewels in randomly chosen cases
	 */
	private void generateJewel() {
		int jewelQuantity = 1;
		int randX = 0;
		int randY = 0;
		for (int i = 0; i < jewelQuantity; i++) {
			randX = ThreadLocalRandom.current().nextInt(0, this.rooms.getSizeX());
			randY = ThreadLocalRandom.current().nextInt(0, this.rooms.getSizeY());
			this.rooms.getCase(randX, randY).addJewel();
		}
	}
	
	@Override
	public void run() {
		// The environment runs permanently
		while (true) {
			try {
				// Time before two modifications of the environment by itself
				int sleepTime = 5000;
				//generatedust
				//this.generateDust();
			    //this.generateJewel();
				// The GUI is notified of the changes
				System.out.println("runrunrunurnurn");
				//this.notifyGUI();
				// The environment waits before next modification
				TimeUnit.MILLISECONDS.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}