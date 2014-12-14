/*
  Rally - A simple rally game

  @author Timo Wiren
  @date 2014-12-13
 
  Uses OpenGL 4.1, so make sure your driver can handle it.

  Compilation:
  javac -cp lwjgl/jar/lwjgl.jar:./PNGDecoder.jar Rally.java Texture.java Shader.java
 
  Running (OS X):
  java -cp .:res:lwjgl/jar/lwjgl.jar:PNGDecoder.jar -Djava.library.path=./lwjgl/native/macosx/x64 Rally
 */
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
 
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

public class Rally
{ 
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private int width = 640;
    private int height = 480;
    private long window;

    // List<String> lines = Files.readAllLines(Paths.get(path), encoding);
    
    public static String readFile( String path ) throws IOException
    {
        byte[] encoded = Files.readAllBytes( Paths.get( path ) );
        return new String( encoded, StandardCharsets.UTF_8 );
    }

    public static void checkGLError( String info )
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

    private void GenerateQuadBuffers()
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

    float[] makeProjectionMatrix( float left, float right, float bottom, float top, float nearDepth, float farDepth )
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

    public void run()
    {
        System.out.println("LWJGL " + Sys.getVersion());
 
        try
        {
            init();
            loop();
 
            glfwDestroyWindow(window);
            keyCallback.release();
        }
        finally
        {
            glfwTerminate();
            errorCallback.release();
        }
    }
 
    private void init()
    {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
 
        if (glfwInit() != GL11.GL_TRUE)
        {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        
        glfwDefaultWindowHints();
        glfwWindowHint( GLFW_CONTEXT_VERSION_MAJOR, 4 );
        glfwWindowHint( GLFW_CONTEXT_VERSION_MINOR, 1 );
        glfwWindowHint( GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE );
        glfwWindowHint( GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE );
        glfwWindowHint( GLFW_VISIBLE, GL_FALSE );
        glfwWindowHint( GLFW_RESIZABLE, GL_FALSE );
  
        // Create the window
        window = glfwCreateWindow( width, height, "Hello World!", NULL, NULL );
        
        if (window == NULL)
        {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback()
        {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods)
            {
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                {
                    glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
                }
            }
        });
 
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
            window,
            (GLFWvidmode.width(vidmode) - width) / 2,
            (GLFWvidmode.height(vidmode) - height) / 2
        );
 
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
    }
 
    private void loop()
    {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLContext.createFromCurrent();
 
        Texture texture = new Texture();
        texture.loadPNG( "../assets/player.png" );

        Shader shader = new Shader();
        try
        {
            shader.load( readFile( "../assets/texture.vert" ), readFile( "../assets/texture.frag" ) );
        }
        catch (IOException e)
        {
            System.out.println( "Could not open shader file." );
            return;
        }
        
        glClearColor( 1.0f, 0.0f, 0.0f, 0.0f );

        GenerateQuadBuffers();
        shader.use();
        float[] uiMatrix = makeProjectionMatrix( 0, width, height, 0, 0, 1 );
        shader.setMatrix44( "uProjectionMatrix", uiMatrix );
        float[] scaleOffset = new float[] { 30, 30, 20, 20 };
        shader.setVector4( "uScaleAndTranslation", scaleOffset );
        checkGLError( "after uploading uniforms" );
        
        while (glfwWindowShouldClose(window) == GL_FALSE)
        {
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
 
            glDrawArrays( GL_TRIANGLES, 0, 6 );
            
            glfwSwapBuffers( window );
            glfwPollEvents();
            checkGLError( "frame end" );
        }
    }
 
    public static void main(String[] args)
    {
        new Rally().run();
    }
}