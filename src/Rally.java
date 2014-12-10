/*
  Rally - A simple rally game

  @author Timo Wiren
  @date 2014-12-10
 
  Uses OpenGL 4.1, so make sure your driver can handle it.

  Compilation:
  javac -cp lwjgl/jar/lwjgl.jar:./PNGDecoder.jar Rally.java Texture.java
 
  Running (OS X):
  java -cp .:res:lwjgl/jar/lwjgl.jar:PNGDecoder.jar -Djava.library.path=./lwjgl/native/macosx/x64 Rally
 */
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
 
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
 
public class Rally
{ 
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
 
    private long window;
 
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
 
        int WIDTH = 300;
        int HEIGHT = 300;
 
        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        
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
            (GLFWvidmode.width(vidmode) - WIDTH) / 2,
            (GLFWvidmode.height(vidmode) - HEIGHT) / 2
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
        texture.loadPNG( "../assets/glider.png" );

        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
 
        while (glfwWindowShouldClose(window) == GL_FALSE)
        {
            glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
 
            glfwSwapBuffers( window );
            glfwPollEvents();
        }
    }
 
    public static void main(String[] args)
    {
        new Rally().run();
    }
}