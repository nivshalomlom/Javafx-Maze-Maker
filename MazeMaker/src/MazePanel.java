import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;

public class MazePanel extends VBox {

	// Constants
	private static final int CANVAS_WIDTH = 600, CANVAS_HEIGHT = 600, MAZE_DEFAULT_WIDTH = 20, MAZE_DEFAULT_HEIGHT = 20;
	private static final Insets UNIVERSAL_MARGIN = new Insets(10, 50, 10, 50);
	private static final Font UNIVERSAL_FONT = new Font(15);
	
	// Flags
	protected boolean finishedMaze;
	
	// Data variables
	private Maze maze;
	private Integer[][] mazeArray;
	private Vertex currentUserLocation;
	private int mazeWidth, mazeHeight;
	
	// Graphical variables
	private Label statusMessage;
	private Canvas mazeGUI;
	private GraphicsContext gc;
	
	private HBox dropBoxFrame;
	private HBox widthHBox, heightHBox;
	private Label wLabel, hLabel;
	private ChoiceBox<Integer> widthBox, heightBox;
	
	// Button pane
	private HBox buttonFrame;
	private Button getNewMaze, getMazeSoultion;
	
	public MazePanel() {
		this.setAlignment(Pos.CENTER);
		// Making the canvas and the maze
		this.mazeGUI = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
		this.maze = new Maze(MAZE_DEFAULT_WIDTH, MAZE_DEFAULT_HEIGHT);
		this.mazeHeight = MAZE_DEFAULT_HEIGHT;
		this.mazeWidth = MAZE_DEFAULT_WIDTH;
		// Creating the graphical context and a empty canvas
		this.gc = mazeGUI.getGraphicsContext2D();
		this.gc.setFill(Color.LIGHTGRAY);
		this.gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		// Setting a default user position so we can know if a maze has been made
		this.currentUserLocation = new Vertex(-1, -1);
		// Setting a status message to display the current status of the game
		this.statusMessage = new Label("Solve this maze!:");
		this.statusMessage.setPrefSize(160, 20);
		this.statusMessage.setFont(new Font(20));
		// Making the buttons
		// Making the button for getting a new maze
		this.getNewMaze = new Button("Generate new maze");
		this.getNewMaze.setPrefSize(250, 30);
		this.getNewMaze.setFont(UNIVERSAL_FONT);
		this.getNewMaze.setOnAction(Action -> {
			this.gc.setFill(Color.LIGHTGRAY);
			this.gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
			this.drawMaze(this.widthBox.getValue(), this.heightBox.getValue());
			this.statusMessage.setText("Solve this maze!:");
		});
		// Making the button for getting the soultion for the maze
		this.getMazeSoultion = new Button("Show maze soultion");
		this.getMazeSoultion.setFont(UNIVERSAL_FONT);
		this.getMazeSoultion.setPrefSize(250, 30);
		this.getMazeSoultion.setOnAction(Action -> {
			if (!this.currentUserLocation.equals(new Vertex(-1, -1))) {
				this.finishedMaze = true;
				this.moveUser(this.currentUserLocation, this.maze.getStartPoint());
				gc.setFill(Color.YELLOW);
				gc.fillRect(this.maze.getEndPoint().getX(), this.maze.getEndPoint().getY(), 1, 1);
				this.gc.setFill(Color.ORANGE);
				for (Vertex vertex : this.maze.getSoultionAsVertexList()) 
					if (!vertex.equals(this.maze.getStartPoint()) && !vertex.equals(this.maze.getEndPoint()))
						this.gc.fillRect(vertex.getX(), vertex.getY(), 1, 1);
				this.statusMessage.setText("The soultion:");
			}
		});
		this.buttonFrame = new HBox(this.getNewMaze, this.getMazeSoultion);
		this.buttonFrame.setPrefSize(600, 30);
		HBox.setMargin(this.getNewMaze, UNIVERSAL_MARGIN);
		HBox.setMargin(this.getMazeSoultion, UNIVERSAL_MARGIN);
		// Building the combo box
		ObservableList<Integer> options = 
			    FXCollections.observableArrayList(
			       20,
			       30,
			       40,
			       50,
			       60,
			       70,
			       80,
			       90,
			       100
			    );
		// Building the width box
		this.widthBox = new ChoiceBox<Integer>(options);
		this.widthBox.getSelectionModel().selectFirst();
		this.wLabel = new Label("Select width: ");
		this.wLabel.setFont(UNIVERSAL_FONT);
		this.widthHBox = new HBox(this.wLabel, this.widthBox);
		HBox.setMargin(this.widthHBox, UNIVERSAL_MARGIN);
		// Building the height box
		this.heightBox = new ChoiceBox<Integer>(options);
		this.heightBox.getSelectionModel().selectFirst();
		this.hLabel = new Label("Select height: ");
		this.hLabel.setFont(UNIVERSAL_FONT);
		this.heightHBox = new HBox(this.hLabel, this.heightBox);
		HBox.setMargin(this.heightHBox, UNIVERSAL_MARGIN);
		// Building the combo box frame
		this.dropBoxFrame = new HBox(this.widthHBox, this.heightHBox);
		// Setting the main VBox frame
		this.getChildren().addAll(this.statusMessage, this.mazeGUI, this.dropBoxFrame, this.buttonFrame);
		// Resetting the flags
		this.finishedMaze = false;
	}
	
	public void drawMaze(int mazeWidth, int mazeHeight) {
		int widthFactor = 1, heightFactor = 1;
		// Resetting the flags
		this.finishedMaze = false;
		// Setting up the maze
		if (this.mazeWidth != mazeWidth) {
			this.maze.setWidth(mazeWidth);
			this.mazeWidth = mazeWidth;
		}
		if (this.mazeHeight != mazeHeight) {
			this.maze.setHeight(mazeHeight);
			this.mazeHeight = mazeHeight;
		}
		// Rescale the affine by a factor to account rectangle mazes
		if (mazeWidth > mazeHeight)
			heightFactor = mazeWidth / mazeHeight;
		if (mazeHeight > mazeWidth)
			widthFactor = mazeHeight / mazeWidth;
		this.maze.generateNewMaze();
		this.currentUserLocation = this.maze.getStartPoint();
		this.mazeWidth = mazeWidth;
		this.mazeHeight = mazeHeight;
		// Setting the canvas and drawing the maze
		Affine scaleTransform = new Affine();
		scaleTransform.appendScale(this.mazeGUI.getWidth() / (mazeWidth * widthFactor), this.mazeGUI.getHeight() / (mazeHeight * heightFactor));
		gc.setTransform(scaleTransform);
		// Draw the maze on the canvas
		this.mazeArray = this.maze.toArray();
		for (int i = 0; i < mazeWidth; i++)
			for (int j = 0; j < mazeHeight; j++) {
				if (mazeArray[i][j] == 0) 
					gc.setFill(Color.LIGHTGRAY);
				else if (mazeArray[i][j] == 1) 
					gc.setFill(Color.BLACK);
				else if (mazeArray[i][j] == 2) 
					gc.setFill(Color.RED);
				else if (mazeArray[i][j] == 3) 
					gc.setFill(Color.YELLOW);
				gc.fillRect(i, j, 1, 1);
			}
	}
	
	public void moveUserLeft() {
		if (this.currentUserLocation.getX() > 1)
			if (this.mazeArray[this.currentUserLocation.getX() - 1][this.currentUserLocation.getY()] == 0)
				this.moveUser(this.currentUserLocation, new Vertex(this.currentUserLocation.getX() - 2, this.currentUserLocation.getY())); 
	}
	
	public void moveUserRight() {
		if (this.currentUserLocation.getX() < this.mazeWidth - 2 && this.currentUserLocation.getX() != -1)
			if (this.mazeArray[this.currentUserLocation.getX() + 1][this.currentUserLocation.getY()] == 0)
				this.moveUser(this.currentUserLocation, new Vertex(this.currentUserLocation.getX() + 2, this.currentUserLocation.getY()));
	}
	
	public void moveUserDown() {
		if (this.currentUserLocation.getY() < this.mazeHeight - 2 && this.currentUserLocation.getY() != -1)
			if (this.mazeArray[this.currentUserLocation.getX()][this.currentUserLocation.getY() + 1] == 0)
				this.moveUser(this.currentUserLocation, new Vertex(this.currentUserLocation.getX(), this.currentUserLocation.getY() + 2));
	}
	
	public void  moveUserUp() {
		if (this.currentUserLocation.getY() > 1)
			if (this.mazeArray[this.currentUserLocation.getX()][this.currentUserLocation.getY() - 1] == 0)
				this.moveUser(this.currentUserLocation, new Vertex(this.currentUserLocation.getX(), this.currentUserLocation.getY() - 2));
	}
	
	private void moveUser(Vertex currentPos, Vertex nextPos) {
		this.gc.setFill(Color.LIGHTGRAY);
		this.gc.fillRect(currentPos.getX(), currentPos.getY(), 1, 1);
		this.gc.setFill(Color.RED);
		this.gc.fillRect(nextPos.getX(), nextPos.getY(), 1, 1);
		this.currentUserLocation = nextPos;
		if (this.currentUserLocation.equals(this.maze.getEndPoint())) {
			this.finishedMaze = true;
			this.statusMessage.setText("You Win!");
		}
	}
	
}
