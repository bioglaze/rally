/**
   @author Timo Wiren
   @date 2014-12-19
 */
public class Matrix
{
    public static float[] makeProjectionMatrix( float left, float right, float bottom, float top, float nearDepth, float farDepth )
    {
        float tx = -((right + left) / (right - left));
        float ty = -((top + bottom) / (top - bottom));
        float tz = -((farDepth + nearDepth) / (farDepth - nearDepth));
        
        float[] matrix = new float[]
        {
            2.0f / (right - left), 0.0f, 0.0f, 0.0f,
            0.0f, 2.0f / (top - bottom), 0.0f, 0.0f,
            0.0f, 0.0f, -2.0f / (farDepth - nearDepth), 0.0f,
            tx, ty, tz, 1.0f
        };
        
        return matrix;
    }
    
    public static float[] makeProjectionMatrix( float fovDegrees, float aspect, float nearDepth, float farDepth )
    {
        float top = (float)Math.tan( (double)(fovDegrees * (3.141592653589f / 360.0f) )) * nearDepth;
        float bottom = -top;
        float left = aspect * bottom;
        float right = aspect * top;
        
        float x = (2 * nearDepth) / (right - left);
        float y = (2 * nearDepth) / (top - bottom);
        float a = (right + left)  / (right - left);
        float b = (top + bottom)  / (top - bottom);
        
        float c = -(farDepth + nearDepth) / (farDepth - nearDepth);
        float d = -(2 * farDepth * nearDepth) / (farDepth - nearDepth);
        
        float proj[] =
        new float[] {
            x, 0, 0,  0,
            0, y, 0,  0,
            a, b, c, -1,
            0, 0, d,  0
        };
        
        return proj;
    }
    
    public static float[] makeIdentity( float[] matrix )
    {
        assert matrix.length == 16;
        
        for (int i = 0; i < 16; ++i)
        {
            matrix[ i ] = 0;
        }
        
        matrix[  0 ] = 1;
        matrix[  5 ] = 1;
        matrix[ 10 ] = 1;
        matrix[ 15 ] = 1;
        
        return matrix;
    }
    
    public static float[] multiply( float[] a, float[] b )
    {
        assert a.length == 16 && b.length == 16;
        
        float tmp[] = new float[ 16 ];
        
        for (int j = 0; j < 4; ++j)
        {
            for (int i = 0; i < 4; ++i)
            {
                tmp[ i * 4 + j ] = a[ i * 4 + 0 ] * b[ 0 * 4 + j ] +
                a[ i * 4 + 1 ] * b[ 1 * 4 + j ] +
                a[ i * 4 + 2 ] * b[ 2 * 4 + j ] +
                a[ i * 4 + 3 ] * b[ 3 * 4 + j ];
            }
        }
        
        return tmp;
    }
    
    public static float[] makeLookAt( Vec3 eye, Vec3 center, Vec3 up )
    {
        Vec3 forward = Vec3.subtract( center, eye ).normalized();
        
        Vec3 right = Vec3.cross( forward, up ).normalized();
        
        Vec3 newUp = Vec3.cross( right, forward );
        
        float m[] = new float[ 4 * 4 ];
        
        m[ 0 ] = right.x;
        m[ 4 ] = right.y;
        m[ 8 ] = right.z;
        m[ 12 ] = 0;
        
        m[ 1 ] = newUp.x;
        m[ 5 ] = newUp.y;
        m[ 9 ] = newUp.z;
        m[13 ] = 0;
        
        m[ 2 ] = -forward.x;
        m[ 6 ] = -forward.y;
        m[10 ] = -forward.z;
        m[14 ] = 0;
        
        m[ 3 ] = m[ 7 ] = m[ 11 ] = 0;
        m[15 ] = 1;
        
        float translate[] = new float[ 4 * 4 ];
        makeIdentity( translate );
        translate[ 12 ] = -eye.x;
        translate[ 13 ] = -eye.y;
        translate[ 14 ] = -eye.z;
        
        m = Matrix.multiply( translate, m );
        
        return m;
    }
}
