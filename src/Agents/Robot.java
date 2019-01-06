package Agents;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import Grid.Case;
import Grid.Coordinates;
import Grid.Direction;
import Grid.Grid;
import GUI.GridFrame;

public class Robot implements Runnable {

	private Manor environment;
	// ==========================
	// Internal state information
	private Sensor sensor;
	private Grid map;
	private PathComputer pathComputer;
	private Coordinates position;
//	private int maxRefreshRate = 20;
	private int maxRefreshRate = 300;
	private int refreshRate;
	private float performance;
	private int nbActions;
	private static int upordown = 0 ; //up =1 down = 0
	private static int leftorright = 1; //left=0 right=1
	private static int movemode = 0;
	private Direction horizentalDir = Direction.RIGHT; 
	private Direction verticalDir = Direction.DOWN;
	// ==========================

	public Robot(Manor environment) {
		this.environment = environment;
		this.sensor = new Sensor() {
			
			@Override
			public Case senseMap(int x, int y) {
				return environment.getRooms().getCase(x, y);
			}
		};
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
		GUI.GridFrame.power -= 0.05;
		System.out.println("suck");
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
			this.map.getCase(this.position.x, this.position.y).setBy(sensor.senseMap(this.position.x, this.position.y));
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
		System.out.println(path);
		
		/*
		 * 憒����撌脰粥����韏堆�停�銵Star�撠���韏圈����
		 */
		if (movemode == 0 &&
				isCaseBlockOrVisited(this.position.x+1, this.position.y) &&
				isCaseBlockOrVisited(this.position.x-1, this.position.y) &&
				isCaseBlockOrVisited(this.position.x, this.position.y+1) &&
				isCaseBlockOrVisited(this.position.x, this.position.y-1)) {
			
			List<Coordinates> targetsCoordinate = new ArrayList<>();
			for (int i = 0; i < map.getSizeX(); i++) {
				for (int j = 0; j < map.getSizeY(); j++) {
					Case tmp = map.getCase(i, j);
					/*
					 * �皜�暺�(Unknown)蝑嚗�憒������暺�脰�Star撠楝嚗�之撟���鈭箄����
					 * ��隞交迨蝑�撠��府�暺��撠��暺撌脩暺���暺黎銝准��
					 */
					if (tmp.isUnknown() && 
							(!isCaseUnknown(i-1, j) ||
							!isCaseUnknown(i+1, j) ||
							!isCaseUnknown(i, j-1) ||
						    !isCaseUnknown(i, j+1))) {
						targetsCoordinate.add(new Coordinates(i, j)); //���暺黎
					}
				}
			}
			this.pathComputer.gonextroom(this.position.x,this.position.y, targetsCoordinate); //敺�暺黎銝剜���餈���
			movemode = 1;
		}
		
		if (path.size() > 0) {
			Coordinates nextObjective = path.get(0);
			this.pathComputer.removeFirstPathElement();
			int diffX = nextObjective.x - this.position.x;
			int diffY = nextObjective.y - this.position.y;
			
			if(diffX == 0 && diffY == 0) //it is self
				return;
			
			if(diffX != 0 && !isCaseBlock(this.position.x + diffX ,this.position.y)) {
				if(diffX < 0)
					this.move(Direction.UP);
				else if(diffX > 0)
					this.move(Direction.DOWN);
				return;
			}
			
			if(diffY != 0 && !isCaseBlock(this.position.x,this.position.y + diffY)) {
				if (diffY < 0)
					this.move(Direction.LEFT);
				else if (diffY > 0)
					this.move(Direction.RIGHT);
				return;
			}
			
			this.pathComputer.clearPath();
			this.followPath();
		} else if (path.size() == 0 && movemode ==1) {
			movemode = 0;
			upordown = 0;
			leftorright=1;
		} else {		
		//System.out.println( "DUST 12 30 "+ this.map.getCase(12,30).getDustLevel());
			if (verticalDir == Direction.DOWN) {
				if(isCaseBlockOrVisited(this.position.x + 1, this.position.y)) {
					verticalDir = Direction.UP;
					
					if(isCaseBlockOrVisited(this.position.x, this.position.y + 1) && isCaseBlockOrVisited(this.position.x, this.position.y - 1)) {
						move(verticalDir);
					} else if (isCaseBlockOrVisited(this.position.x, this.position.y + 1)) {
						horizentalDir = Direction.LEFT;
						move(horizentalDir);
					} else if (isCaseBlockOrVisited(this.position.x, this.position.y - 1)) {
						horizentalDir = Direction.RIGHT;
						move(horizentalDir);
					} else {
						move(horizentalDir);
					}
				} else {
					move(verticalDir);
				}
			} else {
				if(isCaseBlockOrVisited(this.position.x - 1, this.position.y)) {
					verticalDir = Direction.DOWN;
					
					if(isCaseBlockOrVisited(this.position.x, this.position.y + 1) && isCaseBlockOrVisited(this.position.x, this.position.y - 1)) {
						move(verticalDir);
					} else if (isCaseBlockOrVisited(this.position.x, this.position.y + 1)) {
						horizentalDir = Direction.LEFT;
						move(horizentalDir);
					} else if (isCaseBlockOrVisited(this.position.x, this.position.y - 1)) {
						horizentalDir = Direction.RIGHT;
						move(horizentalDir);
					} else {
						move(horizentalDir);
					}
				} else {
					move(verticalDir);
				}
			}
	    }	
	}

	private boolean isCaseBlockOrVisited(int x, int y) {
		return (!this.isCaseUnknown(x, y)) || this.isCaseBlock(x, y);
	}
	
	private boolean isCaseBlock(int x, int y) {
		if(this.map.getCase(x, y)==null || this.map.getCase(x, y).isUnknown()) {
			Case senseCase = sensor.senseMap(x, y);
			if(senseCase==null) {
				return true;
			}
			else if (senseCase.isBlock()) {
				this.map.getCase(x, y).setBy(senseCase);
			}
			return senseCase.isBlock();
		}
		return this.map.getCase(x, y).isBlock();
	}
	
	private boolean isCaseUnknown(int x, int y) {
		return this.map.getCase(x, y)==null || this.map.getCase(x, y).isUnknown();
	}

	/** The robot chooses an action depending on its internal state, then does it
	 */
	private void chooseAction() {
		if (this.map != null && this.position != null) {
			GUI.GridFrame.power -= 0.01;
//			Case currentCase = map.getCase(this.position.x, this.position.y);
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
//		this.map = this.environment.getRoomsCopy();
		this.map = new Grid(this.environment.getRooms().getSizeX(), this.environment.getRooms().getSizeY(), true);
		this.position = this.environment.getRobotPosition();
		this.pathComputer = new PathComputer(this.map);
	}

	@Override
	public void run() {
		// Time between two actions of the robot
		int sleepTime = 300;
		this.observeEnvironment();
		// The robot runs permanently
		while (true) {
			System.out.println(GUI.GridFrame.power);
			// chooses an action
			this.chooseAction();
			try {
				TimeUnit.MILLISECONDS.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}