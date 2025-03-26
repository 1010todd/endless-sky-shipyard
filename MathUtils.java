import java.awt.Point;

public class MathUtils {
	static public double lengthPoints(Point a, Point b) {
		return Math.sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y));
	}

	static public Point polarToCartesian(double len, double angle_deg) {
		Point ret = new Point();

		ret.x = (int)Math.round(len * Math.cos(Math.toRadians(angle_deg)));
		ret.y = (int)Math.round(len * Math.sin(Math.toRadians(angle_deg)));
		return ret;
	}
	static public double cartesianToPolarAngle(double x, double y) {
		return Math.atan2(x, y);
	}
}
