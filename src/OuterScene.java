import javafx.scene.Parent;
import javafx.scene.Scene;

public class OuterScene extends Scene {
	private static int WINDOW_WIDTH = 1000;
	private static int WINDOW_HEIGHT = 800;
	private static int X_LOCATION = 200;
	private static int Y_LOCATION =25;
	
	public OuterScene(Parent root) {
		super(root, WINDOW_WIDTH, WINDOW_HEIGHT);
	}	
}
