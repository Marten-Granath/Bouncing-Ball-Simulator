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

		balls[0] = new Ball(2.5, 2,  1, 0, 0.4, getRandomColor(null));
		balls[1] = new Ball(3.5, 2, -0.5, 0, 0.2, getRandomColor(null));

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
					if (o.x + o.radius > b.x - b.radius && o.x - o.radius < b.x + b.radius) { // Collision with other ball
						System.out.println("Collision");
						// move balls to each others edge if overlap occur
						if(b.x<o.x){
							b.x = o.x - o.radius - b.radius;
						}
						else if(b.x>o.x){
							b.x = o.x + o.radius + b.radius;
						}
						// apply new velocity
						b.vx = calculateNewVelocity(b, o)[0];
						o.vx = calculateNewVelocity(b, o)[1];
						System.out.println(b.vx);
						System.out.println(o.vx);

						/*
						if (b.kx-o.kx < 0){
							//b.vx = -sqrt(abs(2 * (b.kx - o.kx)) / b.mass);
						}
						else if (b.kx-o.kx >= 0) {
							//b.vx = sqrt(abs(2 * (b.kx - o.kx)) / b.mass);
						}
						if (o.kx-b.kx < 0){
							//o.vx = -sqrt(abs(2*(o.kx-b.kx))/o.mass);
						}
						else if (o.kx-b.kx >= 0) {
							//o.vx = sqrt(abs(2*(o.kx-b.kx))/o.mass);
						}

						System.out.println(b.vx);
						System.out.println(o.vx);
						System.out.println(o.vx);
						double tempEtot = (b.mass *pow(b.vx,2)+o.mass *pow(o.vx,2))/2;


						double bm = b.mx;
						double om = o.mx;
						b.mx = om;
						o.mx = bm;
						o.kx = 0.5 * o.mass * Math.pow(o.vx, 2); // Kinetic energy along x-axis
						b.kx = 0.5 * b.mass * Math.pow(b.vx, 2); // Kinetic energy along x-axis
						System.out.println(tempEtot == (b.mass *pow(b.vx,2)+o.mass *pow(o.vx,2))/2);
						*/

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
				b.vx *= -1; // change direction of ball
				b.mx *= -1;
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
					b.my *= -1;
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
		double newVelocityB1;
		double newVelocityB2;
		newVelocityB1 = (b1.mass*b2.vx + b2.mass*b2.vx + b2.mass*b1.vx - b1.mass*b1.vx)/(2*b2.mass);
		newVelocityB2 = b2.vx-newVelocityB1+b1.vx;
		//vxNewB = -(o.mass * o.vx - b.mass * b.vx - b.mass * o.vx + b.mass * b.vx)/b.mass;
		//vxNewO = o.vx-(vxNewB - b.vx);
		double[] newList = {newVelocityB1, newVelocityB2};
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
			this.mx = mass * vx; // Momentum along x-axis
			this.my = mass * vy; // Momentum along y-axis
			this.kx = 0.5 * mass * Math.pow(vx, 2); // Kinetic energy along x-axis
			this.ky = 0.5 * mass * Math.pow(vy, 2); // Kinetic energy along y-axis
		}

		/**
		 * Position, speed, radius and mass of the ball.
		 */
		double x, y, vx, vy, radius, mass, mx, my, kx, ky;
	}
}
