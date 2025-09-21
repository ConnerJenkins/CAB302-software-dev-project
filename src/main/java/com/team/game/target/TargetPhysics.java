package main.java.com.team.game.target;

public final class TargetPhysics {
    private TargetPhysics() {}

    public static double requiredSpeed(double g, double angleRad, double x, double y) {
        double cos_component = Math.cos(angleRad);
        double tan_component = Math.tan(angleRad);
        double denom = 2.0 * cos_component * cos_component * (x * tan_component - y);
        if (denom <= 0.0) {
            throw new IllegalArgumentException("Unreachable target for that angle/x/y");
        }
        return Math.sqrt((g * x * x) / denom);
    }

    public static double yAtX(double v, double angleRad, double x, double y0, double g) {
        double cos_component = Math.cos(angleRad);
        double sin_component = Math.sin(angleRad);

        double t = x / (v * cos_component);
        return y0 + v * sin_component * t - 0.5 * g * t * t;
    }

    public static boolean isHit(double yPxAtWall, double targetCenterPx, double targetRadiusPx) {
        return Math.abs(yPxAtWall - targetCenterPx) <= targetRadiusPx;
    }
}
