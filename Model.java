package bouncing_balls;
import java.awt.*;
import java.util.ArrayList;
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

	//Array of neon colors
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
	
	// Returns a random color from the neonColors array that is different from the current color
	private Color getRandomColor(Color currentColor){
		Color newColor;
		do {
			newColor = neonColors[random.nextInt(neonColors.length)];
		} while (newColor.equals(currentColor)); // Ensure new color is different from the current one
		return newColor;
	}

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;
		
		// Initialize the model with a few balls
		balls = new Ball[2];

		balls[0] = new Ball(3, 2,  0.5, -0.5, 0.4, getRandomColor(null));
		balls[1] = new Ball(10, 0.5, -0.5, 0.5, 0.2, getRandomColor(null));

	}

	// Add this flag to your Model class
	boolean showBackgroundImage = false;
	int frameCounter = 0; // To control how many frames to show the image

	void step(double deltaT) {

		for (Ball b : balls) {

			// detect collision between balls
			Boolean collisionOccured = false;
			for (Ball o : balls) {
				if (b != o) { // Cannot collide with itself
					if (o.x + o.radius > b.x - b.radius && o.x - o.radius < b.x + b.radius && o.y + o.radius > b.y - b.radius && o.y - o.radius < b.y + b.radius) { // Collision along x-axis & y-axis
						// handle overlap
						if(b.x<o.x){
							b.x = o.x - o.radius - b.radius;
						}
						else if(b.x>o.x){
							b.x = o.x + o.radius + b.radius;
						}
						else if(b.y<o.y){
							b.y = o.y - o.radius - b.radius;
						}
						else if(b.y>o.y){
							b.y = o.y + o.radius + b.radius;
						}
						// apply new velocity
						b.vx = calculateNewVelocity(b, o)[0];
						b.vy = calculateNewVelocity(b, o)[1];
						o.vx = calculateNewVelocity(b, o)[2];
						o.vy = calculateNewVelocity(b, o)[3];

						collisionOccured = true;
						showBackgroundImage = true;
						frameCounter = 2;
					}
				}

				// Do something
				if (frameCounter > 0) {
					frameCounter--;
				} else {
					showBackgroundImage = false; // Reset the flag after specified frames
				}
			}

			// detect collision with the border
			if (b.x < b.radius || b.x > areaWidth - b.radius) {
				// unstick from border left
				if (b.x < b.radius) {
					b.x = b.radius;
				}
				// unstick from border right
				if (b.x > areaWidth - b.radius) {
					b.x = areaWidth - b.radius;
				}
				b.vx *= -1; 
				collisionOccured = true;
			}
			if (b.y < b.radius || b.y > areaHeight - b.radius) {
				// unstick from border below
				if (b.y < b.radius) { 
					b.y = b.radius;
				}
				// unstick from border above
				if (b.y > areaHeight - b.radius) { 
					b.y = areaHeight - b.radius;
				}
					b.vy *= -1;
					collisionOccured = true;
			};
			
			// change color of the ball if collision occurred
			if (collisionOccured){
				b.color = getRandomColor(null);
			}

			// compute new position according to the speed of the ball
			b.x += deltaT * b.vx;
			//b.vy += deltaT * gravity; // Take gravity into account
			b.y += deltaT * (b.vy);
		}
	}

	double[] calculateNewVelocity (Ball b1, Ball b2) {
		double newXVelocityB1;
		double newYVelocityB1;
		double newXVelocityB2;
		double newYVelocityB2;
		newXVelocityB1 = (b1.mass*b1.vx+b2.mass*b2.vx-b2.mass*b1.vx+b2.mass*b2.vx) /(b1.mass+b2.mass);
		newXVelocityB2 = b1.vx-b2.vx+newXVelocityB1;
		newYVelocityB1 = (b1.mass*b1.vy+b2.mass*b2.vy-b2.mass*b1.vy+b2.mass*b2.vy) /(b1.mass+b2.mass);
		newYVelocityB2 = b1.vy-b2.vy+newYVelocityB1;
		double[] newList = {newXVelocityB1, newYVelocityB1, newXVelocityB2, newYVelocityB2};
		return newList;
	}

	/**
	 * Simple inner class describing balls.
	 */
	class Ball {
		double density = 10;
		Color color;
		Ball(double x, double y, double vx, double vy, double r, Color color) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.radius = r;
			this.color = color;
			this.mass = density * PI * Math.pow(r, 2);
		}

		/**
		 * Position, speed, radius and mass of the ball.
		 */
		double x, y, vx, vy, radius, mass;
	}
}
