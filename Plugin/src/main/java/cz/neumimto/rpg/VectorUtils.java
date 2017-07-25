package cz.neumimto.rpg;

import com.flowpowered.math.GenericMath;
import com.flowpowered.math.TrigMath;
import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;

/**
 * Created by NeumimTo on 22.7.2017.
 */
public class VectorUtils {

	public static final double RAD_TO_DEG = 180.0 / Math.PI;
	public static final double DEG_TO_RAD = Math.PI / 180.0;

	public static Quaterniond eulerRad(Vector3d vector3d) {
		//https://gist.github.com/aeroson/043001ca12fe29ee911e
		double yaw = vector3d.getX();
		double pitch = vector3d.getY();
		double roll = vector3d.getZ();

		double rollOver2 = roll * 0.5f;
		double sinRollOver2 = TrigMath.sin(rollOver2);
		double cosRollOver2 = TrigMath.cos(rollOver2);
		double pitchOver2 = pitch * 0.5f;
		double sinPitchOver2 = TrigMath.sin(pitchOver2);
		double cosPitchOver2 = TrigMath.cos(pitchOver2);
		double yawOver2 = yaw * 0.5f;
		double sinYawOver2 = TrigMath.sin(yawOver2);
		double cosYawOver2 = TrigMath.cos(yawOver2);
		return new Quaterniond(
				cosYawOver2 * cosPitchOver2 * cosRollOver2 + sinYawOver2 * sinPitchOver2 * sinRollOver2,
				cosYawOver2 * cosPitchOver2 * sinRollOver2 - sinYawOver2 * sinPitchOver2 * cosRollOver2,
				cosYawOver2 * sinPitchOver2 * cosRollOver2 + sinYawOver2 * cosPitchOver2 * sinRollOver2,
				sinYawOver2 * cosPitchOver2 * cosRollOver2 - cosYawOver2 * sinPitchOver2 * sinRollOver2
		);
	}

	public static Quaterniond euler(Vector3d vector3d) {
		return eulerRad(toRad(vector3d));
	}

	public static Vector3d toRad(Vector3d vector3d) {
		return new Vector3d(vector3d.getX() * TrigMath.DEG_TO_RAD, vector3d.getY() * TrigMath.DEG_TO_RAD, vector3d.getZ() * TrigMath.DEG_TO_RAD);
	}

	public static Vector3d transform(Quaterniond q, Vector3d v) {
		double num = q.getX() * 2;
		double num2 = q.getY() * 2;
		double num3 = q.getZ() * 2;
		double num4 = q.getX() * num;
		double num5 = q.getY() * num2;
		double num6 = q.getZ() * num3;
		double num7 = q.getX() * num2;
		double num8 = q.getX() * num3;
		double num9 = q.getY() * num3;
		double num10 = q.getW() * num;
		double num11 = q.getW() * num2;
		double num12 = q.getW() * num3;
		double x = (1 - (num5 + num6)) * v.getX()
				+ (num7 - num12) * v.getY()
				+ (num8 + num11) * v.getZ();
		double y = (num7 + num12) * v.getX()
				+ (1 - (num4 + num6)) * v.getY()
				+ (num9 - num10) * v.getZ();
		double z = (num8 - num11) * v.getX()
				+ (num9 + num10) * v.getY()
				+ (1 - (num4 + num5)) * v.getZ();

		return new Vector3d(x,y,z);
	}

	//https://en.wikipedia.org/wiki/Rotation_matrix
	public static Vector3d rotateAroundAxisX(Vector3d v, double angle) {
		double sin = TrigMath.sin(angle);
		double cos = TrigMath.cos(angle);
		double y = v.getY() * cos - v.getZ() * sin;
		double z = v.getY() * sin + v.getZ() * cos;
		return new Vector3d(v.getX(), y, z);
	}


	public static Vector3d rotateAroundAxisZ(Vector3d v, double angle) {
		double cos = TrigMath.cos(angle);
		double sin = TrigMath.sin(angle);
		double x = v.getX() * cos - v.getY() * sin;
		double y = v.getX() * sin + v.getY() * cos;
		return new Vector3d(x, y, v.getZ());
	}


	public static Vector3d rotateAroundAxisY(Vector3d v, double angle) {
		double cos = TrigMath.cos(angle);
		double sin = TrigMath.sin(angle);
		double z = v.getX() * -sin + v.getZ() * cos;
		double x = v.getX() * cos + v.getZ() * sin;
		return new Vector3d(x, v.getY(), z);
	}

}
