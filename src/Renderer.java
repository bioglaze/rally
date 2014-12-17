/*
  @author Timo Wiren
  @date 2014-12-15
*/
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer
{
    private Shader shader = new Shader();
    
    /**
     Prints OpenGL error to console, if any.
     
     @param info Text to identify the context.
     */
    public void checkGLError( String info )
    {
        int errorCode = GL_INVALID_ENUM;
        
        while ((errorCode = glGetError()) != GL_NO_ERROR)
        {
            String errorStr = "unstringified error";
            
            if (errorCode == GL_INVALID_ENUM)
            {
                errorStr = "GL_INVALID_ENUM";
            }
            else if (errorCode == GL_INVALID_VALUE)
            {
                errorStr = "GL_INVALID_VALUE";
            }
            else if (errorCode == GL_INVALID_OPERATION)
            {
                errorStr = "GL_INVALID_OPERATION";
            }
            
            System.out.println( "OpenGL error in " + info + ": " + errorStr );
        }
    }

    /**
       Side effects: quad buffer is generated and tex shader is compiled and
       bound and its projection uniform is set.
     
       @param width Window's width in pixels.
       @param height Window's height in pixels.
     */
    public void init( int width, int height )
    {
        generateQuadBuffers();

        try
        {
            shader.load( readFile( "../assets/texture.vert" ), readFile( "../assets/texture.frag" ) );
        }
        catch (IOException e)
        {
            System.out.println( "Could not open shader file." );
            return;
        }

        shader.use();
        float[] uiMatrix = makeProjectionMatrix( 0, width, height, 0, 0, 1 );
        shader.setMatrix44( "uProjectionMatrix", uiMatrix );
        float[] scaleOffset = new float[] { 30, 30, 20, 20 };
        shader.setVector4( "uScaleAndTranslation", scaleOffset );
        checkGLError( "after uploading uniforms" );

    }

    private void generateQuadBuffers()
    {
        checkGLError( "GenerateQuadBuffers begin" );
        
        int vao = glGenVertexArrays();
        glBindVertexArray( vao );
        
        float[] quad = new float[] { 0, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1 };
        FloatBuffer positionBuffer = BufferUtils.createFloatBuffer( quad.length );
        positionBuffer.put( quad );
        positionBuffer.flip();
        
        int vbo = glGenBuffers();
        glBindBuffer( GL_ARRAY_BUFFER, vbo );
        glBufferData( GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW );
        glEnableVertexAttribArray( 0 );
        glVertexAttribPointer( 0, 2, GL_FLOAT, false, 0, 0 );
        checkGLError( "GenerateQuadBuffers end" );
    }

    // List<String> lines = Files.readAllLines(Paths.get(path), encoding);
    
    private String readFile( String path ) throws IOException
    {
        byte[] encoded = Files.readAllBytes( Paths.get( path ) );
        return new String( encoded, StandardCharsets.UTF_8 );
    }
    
    private float[] makeProjectionMatrix( float left, float right, float bottom, float top, float nearDepth, float farDepth )
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
    
    private float[] makeProjectionMatrix( float fovDegrees, float aspect, float nearDepth, float farDepth )
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
}
