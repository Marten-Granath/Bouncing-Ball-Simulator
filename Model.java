package bouncing_balls;

import java.awt.*;
import java.util.Random;
import static java.lang.Math.*;

/*
 * Class to simulate the fully elastic collision between two bouncing balls
 */
class Model {

    double areaWidth, areaHeight;
	double gravity;
    Ball[] balls;

	// Sets the colour of the balls
	ColourSetter colourSetter = new ColourSetter();

    Model(double width, double height) {
        areaWidth = width;
        areaHeight = height;
		gravity = -9.82;

        // Initialize the model with a few balls
        balls = new Ball[2];

        balls[0] = new Ball(3.5, 3, 2, -2, 0.25, colourSetter.getRandomColor(null));
        balls[1] = new Ball(1, 2, 3, 0.5, 0.2, colourSetter.getRandomColor(null));
    }

	/*
	 * Repeatedly simulates the movement of the balls using Euler's Method
	 */
    void step(double deltaT) {
        // Handle ball collisions
		for (Ball b1 : balls) {
            for (Ball b2 : balls) {
                if (b1 != b2) {
					// Preliminary calculations
					// * We use Pythagoras Theorem to compute distance
                    double dx = b2.x - b1.x;
                    double dy = b2.y - b1.y;
                    double distance = sqrt(pow(dx,2) + pow(dy,2));

					// Detect collision between balls
                    if (distance < b1.radius + b2.radius) {
                        double overlap = (b1.radius + b2.radius - distance) / 2;
						Vector normalVector = new Vector(dx/distance, dy/distance);
						Vector tangentialVector = new Vector(-normalVector.y, normalVector.x);
						
						// Adjust for overlap after collision
						overlapAdjustment(b1, b2, normalVector, overlap);

						// Calculate & apply new velocities
						applyVelocity(b1, b2, calculateVelocity(b1, b2, normalVector, tangentialVector));

						// Set new colours
                        colourSetter.setColour(b1);
						colourSetter.setColour(b2);
                    }
                }
            }

            // Detect collision with walls
            if (b1.x < b1.radius || b1.x > areaWidth - b1.radius) {
                // Set new velocity
				b1.vx *= -1;

				// Ball cannot move past horizontal borders
				if (b1.x < b1.radius) b1.x = b1.radius;
				if (b1.x > areaWidth - b1.radius) b1.x = areaHeight - b1.radius;

				// Set new colour
                colourSetter.setColour(b1);
            }
            if (b1.y < b1.radius || b1.y > areaHeight - b1.radius) {
                // Set new velocity
				b1.vy *= -1;
                
				// Ball cannot move past vertical borders
                if (b1.y < b1.radius) b1.y = b1.radius;
                if (b1.y > areaHeight - b1.radius) b1.y = areaHeight - b1.radius;
                
				// Set new colour
				colourSetter.setColour(b1);
            }

            // Apply gravity
            b1.vy += deltaT * gravity;

            // Update position
            b1.x += deltaT * b1.vx;
            b1.y += deltaT * b1.vy;
        }
    }

	/*
	 * Adjusts the position of two colliding balls when there is overlap
	 */
	void overlapAdjustment(Ball b1, Ball b2, Vector normalVector, double overlap) {
		double xAdjustment = normalVector.x * overlap;
        double yAdjustment = normalVector.y * overlap;
		
		b1.x -= xAdjustment;
		b1.y -= yAdjustment;
		b2.x += xAdjustment;
		b2.y += yAdjustment;
	}
	
	/*
	 * Calculates the new velocities of each ball upon collision
	 * - The values v1 & v2 are calculated using the theorems for:
	 *   - Convervation of Energy
	 *   - Convervation of Momentum 
	 * 	 Using these we get two equations with two unknown variables, which we resolve with substitution
	 */  
    double[] calculateVelocity(Ball b1, Ball b2, Vector normalVector, Vector tangentialVector) {        
		// Calculate new velocities 
        double v1 = ((b1.vx * normalVector.x + b1.vy * normalVector.y) * (b1.mass - b2.mass) + 2.0 * b2.mass * (b2.vx * normalVector.x + b2.vy * normalVector.y)) / (b1.mass + b2.mass);
        double v2 = ((b2.vx * normalVector.x + b2.vy * normalVector.y) * (b2.mass - b1.mass) + 2.0 * b1.mass * (b1.vx * normalVector.x + b1.vy * normalVector.y)) / (b1.mass + b2.mass);

        // Projection unto x-axis & y-axis
		// * Since the magnitude of our tangential and normal vectors are one, it becomes trivial to divide by their magnitude in the projections below
        double b1vx = tangentialVector.x * (b1.vx * tangentialVector.x + b1.vy * tangentialVector.y) + normalVector.x * v1;
        double b1vy = tangentialVector.y * (b1.vx * tangentialVector.x + b1.vy * tangentialVector.y) + normalVector.y * v1;
        double b2vx = tangentialVector.x * (b2.vx * tangentialVector.x + b2.vy * tangentialVector.y) + normalVector.x * v2;
        double b2vy = tangentialVector.y * (b2.vx * tangentialVector.x + b2.vy * tangentialVector.y) + normalVector.y * v2;

		return new double[] {b1vx, b1vy, b2vx, b2vy};		
    }

	// Applies velocity to each ball
	void applyVelocity(Ball b1, Ball b2, double[] velocities) {
		b1.vx = velocities[0];
        b1.vy = velocities[1];
		b2.vx = velocities[2];
		b2.vy = velocities[3];
	}

    /**
     * Simple inner class describing balls
     */
    class Ball {
        double density = 10;
        Color color;
        double x, y, vx, vy, radius, mass;

        Ball(double x, double y, double vx, double vy, double r, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = r;
            this.color = color;
            this.mass = density * PI * pow(r, 2);
        }
    }

	/** 
	 * Simple inner class describing vectors
	 */
	class Vector {
		double x,y;
		
		Vector(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

    /**
	 * Simple inner class for setting colours
	 */
    class ColourSetter{
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

		private void setColour(Ball b) {
			b.color = getRandomColor(b.color);
		}

        private Color getRandomColor(Color currentColor) {
            Color newColor;
            do {
                newColor = neonColors[random.nextInt(neonColors.length)];
            } while (newColor.equals(currentColor)); // Ensure new color is different from the current one
            return newColor;
        }
    }
}