package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Snake extends Application {
	// variable
	static int speed = 5;
	static int foodcolor = 0;
	static int width = 20;
	static int height = 20;
	static int foodX = 0;
	static int foodY = 0;
	static int cornersize = 25;
	static List<Corner> snake = new ArrayList<>();
	static Dir direction = Dir.left;
	static boolean gameOver = false;
	static Random rand = new Random();

	public enum Dir {
		left, right, up, down
	}

	public static class Corner {
		int x;
		int y;

		public Corner(int x, int y) {
			this.x = x;
			this.y = y;
		}

	}

	public void start(Stage primaryStage) {
		try {
			newFood();

			VBox root = new VBox();
			Canvas c = new Canvas(width * cornersize, height * cornersize);
			GraphicsContext gc = c.getGraphicsContext2D();
			root.getChildren().add(c);

			new AnimationTimer() {
				long lastTick = 0;

				public void handle(long now) {
					if (lastTick == 0) {
						lastTick = now;
						tick(gc);
						return;
					}

					if (now - lastTick > 1000000000 / speed) {
						lastTick = now;
						tick(gc);
					}
				}

			}.start();

			Scene scene = new Scene(root, width * cornersize, height * cornersize);

			// control
			scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
			    KeyCode keyCode = key.getCode();
			    if (keyCode == KeyCode.Z || keyCode == KeyCode.UP) {
			        direction = Dir.up;
			    } else if (keyCode == KeyCode.Q || keyCode == KeyCode.LEFT) {
			        direction = Dir.left;
			    } else if (keyCode == KeyCode.S || keyCode == KeyCode.DOWN) {
			        direction = Dir.down;
			    } else if (keyCode == KeyCode.D || keyCode == KeyCode.RIGHT) {
			        direction = Dir.right;
			    }
			});

			// add start snake parts
			snake.add(new Corner(width / 2, height / 2));
			snake.add(new Corner(width / 2, height / 2));
			snake.add(new Corner(width / 2, height / 2));
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("SNAKE GAME");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void tick(GraphicsContext gc) {
	    if (gameOver) {
	        gc.setFill(Color.RED);
	        gc.setFont(new Font("", 50));
	        gc.fillText("GAME OVER", 100, 250);
	        return;
	    }

	    for (int i = snake.size() - 1; i >= 1; i--) {
	        snake.get(i).x = snake.get(i - 1).x;
	        snake.get(i).y = snake.get(i - 1).y;
	    }

	    switch (direction) {
	    case up:
	        snake.get(0).y--;
	        if (snake.get(0).y < 0) {
	            gameOver = true;
	        }
	        break;
	    case down:
	        snake.get(0).y++;
	        if (snake.get(0).y > height) {
	            gameOver = true;
	        }
	        break;
	    case left:
	        snake.get(0).x--;
	        if (snake.get(0).x < 0) {
	            gameOver = true;
	        }
	        break;
	    case right:
	        snake.get(0).x++;
	        if (snake.get(0).x > width) {
	            gameOver = true;
	        }
	        break;

	    }

	    // draw screen edges
	    gc.setFill(Color.PALETURQUOISE);
	    gc.fillRect(0, 0, cornersize, height * cornersize);
	    gc.fillRect(0, 0, width * cornersize, cornersize);
	    gc.fillRect(width * cornersize - cornersize, 0, cornersize, height * cornersize);
	    gc.fillRect(0, height * cornersize - cornersize, width * cornersize, cornersize);

	    // eat food
	    if (foodX == snake.get(0).x && foodY == snake.get(0).y) {
	        snake.add(new Corner(-1, -1));
	        newFood();
	    }

	    // self destroy
	    for (int i = 1; i < snake.size(); i++) {
	        //if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y && snake.get(0).x != snake.get(1).x && snake.get(0).y != snake.get(1).y) {
	        if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
	            gameOver = true;
	        }
	    }
	    
	    // score
	    gc.setFill(Color.CORAL);
	    gc.setFont(Font.font("", FontWeight.BOLD, 20));
	    int score=(snake.size() - 3);
	    gc.fillText("Score: " + score, 5, 20);
	    Text scoreText = new Text("Score: " + (snake.size() - 3));
	    scoreText.setId("score");

	    // background
	    if (score < 5) {
	        gc.setFill(Color.BLACK);
	        gc.fillRect(cornersize, cornersize, width * cornersize - 2 * cornersize, height * cornersize - 2 * cornersize);
	    } else {
	        if (score % 10 >= 5) {
	            gc.setFill(Color.WHITE);
	        } else {
	            gc.setFill(Color.BLACK);
	        }
	        gc.fillRect(cornersize, cornersize, width * cornersize - 2 * cornersize, height * cornersize - 2 * cornersize);
	    }
		// random food color
		Color Cl = Color.BROWN;

		switch (foodcolor) {
		case 0:
			Cl = Color.PURPLE;
			break;
		case 1:
			Cl = Color.DEEPSKYBLUE;
			break;
		case 2:
			Cl = Color.YELLOW;
			break;
		case 3:
			Cl = Color.PINK;
			break;
		case 4:
			Cl = Color.ORANGE;
			break;
		}
		gc.setFill(Cl);
		gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);

		// snake
		if (score < 5) {
			for (int i = 0; i < snake.size(); i++) {
			    Corner c = snake.get(i);
			    if (i == 0) {
			        // head of the snake
			        gc.setFill(Color.RED);
			    } else {
			        // body of the snake
			        gc.setFill(Color.WHITE);
			    }
			    gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
			}
		} else {
				if (score % 10 >= 5) {
					for (int i = 0; i < snake.size(); i++) {
					    Corner c = snake.get(i);
					    if (i == 0) {
					        // head of the snake
					        gc.setFill(Color.RED);
					    } else {
					        // body of the snake
					        gc.setFill(Color.BLACK);
					    }
					    gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
					}
				} else {
					for (int i = 0; i < snake.size(); i++) {
					    Corner c = snake.get(i);
					    if (i == 0) {
					        // head of the snake
					        gc.setFill(Color.RED);
					    } else {
					        // body of the snake
					        gc.setFill(Color.WHITE);
					    }
					    gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
					}
				}
	
		}
	}

	// food
	public static void newFood() {
		start: while (true) {
			foodX = rand.nextInt(width);
			foodY = rand.nextInt(height);

			for (Corner c : snake) {
				if (c.x == foodX && c.y == foodY) {
					continue start;
				}
			}
			foodcolor = rand.nextInt(5);
			speed++;
			break;

		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}