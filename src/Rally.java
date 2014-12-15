/*
  Rally - A simple rally game

  @author Timo Wiren
  @date 2014-12-15
 
  Uses OpenGL 4.1, so make sure your driver can handle it.

  Compilation:
  javac -cp lwjgl/jar/lwjgl.jar:./PNGDecoder.jar *.java
 
  Running (OS X):
  java -cp .:res:lwjgl/jar/lwjgl.jar:PNGDecoder.jar -Djava.library.path=./lwjgl/native/macosx/x64 Rally
 */
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL11.*;

public class Rally
{
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private int width = 640;
    private int height = 480;
    private long window;
    private Assets assets = new Assets();
    
    public static void main(String[] args)
    {
        new Rally().run();
    }

    public void run()
    {
        System.out.println("LWJGL " + Sys.getVersion());
 
        try
        {
            init();
            loop();
 
            glfwDestroyWindow( window );
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
            throw new IllegalStateException( "Unable to initialize GLFW" );
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
        
        glfwSetKeyCallback(window, keyCallback = GLFWKeyCallback((win, key, scanCode, action, mods) -> keyAction( key, action )));

        ByteBuffer vidmode = glfwGetVideoMode( glfwGetPrimaryMonitor() );
        // Center our window
        glfwSetWindowPos(
            window,
            (GLFWvidmode.width( vidmode ) - width) / 2,
            (GLFWvidmode.height( vidmode ) - height) / 2
        );
 
        glfwMakeContextCurrent( window );
        glfwSwapInterval( 1 );
        glfwShowWindow( window );
    }

    private void keyAction( int key, int action )
    {
        if (action == GLFW_PRESS)
        {
        
        }
        else if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
        {
            glfwSetWindowShouldClose( window, GL_TRUE ); // We will detect this in our rendering loop
        }

        //System.out.println( "key action. Key: " + key + ", action: " + action );
    }
    
    private void loop()
    {
        GLContext.createFromCurrent();
 
        Texture texture = new Texture();
        texture.loadPNG( "../assets/player.png" );
        
        glClearColor( 1.0f, 0.0f, 0.0f, 0.0f );

        Renderer renderer = new Renderer();
        renderer.init( width, height );
        
        assets.init();
        
        while (glfwWindowShouldClose(window) == GL_FALSE)
        {
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
 
            glDrawArrays( GL_TRIANGLES, 0, 6 );
            
            glfwSwapBuffers( window );
            glfwPollEvents();
            renderer.checkGLError( "frame end" );
        }
    }
}