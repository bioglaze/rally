/*
  @author Timo Wiren
  @date 2014-12-26
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
    private Shader modelShader = new Shader();
    private float[] viewMatrix = new float[ 16 ];
    private float[] perspMatrix = new float[ 16 ];
    private float[] viewProjection = new float[ 16 ];

    public void draw( Model model )
    {
        model.draw( modelShader, viewProjection );
    }
    
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

    public void clear()
    {
        glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
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
            shader.load( readFile( "assets/texture.vert" ), readFile( "assets/texture.frag" ) );
        }
        catch (IOException e)
        {
            System.out.println( "Could not open shader file." );
            return;
        }

        shader.use();
        float[] uiMatrix = Matrix.makeProjectionMatrix( 0, width, height, 0, 0, 1 );
        shader.setMatrix44( "uProjectionMatrix", uiMatrix );
        float[] scaleOffset = new float[] { 30, 30, 20, 20 };
        shader.setVector4( "uScaleAndTranslation", scaleOffset );
        checkGLError( "after uploading uniforms" );

        try
        {
            modelShader.load( readFile( "assets/model.vert" ), readFile( "assets/model.frag" ) );
        }
        catch (IOException e)
        {
            System.out.println( "Could not open shader file." );
            return;
        }
        
        perspMatrix = Matrix.makeProjectionMatrix( 45, width / (float)height, 1, 200 );
        viewProjection = perspMatrix;
        
        glClearColor( 0, 0, 0, 0 );
        glEnable( GL_DEPTH_TEST );
    }

    public void lookAt( Vec3 position, Vec3 target )
    {
        viewMatrix = Matrix.makeLookAt( position, target, new Vec3( 0, 1, 0 ) );
        viewProjection = Matrix.multiply( viewMatrix, perspMatrix );
    }
    
    private void generateQuadBuffers()
    {
        checkGLError( "GenerateQuadBuffers begin" );
        
        int vao = glGenVertexArrays();
        glBindVertexArray( vao );
        
        //float[] quad = new float[] { 0, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1 };
        float[] quad = new float[] { 0, 0, 0, 0, // x, y, u, v
                                     0, 1, 0, 1,
                                     1, 0, 1, 0,
                                     1, 0, 1, 0,
                                     1, 1, 1, 1,
                                     0, 1, 0, 1 };
        FloatBuffer positionBuffer = BufferUtils.createFloatBuffer( quad.length );
        positionBuffer.put( quad );
        positionBuffer.flip();
        
        int vbo = glGenBuffers();
        glBindBuffer( GL_ARRAY_BUFFER, vbo );
        glBufferData( GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW );
        glEnableVertexAttribArray( 0 );
        glVertexAttribPointer( 0, 4, GL_FLOAT, false, 0, 0 );
        checkGLError( "GenerateQuadBuffers end" );
    }

    // List<String> lines = Files.readAllLines(Paths.get(path), encoding);
    
    private String readFile( String path ) throws IOException
    {
        byte[] encoded = Files.readAllBytes( Paths.get( path ) );
        return new String( encoded, StandardCharsets.UTF_8 );
    }
}
