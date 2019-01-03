package Agents;

import java.util.ArrayList;
import java.util.List;

import Grid.Coordinates;
import Grid.Grid;

public class PathComputer implements Runnable {
	
	private Grid grid;
	private Coordinates robotPosition;
	private ArrayList<Coordinates> path;
	private boolean computing;
	private int block_postion [][];
	
	public PathComputer(Grid grid, Coordinates robotPosition,int[][] block) {
		this.grid = grid;
		this.robotPosition = robotPosition;
		this.path = new ArrayList<Coordinates>();
		this.computing = true;
		this.block_postion = block;
	}
	
	/**
	 * @return path
	 */
	public ArrayList<Coordinates> getPath() {
		return this.path;
	}
	
	/** Adds a set of coordinates at the end of the path
	 * @param coordinates
	 */
	public synchronized void addToPath (Coordinates coordinates) {
		this.path.add(coordinates);
	}
	
	/** Removes the first element of the path
	 */
	public synchronized void removeFirstPathElement() {
		if (this.path.size() > 0) {this.path.remove(0);}
	}
	
	/**
	 * @return computing
	 */
	public boolean isComputing() {
		return this.computing;
	}
	
	/** Looks for all the objectives (cases with dust/jewel) in the map
	 * @return objectives
	 */
	private ArrayList<Coordinates> findObjectives() {
		ArrayList<Coordinates> objectives = new ArrayList<Coordinates>();
		int sizeX = this.grid.getSizeX();
		int sizeY = this.grid.getSizeY();
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				if (this.grid.getCase(i, j).getDustLevel() > 0 || this.grid.getCase(i, j).hasJewel()) {
					objectives.add(new Coordinates(i, j));
				}
			}
		}
		return objectives;
	}
	
	/** Sort the objectives, by minimizing the distance between two consecutive ones.
	 * Although not optimal, this works in n2 instead of n!, is close to optimal in this particular case.
	 * @param objectives
	 */
	private void sortObjectives(ArrayList<Coordinates> objectives) {
		if (this.grid != null && this.robotPosition != null) {
			int nbObjectives = objectives.size();
			// The index of the minimum found
			int minimumIndex = 0;
			double distanceMin = this.grid.getSizeX() + this.grid.getSizeY();
			double currentDistance;
			Coordinates referenceCoordinates = this.robotPosition;
			Coordinates coord;
			double dust;
			for (int i = 0; i < nbObjectives; i++) {
				for (int j = 0; j < nbObjectives - i; j++) {
					coord = objectives.get(j);
					dust = this.grid.getCase(coord.x, coord.y).getDustLevel();
					if (dust == 0) dust++;
					// Divides by sqrt(dust) to force the robot to go to cells with lots of dust
					currentDistance = (double) referenceCoordinates.distance(coord) / Math.sqrt(dust);
					if (currentDistance < distanceMin) {
						minimumIndex = j;
						distanceMin = currentDistance;
					}
				}
				// Changes the reference coordinates
				referenceCoordinates = objectives.get(minimumIndex);
				// Reset the minimum distance
				distanceMin = this.grid.getSizeX() + this.grid.getSizeY();
				// Adds the minimum to the path
				this.addToPath(objectives.get(minimumIndex));
				// Removes it from the objectives
				objectives.remove(minimumIndex);
			}
		}
	}
	
	public synchronized void gonextroom(int x,int y) {
		Node initialNode = new Node(x, y);
		Node finalNode = new Node(20, 21);
		if (x==12 && y==35) {//客廳終點
          finalNode = new Node(20, 21); //洗衣間起點
		} else if (x==20 && y==24) { //洗衣間終點
		 finalNode = new Node(20,12); //臥室2起點
		} else if (x==21 && y==19) {
		 finalNode = new Node(16,9);
		} else if (x==16 && y==4) {
		  finalNode = new Node(16,4);
		}
		
        int rows = this.grid.getSizeX();
        int cols = this.grid.getSizeY();
        //int[][] blocksArray1= this.grid.
        AStar aStar = new AStar(rows, cols, initialNode, finalNode);
//        int[][] blocksArray = new int[][]{{18,25},{19,25},{20,25},{21,25},{22,25},{23,25},{24,25},{25,25},{26,25},{27,25},{28,25},{29,25},{29,24},{29,23},{29,22},{29,21}, //客廳左牆
//        	                             {19,22},{19,23},{19,24} //洗衣間隱牆
//        	                             };
        int[][] blocksArray = this.block_postion;
        aStar.setBlocks(blocksArray);
        List<Node> nextpath = aStar.findPath();
        for (Node node : nextpath) {
//            System.out.println( "rows====" + node.getRow() + "   cols=====" + node.getCol());
        	//if(node.getRow()!=initialNode.getRow() && node.getCol() !=initialNode.getCol())
            this.addToPath(new Coordinates(node.getRow(), node.getCol()));
        }
        System.out.println("gonextroom()gonextroom()gonextroom()gonextroom()gonextroom()gonextroom()gonextroom()gonextroom()gonextroom()");
        
	}

	@Override
	public void run() {
		// Looks for all the objectives (cases with dust/jewel) in the map
		ArrayList<Coordinates> objectives = this.findObjectives();
		// Sort them according to their distance to the robot
		this.sortObjectives(objectives);
		// Set to false to show the robot it has finished computing
		this.computing = false;
	}
	
}