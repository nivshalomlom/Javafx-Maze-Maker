import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class GUI extends Application {

	private MazePanel mazePanel;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		mazePanel = new MazePanel();
		mazePanel.drawMaze(20, 20);
		Scene scene = new Scene(mazePanel);
		scene.setOnKeyPressed(KeyPressed -> {
			if (!this.mazePanel.finishedMaze) {
				KeyCode action = KeyPressed.getCode();
				switch (action) {
				case UP:
					this.mazePanel.moveUserUp();
					break;

				case DOWN:
					this.mazePanel.moveUserDown();
					break;

				case LEFT:
					this.mazePanel.moveUserLeft();
					break;

				case RIGHT:
					this.mazePanel.moveUserRight();
					break;

				default:
					break;
				}
			}
		});
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

}
