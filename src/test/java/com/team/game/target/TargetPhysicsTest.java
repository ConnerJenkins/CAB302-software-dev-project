package test.java.com.team.game.target;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import main.java.com.team.game.target.TargetPhysics;

/**
 * Unit tests for {@link TargetPhysics}.
 * Verifies projectile motion formulas for required speed, hit detection, and error handling.
 */
public class TargetPhysicsTest {

    /** Ensures the computed speed allows the projectile to hit the target within a tight tolerance. */
    @Test
    void requiredSpeed_whenUsed_hitsTargetWithinTolerance() {
        double g = 9.8;
        double angle = Math.toRadians(45.0);
        double x = 12.0;
        double y = 3.0;
        double y0 = 0.0;

        double v = TargetPhysics.requiredSpeed(g, angle, x, y);
        double yAtWall = TargetPhysics.yAtX(v, angle, x, y0, g);

        assertEquals(y, yAtWall, 1e-6, "Projectile should reach target height at wall");
    }

    /** Verifies that an exception is thrown if the target lies above the projectile’s possible apex. */
    @Test
    void requiredSpeed_throws_ifTargetIsAboveApexLine() {
        double g = 9.8;
        double angle = Math.toRadians(35.0);
        double x = 10.0;
        double yAboveVertexLine = x * Math.tan(angle);

        assertThrows(IllegalArgumentException.class, () ->
                TargetPhysics.requiredSpeed(g, angle, x, yAboveVertexLine));
    }

    /** Confirms {@link TargetPhysics#isHit(double, double, double)} works correctly for hit radius detection. */
    @Test
    void isHit_true_whenWithinRadius_falseOtherwise() {
        double center = 300.0;
        double radius = 14.0;

        assertTrue(TargetPhysics.isHit(300.0, center, radius));
        assertTrue(TargetPhysics.isHit(312.0, center, radius));
        assertFalse(TargetPhysics.isHit(315.0, center, radius));
    }
}
