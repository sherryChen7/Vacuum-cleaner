import Agents.Manor;
import Agents.Robot;
import GUI.GridFrame;

public class GUILauncher {

	public static void main(String[] args) throws InterruptedException {
		// Creation of the Manor
		Manor manor = new Manor(30,37);
		// Creation of a new robot
		Robot robot = new Robot(manor);
		manor.setRobotPosition(20, 26);//題目原點(客廳)
		//manor.setRobotPosition(12, 34);
		//manor.setRobotPosition(20, 21); //洗衣間
		//manor.setRobotPosition(20, 12); //臥室2
		//manor.setRobotPosition(16, 9); //臥室1
		//manor.setRobotPosition(10, 10); //鋼琴房
		manor.setChargePosition(22, 26);
		manor.setBlock();
		manor.setKeypoint();
		// Creates the GUI, pass it to the environment
		GridFrame gui = new GridFrame(manor.getRooms());
		manor.setGui(gui);
		// Launches the environment in a new thread
		//Thread envThread = new Thread(manor);
		//envThread.start();
		// Launches the robot in a new thread
		Thread robotThread = new Thread(robot);
		robotThread.start();
	}

}