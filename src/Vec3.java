/**
  @author Timo Wiren
  @date 2014-12-19
*/
public class Vec3
{
    public Vec3( float ax, float ay, float az )
    {
        x = ax;
        y = ay;
        z = az;
    }
    
    public float length()
    {
        return (float)Math.sqrt( x * x + y * y + z * z );
    }
    
    public Vec3 normalized()
    {
        float invLength = 1.0f / length();
        Vec3 outNormalized = new Vec3( x, y, z );
        outNormalized.x *= invLength;
        outNormalized.y *= invLength;
        outNormalized.z *= invLength;
        return outNormalized;
    }

    public static Vec3 add( Vec3 v1, Vec3 v2 )
    {
        return new Vec3( v1.x + v2.x, v1.y + v2.y, v1.z + v2.z );
    }

    public static Vec3 subtract( Vec3 v1, Vec3 v2 )
    {
        return new Vec3( v1.x - v2.x, v1.y - v2.y, v1.z - v2.z );
    }
    
    public static Vec3 cross( Vec3 v1, Vec3 v2 )
    {
        return new Vec3( v1.y * v2.z - v1.z * v2.y,
                         v1.z * v2.x - v1.x * v2.z,
                         v1.x * v2.y - v1.y * v2.x );
    }
    
    public static float dot( Vec3 v1, Vec3 v2 )
    {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public float x, y, z;
}
