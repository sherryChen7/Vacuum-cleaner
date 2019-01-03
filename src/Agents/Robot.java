package Agents;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import Grid.Case;
import Grid.Coordinates;
import Grid.Direction;
import Grid.Grid;

public class Robot implements Runnable {

	private Manor environment;
	// ==========================
	// Internal state information
	private Grid map;
	private PathComputer pathComputer;
	private Coordinates position;
	private int block_postion [][];
	//private int maxRefreshRate = 20;
	private int maxRefreshRate = 300;
	private int refreshRate;
	private float performance;
	private int nbActions;
	private static int upordown = 0 ; //up =1 down = 0
	private static int leftorright = 1; //left=0 right=1
	private static int movemode = 0;
	// ==========================

	public Robot(Manor environment) {
		this.environment = environment;
		// The robot knows the map and its own position once it has scanned at least once the environment
		this.map = null;
		this.pathComputer = null;
		this.position = null;
		// The number of iterations between two use of the robot sensors
		this.refreshRate = this.maxRefreshRate;
		this.nbActions = 0;
	}
	
	private void addIcon(JLabel label, String iconPath) {
		ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(iconPath)));
		label.setIcon(icon);
	}

	/**
	 * @param x
	 * @param y
	 */
	private void suckUp(int x, int y) {
		this.environment.suckUp(x, y);
		// Modifies the internal state of the robot
		this.map.getCase(x, y).removeDust();
		this.nbActions++;
	}

	/**
	 * @param x
	 * @param y
	 */
	private void pickJewel(int x, int y) {
		this.environment.pickJewel(x, y);
		// Modifies the internal state of the robot
		this.map.getCase(x, y).removeJewel();
		this.nbActions++;
	}
	
	private void generateDust(int x, int y) {
		    this.environment.generateDust(x, y);
			this.map.getCase(x,y).addDust();
	}

	/**
	 * @param dir
	 * @return validMove
	 */
	private boolean move(Direction dir) {
		generateDust(this.position.x, this.position.y);
		if (this.environment.moveRobot(dir)) {
			this.map.getCase(this.position.x, this.position.y).setRobot(false);
			//generateDust(22, 26);
			System.out.println( "dustlevel2222:" + map.getCase(22, 26).getDustLevel() );
			this.position.move(dir);
			this.map.getCase(this.position.x, this.position.y).setRobot(true);
			this.nbActions++;
			return true;
		}
		return false;
	}

	/** Makes the robot follow its current path
	 */
	private void followPath() {
		// Get the current value of the path from the path computer
		ArrayList<Coordinates> path = this.pathComputer.getPath();
		int living_room = 0;
		// If there is at least one objective
		// If there is no objective on the map, the robot doesn't move

//		if (living_room == 12) {
//			this.move(Direction.RIGHT);
//		}
		System.out.println(path);
		
		if (this.position.x==16 && this.position.y==4) {
			//path =[16,4];
		}
		
		if (((this.position.x==12 && this.position.y==35) || (this.position.x==20 && this.position.y==24)
		  || (this.position.x==21 && this.position.y==19) || (this.position.x==16 && this.position.y==4 )) && movemode ==0) {
			this.pathComputer.gonextroom(this.position.x,this.position.y);
			movemode = 1;
			}
		
		if (path.size() > 0) {
			Coordinates nextObjective = path.get(0);
			int diffX = nextObjective.x - this.position.x;
			if (diffX < 0) {
			this.move(Direction.UP);
				return;
			} else if (diffX > 0) {
				this.move(Direction.DOWN);
				return;
			} else {
				int diffY = nextObjective.y - this.position.y;
				if (diffY < 0) {
					this.move(Direction.LEFT);
					return;
				} else if (diffY > 0) {
					this.move(Direction.RIGHT);
					return;
				} else {
					// If the next objective is the current case, removes it, and then calls the
					// followPath again with the next objective
					this.pathComputer.removeFirstPathElement();
					this.followPath();
					System.out.println("gggggggggggggggggggggg");
					
				}
			}
		} else if (path.size() == 0 && movemode ==1) {
			movemode = 0;
			upordown = 0;
			leftorright=1;
		} else {		
		//System.out.println( "DUST 12 30 "+ this.map.getCase(12,30).getDustLevel());
		
		if (upordown == 1) {//往上
			if (this.map.getCase(this.position.x-1 ,this.position.y).isBlock()) { //上面那格
				if (this.map.getCase(this.position.x ,this.position.y-1).isBlock() && this.map.getCase(this.position.x ,this.position.y+1).isBlock()) { //左上右
					this.move(Direction.DOWN);
				}   else if (this.map.getCase(this.position.x ,this.position.y-1).isBlock() ) { //上左
					this.move(Direction.RIGHT);
				}  else if (this.map.getCase(this.position.x ,this.position.y+1).isBlock() ) { //上右
					this.move(Direction.LEFT);
					leftorright=0;
					upordown=0;
				}   else { //上
					if (leftorright==1 ) { //頭往右
						if ( this.map.getCase(this.position.x ,this.position.y+1).getDustLevel() < 1) {
					        this.move(Direction.RIGHT);
					       } else {
					    	this.move(Direction.LEFT);
					    	leftorright = 0;
					       }
					}  else { //頭往左
						System.out.println( "DUST!!!!!!!!!!! "+ this.map.getCase(this.position.x ,this.position.y-1).getDustLevel());
						if ( this.map.getCase(this.position.x ,this.position.y-1).getDustLevel() < 1) {
							this.move(Direction.LEFT); 	
						} else {
					    	this.move(Direction.RIGHT);
					    	leftorright = 1;
					       }
					}
					upordown=0;
				}
			 } else if (this.map.getCase(this.position.x+1,this.position.y).isBlock())  { //下面那格
				 if (this.map.getCase(this.position.x ,this.position.y-1).isBlock() && this.map.getCase(this.position.x ,this.position.y+1).isBlock()) { //左下右
					 this.move(Direction.UP);
				 } else if (this.map.getCase(this.position.x ,this.position.y-1).isBlock() ) { //下左
					 this.move(Direction.UP);
				 }  else if (this.map.getCase(this.position.x ,this.position.y+1).isBlock() ) { //下右
					 this.move(Direction.UP);
				 } else {
					 this.move(Direction.UP);
				 }			 
			 } else {
				 if (this.map.getCase(this.position.x ,this.position.y-1).isBlock() ) { //左
				     this.move(Direction.UP);
				 } else if (this.map.getCase(this.position.x ,this.position.y+1).isBlock() ) { //右
					 this.move(Direction.UP);
				 } else {
					 living_room = living_room + 1;
					 this.move(Direction.UP);
				 }
			 }
		} else {//往下
			if (this.map.getCase(this.position.x-1 ,this.position.y).isBlock()) { //上面那格
				if (this.map.getCase(this.position.x ,this.position.y-1).isBlock() && this.map.getCase(this.position.x ,this.position.y+1).isBlock()) { //左上右
					this.move(Direction.DOWN);
				}   else if (this.map.getCase(this.position.x ,this.position.y-1).isBlock() ) { //上左
					this.move(Direction.DOWN);
				}  else if (this.map.getCase(this.position.x ,this.position.y+1).isBlock() ) { //上右
					this.move(Direction.DOWN);
				}   else { //上
					this.move(Direction.DOWN);
					upordown=0;
				}
			 } else if (this.map.getCase(this.position.x+1,this.position.y).isBlock())  { //下面那格
				 if (this.map.getCase(this.position.x ,this.position.y-1).isBlock() && this.map.getCase(this.position.x ,this.position.y+1).isBlock()) { //左下右
					 this.move(Direction.UP);
				 } else if (this.map.getCase(this.position.x ,this.position.y-1).isBlock() ) { //下左
					 this.move(Direction.RIGHT);
					 upordown=1;
				 }  else if (this.map.getCase(this.position.x ,this.position.y+1).isBlock() ) { //下右
					 this.move(Direction.LEFT);
					 leftorright=0;
					 upordown=1;
				 } else {
					 if (leftorright==1) {
						 this.move(Direction.RIGHT);
					 } else {
						 this.move(Direction.LEFT); 
					 }
					 upordown=1;
				 }			 
			 } else {
				 if (this.map.getCase(this.position.x ,this.position.y-1).isBlock() ) { //左
				     this.move(Direction.DOWN);
				 } else if (this.map.getCase(this.position.x ,this.position.y+1).isBlock() ) { //右
					 this.move(Direction.DOWN);
				 } else {
					 this.move(Direction.DOWN);
				 }
			 }
		}
	 }	
	}

	/** The robot chooses an action depending on its internal state, then does it
	 */
	private void chooseAction() {
		if (this.map != null && this.position != null) {
			Case currentCase = map.getCase(this.position.x, this.position.y);
			// If there is a jewel on the current case, according to the robot's copy of the map
//			if (currentCase.hasJewel()) {
//				this.pickJewel(this.position.x, this.position.y);
//			}
//			// If there is dust on the current case, according to the robot's copy of the map
//			else if (currentCase.getDustLevel() > 0) {
//				this.suckUp(this.position.x, this.position.y);
//			}
//			// If there is nothing to do on the current case, the robot moves
//			else {
//				
//			}
			this.followPath();
		}
	}

	/** The robot uses its sensors to observe the environment
	 */
	private void observeEnvironment() {
		this.map = this.environment.getRoomsCopy();
		this.position = this.environment.getRobotPosition();
		this.block_postion = this.environment.getBlock();
	}

	/** The robot computes the path it has to follow, in a new thread
	 */
	private void updateState() {
		this.pathComputer = new PathComputer(this.map, this.position,this.block_postion);
		// Then the robot computes the path it has to follow, in a new thread
	//	(new Thread(this.pathComputer)).start();
		this.performanceMeasure();
	}

	/** Computes the performance of the robot over the last iterations
	 */
	private void performanceMeasure() {
		float nextPerf = (float) this.environment.getLostPoints() / ((float) this.nbActions + 1);
		//System.out.println("Perf : " + nextPerf);
		float perfDiff = nextPerf - this.performance;
		// If the robot has lost more points than during the previous cycle, decreases the refresh rate
		if (perfDiff > 0 && this.refreshRate - 2 >= 1) {
			this.refreshRate -= 2;
			this.environment.notifyRefreshRateGUI(this.refreshRate);
			//System.out.println("Decreasing refreshing rate.");
		} else if (perfDiff == 0 && this.refreshRate < this.maxRefreshRate) {
			this.refreshRate++;
			this.environment.notifyRefreshRateGUI(this.refreshRate);
			//System.out.println("Increasing refreshing rate.");
		}
		this.performance = nextPerf;
		//System.out.println("Refresh rate : " + this.refreshRate);
		// Reset the values used to measure performance
		this.nbActions = 0;
		this.environment.resetPoints();
		this.environment.notifyRefreshRateGUI(this.refreshRate);
	}

	@Override
	public void run() {
		// Time between two actions of the robot
		int sleepTime = 300;

		// Remaining iterations before next internal state update
		int beforeUpdate = 1;
		// The robot runs permanently
		while (true) {
			// First, the robot asks the environment for a map, and updates its internal state
			if (beforeUpdate <= 0) {
				this.observeEnvironment();
				this.updateState();
				beforeUpdate = this.refreshRate;
			}
			// Then, it chooses an action
			this.chooseAction();
			try {
				TimeUnit.MILLISECONDS.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			beforeUpdate--;
			//System.out.println("Before Update : " + beforeUpdate);
		}
	}

}