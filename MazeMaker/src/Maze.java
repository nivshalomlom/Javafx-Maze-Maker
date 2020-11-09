import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

public class Maze implements Cloneable {

	private static final Random random = new Random();
	
	private boolean sizeChanged = false;
	
	private Integer[][] maze;
	private int height, width;
	private Vertex huntStartVertex, startPoint, endPoint;
	
	public Maze(int width, int height) {
		this.height = height;
		this.width = width;
		this.maze = new Integer[width][height];
		this.generateNewMaze();
	}
	
	public Vertex getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Vertex startPoint) {
		this.startPoint = startPoint;
	}

	public Vertex getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Vertex endPoint) {
		this.endPoint = endPoint;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		this.sizeChanged = true;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		this.sizeChanged = true;
	}

	/*
	 * A secondary constructor that receives a already generated maze
	 * the maze should be full with ones(1) to represent walls
	 * zeros(0) to represent paths
	 * a 2 to signify the start location
	 * a 3 to signify the end location
	 * */
	public Maze(Integer[][] maze) {
		this.height = maze[0].length;
		this.width = maze.length;
		this.maze = maze;
	}
	
	// Fills the maze with a given value
	private void fillMazeWith(int initValue) {
		if (this.sizeChanged)
			this.maze = new Integer[this.width][this.height];
		for (int i = 0; i < this.width; i++)
			for (int j = 0; j < this.height; j++)
				maze[i][j] = initValue;
	}
	
	// Gets every neighboring wall of a given cell
	private Collection<Vertex> getNeighboringWalls(Vertex vertex) {
		HashSet<Vertex> ret_val = new HashSet<Vertex>();
		if (vertex.getX() > 0)
			if (this.maze[vertex.getX() - 1][vertex.getY()] == 1)
				ret_val.add(new Vertex(vertex.getX() - 1, vertex.getY()));
		if (vertex.getY() > 0)
			if (this.maze[vertex.getX()][vertex.getY() - 1] == 1)
				ret_val.add(new Vertex(vertex.getX(), vertex.getY() - 1));
		if (vertex.getX() < this.width - 1)
			if (this.maze[vertex.getX() + 1][vertex.getY()] == 1)
				ret_val.add(new Vertex(vertex.getX() + 1, vertex.getY()));
		if (vertex.getY() < this.height - 1)
			if (this.maze[vertex.getX()][vertex.getY() + 1] == 1)
				ret_val.add(new Vertex(vertex.getX(), vertex.getY() + 1));
		return ret_val;
	}
	
	// Generates a random maze using prim's algorithm
	private void generateMazeUsingPrim() {
		// We fill the maze with walls
		this.fillMazeWith(1);
		// Then we choose a random point and declare that point a path
		LinkedList<Vertex> wallList = new LinkedList<Vertex>();
		int randomX = random.ints(0, this.width).findFirst().getAsInt();
		int randomY = random.ints(0, this.height).findFirst().getAsInt();
		this.startPoint = new Vertex(randomX, randomY);
		this.maze[randomX][randomY] = 0;
		// We add its walls to the wall list
		wallList.addAll(this.getNeighboringWalls(startPoint));
		// Then while the wall list is not empty we choose a random wall form a path from the point its blocking to it
		// Then we remove it form the list
		while (!wallList.isEmpty()) {
			int randIndex = random.ints(0, wallList.size()).findFirst().getAsInt();
			Vertex wallVertx = wallList.get(randIndex);
			if (wallVertx.getX() > 0 && wallVertx.getX() < this.width - 1) {
				// Handles the case of path wall wall (011)
				if (this.maze[wallVertx.getX() - 1][wallVertx.getY()] == 0 && this.maze[wallVertx.getX() + 1][wallVertx.getY()] == 1) {
					this.maze[wallVertx.getX() + 1][wallVertx.getY()] = 0;
					this.maze[wallVertx.getX()][wallVertx.getY()] = 0;
					wallList.remove(randIndex);
					wallList.addAll(this.getNeighboringWalls(new Vertex(wallVertx.getX() + 1, wallVertx.getY())));
					continue;
				}
				// Handles the case of wall wall path (110)
				else if (this.maze[wallVertx.getX() - 1][wallVertx.getY()] == 1 && this.maze[wallVertx.getX() + 1][wallVertx.getY()] == 0) {
					this.maze[wallVertx.getX() - 1][wallVertx.getY()] = 0;
					this.maze[wallVertx.getX()][wallVertx.getY()] = 0;
					wallList.remove(randIndex);
					wallList.addAll(this.getNeighboringWalls(new Vertex(wallVertx.getX() - 1, wallVertx.getY())));
					continue;
				}
			}
			if (wallVertx.getY() > 0 && wallVertx.getY() < this.height - 1) {
				// Handles the case of 
				// wall 1
				// wall 1
				// path 0
				if (this.maze[wallVertx.getX()][wallVertx.getY() + 1] == 1 && this.maze[wallVertx.getX()][wallVertx.getY() - 1] == 0) {
					this.maze[wallVertx.getX()][wallVertx.getY() + 1] = 0;
					this.maze[wallVertx.getX()][wallVertx.getY()] = 0;
					wallList.remove(randIndex);
					wallList.addAll(this.getNeighboringWalls(new Vertex(wallVertx.getX(), wallVertx.getY() + 1)));
					continue;
				}
				// Handles the case of
				// path 0
				// wall 1
				// wall 1
				else if (this.maze[wallVertx.getX()][wallVertx.getY() + 1] == 0 && this.maze[wallVertx.getX()][wallVertx.getY() - 1] == 1) {
					this.maze[wallVertx.getX()][wallVertx.getY() - 1] = 0;
					this.maze[wallVertx.getX()][wallVertx.getY()] = 0;
					wallList.remove(randIndex);
					wallList.addAll(this.getNeighboringWalls(new Vertex(wallVertx.getX(), wallVertx.getY() - 1)));
				}
			}
			wallList.remove(randIndex);
		}
		this.generateEndPoint();
		this.maze[this.startPoint.getX()][this.startPoint.getY()] = 2;
		this.maze[this.endPoint.getX()][this.endPoint.getY()] = 3;
	}
	
	// A method used in the DFS generation to get all nearby unvisited cells
	// Its smiler to getNeighboringWalls but gives you the cells after the walls
	private LinkedList<Vertex> getUnvisitedNeighbors(Vertex vertex) {
		LinkedList<Vertex> ret_val = new LinkedList<Vertex>();
		if (vertex.getX() > 1)
			if (this.maze[vertex.getX() - 2][vertex.getY()] == 1)
				ret_val.add(new Vertex(vertex.getX() - 2, vertex.getY()));
		if (vertex.getY() > 1)
			if (this.maze[vertex.getX()][vertex.getY() - 2] == 1)
				ret_val.add(new Vertex(vertex.getX(), vertex.getY() - 2));
		if (vertex.getX() < this.width - 2)
			if (this.maze[vertex.getX() + 2][vertex.getY()] == 1)
				ret_val.add(new Vertex(vertex.getX() + 2, vertex.getY()));
		if (vertex.getY() < this.height - 2)
			if (this.maze[vertex.getX()][vertex.getY() + 2] == 1)
				ret_val.add(new Vertex(vertex.getX(), vertex.getY() + 2));
		return ret_val;
	} 
	
	// Generates a maze using a DFS backtracker method
	private void generateByDFS() {
		// We fill the maze with walls
		this.fillMazeWith(1);
		// Then we pick a random start point
		int randomX = random.ints(0, this.width).findFirst().getAsInt();
		int randomY = random.ints(0, this.height).findFirst().getAsInt();
		this.startPoint = new Vertex(randomX, randomY);
		this.generateByDFS(startPoint);
		this.generateEndPoint();
		this.maze[this.startPoint.getX()][this.startPoint.getY()] = 2;
		this.maze[this.endPoint.getX()][this.endPoint.getY()] = 3;
	}
	
	private void generateByDFS(Vertex currentCell) {
		// We set the current cell to a path
		this.maze[currentCell.getX()][currentCell.getY()] = 0;
		// Then we go at a random order to each of its neighbors we didn't visit and call the method recursively on them
		LinkedList<Vertex> unvisitedCells = this.getUnvisitedNeighbors(currentCell);
		while (!unvisitedCells.isEmpty()) {
			int chosenCellIndex = random.nextInt(unvisitedCells.size());
			Vertex destCell = unvisitedCells.get(chosenCellIndex);
			if (this.maze[destCell.getX()][destCell.getY()] == 1) {
				if (destCell.getX() > currentCell.getX())
					this.maze[currentCell.getX() + 1][currentCell.getY()] = 0;
				else if (destCell.getY() > currentCell.getY())
					this.maze[currentCell.getX()][currentCell.getY() + 1] = 0;
				else if (destCell.getX() < currentCell.getX())
					this.maze[currentCell.getX() - 1][currentCell.getY()] = 0;
				else if (destCell.getY() < currentCell.getY())
					this.maze[currentCell.getX()][currentCell.getY() - 1] = 0;
				this.generateByDFS(destCell);
			}
			unvisitedCells.remove(chosenCellIndex);
		}
	}
	
	// A method to generate mazes using the hunt and kill algorithm
	private void generateByHuntAndKill() {
		// Filling the maze with walls and choosing a random start point
		this.fillMazeWith(1);
		int randomX = random.ints(0, this.width).findFirst().getAsInt();
		int randomY = random.ints(0, this.height).findFirst().getAsInt();
		Vertex startingLocation = new Vertex(randomX, randomY);
		// Checking from where cells will be placed so we could know from were to hunt
		while (randomX - 2 > 0) randomX -= 2;
		while (randomY - 2 > 0) randomY -= 2;
		huntStartVertex = new Vertex(randomX, randomY);
		this.startPoint = startingLocation;
		// Run the algorithm on the start point
		this.generateByHuntAndKill(startingLocation);
		this.generateEndPoint();
		this.maze[this.startPoint.getX()][this.startPoint.getY()] = 2;
		this.maze[this.endPoint.getX()][this.endPoint.getY()] = 3;
	}
	
	private void generateByHuntAndKill(Vertex startingLocation) {
		LinkedList<Vertex> unvisitedCells = this.getUnvisitedNeighbors(startingLocation);
		while (!unvisitedCells.isEmpty()) {
			// Do a random step by choosing a destination and carving a way there
			this.maze[startingLocation.getX()][startingLocation.getY()] = 0;
			int randCellIndex = random.nextInt(unvisitedCells.size());
			Vertex destCell = unvisitedCells.get(randCellIndex);
			this.maze[destCell.getX()][destCell.getY()] = 0;
			if (destCell.getX() > startingLocation.getX())
				this.maze[startingLocation.getX() + 1][startingLocation.getY()] = 0;
			else if (destCell.getY() > startingLocation.getY()) 
				this.maze[startingLocation.getX()][startingLocation.getY() + 1] = 0;
			else if (destCell.getX() < startingLocation.getX())
				this.maze[startingLocation.getX() - 1][startingLocation.getY()] = 0;
			else if (destCell.getY() < startingLocation.getY()) 
				this.maze[startingLocation.getX()][startingLocation.getY() - 1] = 0;
			unvisitedCells = this.getUnvisitedNeighbors(destCell);
			startingLocation = destCell;
		}
		Vertex huntVertex = this.hunt();
		if (huntVertex != null) 
			this.generateByHuntAndKill(huntVertex);
		return;
	}
	
	// The hunt algorithm, look for a unvisited cell across all cells that has a adjacent visited cell
	private Vertex hunt() {
		for (int i = this.huntStartVertex.getX(); i < this.width; i += 2)
			for (int j = this.huntStartVertex.getY(); j < this.height - 1; j += 2)
				if (this.maze[i][j] == 0) {
					if (i < this.width - 2)
						if (this.maze[i + 2][j] == 1) {
							this.maze[i + 2][j] = 0;
							this.maze[i + 1][j] = 0;
							return new Vertex(i + 2, j);
					}
					if (i > 1)
						if (this.maze[i - 2][j] == 1) {
							this.maze[i - 2][j] = 0;
							this.maze[i - 1][j] = 0;
							return new Vertex(i - 2, j);
					}
					if (j < this.height - 2)
						if (this.maze[i][j + 2] == 1) {
							this.maze[i][j + 2] = 0;
							this.maze[i][j + 1] = 0;
							return new Vertex(i, j + 2);
					}
					if (j > 1)
						if (this.maze[i][j - 2] == 1) {
							this.maze[i][j - 2] = 0;
							this.maze[i][j - 1] = 0;
							return new Vertex(i, j - 2);
					}
				}
		return null;
	}
	
	// A function to make maze creation even more random by randomly choosing how to create it
	public void generateNewMaze() {
		int randIndex = random.nextInt(3);
		if (randIndex == 0)
			this.generateByDFS();
		else if (randIndex == 1)
			this.generateMazeUsingPrim();
		else if (randIndex == 2)
			this.generateByHuntAndKill();
	}
	
	// A method to return a given vertex's path neighbors
	private LinkedList<Vertex> getNeighbors(Vertex vertex) {
		LinkedList<Vertex> ret_val = new LinkedList<Vertex>();
		if (vertex.getX() > 1)
			if (this.maze[vertex.getX() - 1][vertex.getY()] == 0)
				ret_val.add(new Vertex(vertex.getX() - 2, vertex.getY()));
		if (vertex.getY() > 1)
			if (this.maze[vertex.getX()][vertex.getY() - 1] == 0)
				ret_val.add(new Vertex(vertex.getX(), vertex.getY() - 2));
		if (vertex.getX() < this.width - 2)
			if (this.maze[vertex.getX() + 1][vertex.getY()] == 0)
				ret_val.add(new Vertex(vertex.getX() + 2, vertex.getY()));
		if (vertex.getY() < this.height - 2)
			if (this.maze[vertex.getX()][vertex.getY() + 1] == 0)
				ret_val.add(new Vertex(vertex.getX(), vertex.getY() + 2));
		return ret_val;
	} 
	
	// A method to compute a possible solution for the maze and return a string representation of it
	public String getSoultion() {
		// Find the a path to the maze's end
		LinkedList<Vertex> pathToSol = this.solMaze(this.startPoint, null);
		// Create a copy of the maze to show the solution on
		Integer[][] mazeCopy = new Integer[this.width][this.height];
		for (int i = 0; i < this.width; i++)
			mazeCopy[i] = this.maze[i].clone();
		for (Vertex pathPoint : pathToSol)
			if (!this.startPoint.equals(pathPoint) && !this.endPoint.equals(pathPoint))
				mazeCopy[pathPoint.getX()][pathPoint.getY()] = 4;
		return this.matrixToString(mazeCopy);
	}
	
	// A method to compute a possible solution for the maze and return a array representation of it
	public Integer[][] getSoultionAsArray() {
		// Find the a path to the maze's end
		LinkedList<Vertex> pathToSol = this.solMaze(this.startPoint, null);
		// Create a copy of the maze to show the solution on
		Integer[][] mazeCopy = new Integer[this.width][this.height];
		for (int i = 0; i < this.width; i++)
			mazeCopy[i] = this.maze[i].clone();
		for (Vertex pathPoint : pathToSol)
			if (!this.startPoint.equals(pathPoint) && !this.endPoint.equals(pathPoint))
				mazeCopy[pathPoint.getX()][pathPoint.getY()] = 4;
		return mazeCopy;
	}

	// A method to return a list of vertexes to show the maze's soultion
	public LinkedList<Vertex> getSoultionAsVertexList() {
		return this.solMaze(this.startPoint, null);
	}
	
	private LinkedList<Vertex> solMaze(Vertex startcell, Vertex prevCell) {
		// Assume the current node is a part of the path to the end
		LinkedList<Vertex> pathToSol = new LinkedList<Vertex>();
		pathToSol.add(startcell);
		LinkedList<Vertex> optionsForPath = this.getNeighbors(startcell);
		// Remove the cell we came from the options so we wont back track
		if (prevCell != null)
			optionsForPath.remove(prevCell);
		// Try all possible directions until one of them get to the end
		// Only when we reached the end we send up the path else we dont save the path
		for (Vertex option : optionsForPath) {
			LinkedList<Vertex> optionPath = this.solMaze(option, startcell);
			if (optionPath.getLast().equals(this.endPoint)) {
				pathToSol.addAll(optionPath);
				break;
			}
		}
		return pathToSol;
	}
	
	private String matrixToString(Integer[][] matrix) {
		StringBuilder textMaze = new StringBuilder();
		for (int i = 0; i < matrix[0].length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				if (matrix[j][i] == null)
					textMaze.append("N ");
				else if (matrix[j][i] == 0)
					textMaze.append("  ");
				else if (matrix[j][i] == 1)
					textMaze.append("O ");
				else if (matrix[j][i] == 2)
					textMaze.append("S ");
				else if (matrix[j][i] == 3)
					textMaze.append("E ");
				else if (matrix[j][i] == 4)
					textMaze.append("X ");
			}
			textMaze.append("\n");
		}
		return textMaze.toString();
	}
	
	// A method to get to the maximal valid y coordinate possible in the given maze
	private void maximizeY(Vertex vertex) {
		while (vertex.getY() <= this.height - 3) {
			if (this.maze[vertex.getX()][vertex.getY() + 2] == 1)
				break;
			vertex.setY(vertex.getY() + 2); 
		}
	}
	
	// A method to get to the maximal valid X coordinate possible in the given maze
	private void maximizeX(Vertex vertex) {
		while (vertex.getX() <= this.width - 3) {
			if (this.maze[vertex.getX() + 2][vertex.getY()] == 1)
				break;
			vertex.setX(vertex.getX() + 2); 
		}
	}
	
	// A method to get to the minimal valid y coordinate possible in the given maze
	private void minimizeY(Vertex vertex) {
		while (vertex.getY() >= 2) {
			if (this.maze[vertex.getX()][vertex.getY() - 2] == 1)
				break;
			vertex.setY(vertex.getY() - 2); 
		}
	}
	
	// A method to get to the minimal valid x coordinate possible in the given maze
	private void minimizeX(Vertex vertex) {
		while (vertex.getX() >= 2) {
			if (this.maze[vertex.getX() - 2][vertex.getY()] == 1)
				break;
			vertex.setX(vertex.getX() - 2); 
		}
	}
	
	// A method for placing the end point of the maze in the opposite quadrant of the maze from the start point
	private void generateEndPoint() {
		this.endPoint = startPoint.clone();
		if (this.startPoint.getX() < this.width/2 && this.startPoint.getY() < this.height/2) {
			this.maximizeX(this.endPoint);
			this.maximizeY(this.endPoint);
		}
		else if (this.startPoint.getX() >= this.width/2 && this.startPoint.getY() >= this.height/2) {
			this.minimizeX(this.endPoint);
			this.minimizeY(this.endPoint);
		}
		else if (this.startPoint.getX() < this.width/2 && this.startPoint.getY() >= this.height/2) {
			this.maximizeX(this.endPoint);
			this.minimizeY(this.endPoint);
		}
		else if (this.startPoint.getX() >= this.width/2 && this.startPoint.getY() < this.height/2) {
			this.minimizeX(this.endPoint);
			this.maximizeY(this.endPoint);
		}
	}
	/*
	 * Return a 2 dimensional array repressing the maze 
	 * 0 - stands for a path
	 * 1 - stands for a wall
	 * 2 - stands for the start point
	 * 3 - stands for the end point
	 * */
	public Integer[][] toArray() {
		return this.maze;
	}
	
	@Override
	public Maze clone() {
		return new Maze(this.maze);
	}
	
	@Override
	public String toString() {
		return this.matrixToString(this.maze);
	}

}
