package Agents;

import java.util.ArrayList;
import java.util.List;

import Algorithm.AStar;
import Algorithm.AStarListener;
import Algorithm.Node;
import Grid.Coordinates;
import Grid.Grid;

public class PathComputer {
	private Grid grid;
	private ArrayList<Coordinates> path;
	
	public PathComputer(Grid grid) {
		this.grid = grid;
		this.path = new ArrayList<Coordinates>();
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
	private synchronized void addToPath (Coordinates coordinates) {
		this.path.add(coordinates);
	}
	
	/** Removes the first element of the path
	 */
	public synchronized void removeFirstPathElement() {
		if (this.path.size() > 0) {this.path.remove(0);}
	}
	
	public synchronized void clearPath() {
		this.path.clear();
	}

	public synchronized void gonextroom(int x,int y, List<Coordinates> targets) {
		Node initialNode = new Node(y, x);
		
        int rows = this.grid.getSizeX();
        int cols = this.grid.getSizeY();
        //int[][] blocksArray1= this.grid.
//        MapInfo mapInfo = new MapInfo();
        AStar aStar = new AStar();
        aStar.listener = new AStarListener() {
			
			@Override
			public boolean canAddNodeToOpen(int x, int y) {
				if (grid.getCase(y, x) != null) {
					if (grid.getCase(y, x).isBlock()) {
						return false;
					}
				} else {
					return false;
				}
						
				return true;
			}
		};

		int minCost = 9999999;
		List<Node> minCostPath = new ArrayList<>();
        
        for (Coordinates targetCoordinate : targets) {
        	Node finalNode = new Node(targetCoordinate.y, targetCoordinate.x);
        	List<Node> nextpath = aStar.start(initialNode, finalNode);
        	if(nextpath.size() > 1) {
        		Node end = nextpath.get(nextpath.size()-1);
        		if(end.G < minCost) {
        			minCost = end.G;
        			minCostPath = nextpath;
        		}
        	}
		}
        
        for (Node node : minCostPath) {
//            System.out.println( "rows====" + node.getRow() + "   cols=====" + node.getCol());
        	//if(node.getRow()!=initialNode.getRow() && node.getCol() !=initialNode.getCol())
            this.addToPath(new Coordinates(node.coord.y, node.coord.x));
        }
        
        System.out.println("gonextroom()gonextroom()gonextroom()gonextroom()gonextroom()gonextroom()gonextroom()gonextroom()gonextroom()");
	}
}