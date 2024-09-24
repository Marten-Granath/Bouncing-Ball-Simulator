package bouncing_balls;
import java.awt.*;
import java.util.HashSet;
import java.util.Random;

import static java.lang.Math.*;
/**
 * The physics model.
 * 
 * This class is where you should implement your bouncing balls model.
 * 
 * The code has intentionally been kept as simple as possible, but if you wish, you can improve the design.
 * 
 * @author Simon Robillard
 *
 */
class Model {

	double areaWidth, areaHeight;
	double gravity = -9.81;

	Ball [] balls;
	Random random = new Random();

	private Color[] neonColors = {
			new Color(57, 255, 20),   // Neon green
			new Color(255, 20, 147),  // Neon pink
			new Color(77, 77, 255),   // Neon blue
			new Color(255, 165, 0),   // Neon orange
			new Color(0, 255, 255),   // Neon cyan
			new Color(255, 255, 0),   // Neon yellow
			new Color(191, 0, 255),   // Neon purple
			new Color(255, 0, 0),     // Neon red
			new Color(255, 0, 255),   // Neon magenta
			new Color(191, 255, 0),   // Neon lime
			new Color(125, 249, 255), // Electric blue
			new Color(255, 69, 0)     // Neon orange-red
	};
	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;
		
		// Initialize the model with a few balls
		balls = new Ball[2];

		balls[0] = new Ball(0.5, 2, 2, 0, 0.2, getRandomColor(null));
		balls[1] = new Ball(3.5, 2, -2, 0, 0.2, getRandomColor(null));

	}

	private Color getRandomColor(Color currentColor){
		Color newColor;
		do {
			newColor = neonColors[random.nextInt(neonColors.length)];
		} while (newColor.equals(currentColor)); // Ensure new color is different from the current one
		return newColor;
	}

	void step(double deltaT) {

		// TODO this method implements one step of simulation with a step deltaT
		for (Ball b : balls) {
			boolean collisionOccured = false;

			// detect collision between balls

			for (Ball o : balls) {
				if (b != o) { // Cannot collide with itself
					//System.out.println(b);
					//System.out.println(o);
					if (o.x + o.radius > b.x - b.radius && o.x - o.radius < b.x + b.radius && o.vx/b.vx < 0) { // Collision with other ball
						if(o.x + o.radius >= b.x - b.radius){
							b.x = o.x + o.radius + b.radius;
						}
						else{
							b.x = o.x - o.radius - b.radius;
						}
						o.vx = -o.vx;
						b.vx = -b.vx;
						collisionOccured = true;
						System.out.println("Collision");
					}

				}
				// Do something
			}

			// detect collision with the border
			if (b.x < b.radius || b.x > areaWidth - b.radius) {
				b.vx *= -1; // change direction of ball
				collisionOccured = true;
			}

			if (b.y < b.radius || b.y > areaHeight - b.radius) {
				if (b.y < b.radius) { //won't get stuck under border
					b.y = b.radius;
				}
				if (b.y > areaHeight - b.radius) { //won't get stuck above border
					b.y = areaHeight - b.radius;
				}
					b.vy *= -1;
					collisionOccured = true;
			};

			if (collisionOccured){
				b.color = getRandomColor(null);
			}

			// compute new position according to the speed of the ball
			b.x += deltaT * b.vx;
			//b.vy += deltaT * gravity; // Take gravity into account
			b.y += deltaT * (b.vy);
		}
	}

	/**
	 * Simple inner class describing balls.
	 */
	class Ball {
		double density = 2;
		Color color;
		Ball(double x, double y, double vx, double vy, double r, Color color) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.radius = r;
			this.color = color;
			this.mass = density* Math.pow(PI,2);
		}

		/**
		 * Position, speed, radius and mass of the ball.
		 */
		double x, y, vx, vy, radius, mass;
	}
}
