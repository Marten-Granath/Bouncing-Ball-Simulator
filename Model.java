package bouncing_balls;

import java.awt.*;
import java.util.Random;
import static java.lang.Math.*;

class Model {

    double areaWidth, areaHeight;
    double gravity = -5;
    Ball[] balls;

    Random random = new Random();

    // Array of neon colors
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
    private Color getRandomColor(Color currentColor) {
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

        balls[0] = new Ball(3.5, 3, 2, -2, 0.25, getRandomColor(null));
        balls[1] = new Ball(1, 2, 3, 0.5, 0.2, getRandomColor(null));
    }

    void step(double deltaT) {
        for (Ball b1 : balls) {
            boolean collisionOccurred = false;

            // Handle ball-to-ball collisions
            for (Ball b2 : balls) {
                if (b1 != b2) {
                    double dx = b2.x - b1.x;
                    double dy = b2.y - b1.y;
                    double distance = sqrt(pow(dx,2) + pow(dy,2)); //Pythagoras theorem to calculate distance between center of balls

                    if (distance < b1.radius + b2.radius) { // tells if there is an overlap
                        // Adjust positions to prevent overlap
                        double overlap = (b1.radius + b2.radius - distance) / 2; // overlap per ball
						double xNormal = dx/distance;
						double yNormal = dy/distance;
						
						overlapAdjustment(b1, b2, xNormal, yNormal, overlap);

						applyVelocity(b1, b2, calculateVelocity(b1, b2, xNormal, yNormal));

                        collisionOccurred = true;
                    }
                }
            }

            // Detect collision with the walls
            if (b1.x < b1.radius || b1.x > areaWidth - b1.radius) {
                b1.vx *= -1;
                collisionOccurred = true;
            }
            if (b1.y < b1.radius || b1.y > areaHeight - b1.radius) {
                b1.vy *= -1;
                // Unstick from borders
                if (b1.y < b1.radius) b1.y = b1.radius;
                if (b1.y > areaHeight - b1.radius) b1.y = areaHeight - b1.radius;
                collisionOccurred = true;
            }

            // Change color of the ball if collision occurred
            if (collisionOccurred) {
                b1.color = getRandomColor(b1.color);
            }

            // Apply gravity
            b1.vy += deltaT * gravity;

            // Update position
            b1.x += deltaT * b1.vx;
            b1.y += deltaT * b1.vy;
        }
    }
	void overlapAdjustment(Ball b1, Ball b2, double xNormal, double yNormal, double overlap) {
		double xAdjustment = xNormal * overlap;
        double yAdjustment = yNormal * overlap;
		
		b1.x -= xAdjustment;
		b1.y -= yAdjustment;
		b2.x += xAdjustment;
		b2.y += yAdjustment;
	}
	
    double[] calculateVelocity(Ball b1, Ball b2, double xNormal, double yNormal) {
        // Tangential vector (perpendicular to the normal)
        double tx = -yNormal;
        double ty = xNormal;

        // Dot product of velocity along the normal and tangent directions
        double dpTan1 = b1.vx * tx + b1.vy * ty;
        double dpTan2 = b2.vx * tx + b2.vy * ty;

        double dpNorm1 = b1.vx * xNormal + b1.vy * yNormal;
        double dpNorm2 = b2.vx * xNormal + b2.vy * yNormal;

        // Compute new normal velocities after collision (1D elastic collision equations)
        double m1 = (dpNorm1 * (b1.mass - b2.mass) + 2.0 * b2.mass * dpNorm2) / (b1.mass + b2.mass);
        double m2 = (dpNorm2 * (b2.mass - b1.mass) + 2.0 * b1.mass * dpNorm1) / (b1.mass + b2.mass);

        // Update velocities along the normal and tangential directions
        double b1vx = tx * dpTan1 + xNormal * m1;
        double b1vy = ty * dpTan1 + yNormal * m1;
        double b2vx = tx * dpTan2 + xNormal * m2;
        double b2vy = ty * dpTan2 + yNormal * m2;

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
}