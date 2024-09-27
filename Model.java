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

        balls[0] = new Ball(3.5, 0, 0, -2, 0.4, getRandomColor(null));
        balls[1] = new Ball(3.5, 2, 0.5, 0.5, 0.2, getRandomColor(null));
    }

    void step(double deltaT) {
        for (Ball b : balls) {
            boolean collisionOccurred = false;

            // Handle ball-to-ball collisions
            for (Ball o : balls) {
                if (b != o) {
                    double dx = o.x - b.x;
                    double dy = o.y - b.y;
                    double distance = sqrt(dx * dx + dy * dy);

                    if (distance < b.radius + o.radius) {
                        // Adjust positions to prevent overlap
                        double overlap = (b.radius + o.radius - distance) / 2;
                        double nx = dx / distance;
                        double ny = dy / distance;

                        b.x -= nx * overlap;
                        b.y -= ny * overlap;
                        o.x += nx * overlap;
                        o.y += ny * overlap;

                        // Calculate new velocities using 2D elastic collision physics
                        resolveCollision(b, o);

                        collisionOccurred = true;
                    }
                }
            }

            // Detect collision with the walls
            if (b.x < b.radius || b.x > areaWidth - b.radius) {
                b.vx *= -1;
                collisionOccurred = true;
            }
            if (b.y < b.radius || b.y > areaHeight - b.radius) {
                b.vy *= -1;
                // Unstick from borders
                if (b.y < b.radius) b.y = b.radius;
                if (b.y > areaHeight - b.radius) b.y = areaHeight - b.radius;
                collisionOccurred = true;
            }

            // Change color of the ball if collision occurred
            if (collisionOccurred) {
                b.color = getRandomColor(b.color);
            }

            // Apply gravity
            b.vy += deltaT * gravity;

            // Update position
            b.x += deltaT * b.vx;
            b.y += deltaT * b.vy;
        }
    }

    // Function to resolve collisions between two balls using 2D elastic collision physics
    void resolveCollision(Ball b1, Ball b2) {
        // Get the vector between the balls
        double dx = b2.x - b1.x;
        double dy = b2.y - b1.y;
        double distance = sqrt(dx * dx + dy * dy);

        // Normal vector (direction of collision)
        double nx = dx / distance;
        double ny = dy / distance;

        // Tangential vector (perpendicular to the normal)
        double tx = -ny;
        double ty = nx;

        // Dot product of velocity along the normal and tangent directions
        double dpTan1 = b1.vx * tx + b1.vy * ty;
        double dpTan2 = b2.vx * tx + b2.vy * ty;

        double dpNorm1 = b1.vx * nx + b1.vy * ny;
        double dpNorm2 = b2.vx * nx + b2.vy * ny;

        // Compute new normal velocities after collision (1D elastic collision equations)
        double m1 = (dpNorm1 * (b1.mass - b2.mass) + 2.0 * b2.mass * dpNorm2) / (b1.mass + b2.mass);
        double m2 = (dpNorm2 * (b2.mass - b1.mass) + 2.0 * b1.mass * dpNorm1) / (b1.mass + b2.mass);

        // Update velocities along the normal and tangential directions
        b1.vx = tx * dpTan1 + nx * m1;
        b1.vy = ty * dpTan1 + ny * m1;
        b2.vx = tx * dpTan2 + nx * m2;
        b2.vy = ty * dpTan2 + ny * m2;
    }

    /**
     * Simple inner class describing balls.
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