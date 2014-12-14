/*
  @author Timo Wiren
  @date 2014-12-13
*/
/*import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform2i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform3i;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniform4i;
import static org.lwjgl.opengl.GL20.glUniformMatrix3;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glUseProgram;
 */
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import java.nio.FloatBuffer;

public class Shader
{
    private int programId;
    private FloatBuffer mat44buffer = BufferUtils.createFloatBuffer( 16 );
    
    public void load( String vertexSource, String fragmentSource )
    {
        if (vertexSource == null || fragmentSource == null)
        {
            throw new IllegalArgumentException( "Null shader source!" );
        }
        
        int vertId = compile( vertexSource, GL_VERTEX_SHADER );
        int fragId = compile( fragmentSource, GL_FRAGMENT_SHADER );

        Rally.checkGLError( "Shader load after compiling shaders." );

        programId = glCreateProgram();
        glAttachShader( programId, vertId );
        glAttachShader( programId, fragId );
        glLinkProgram( programId );
        
        int comp = glGetProgrami( programId, GL_LINK_STATUS );
        int len = glGetProgrami( programId, GL_INFO_LOG_LENGTH );
        String err = glGetProgramInfoLog( programId, len );
        
        if (comp == GL11.GL_FALSE)
        {
            System.out.println( "Could not link program." );
            if (err != null && err.length() != 0)
            {
                System.out.println( err );
            }
        }
        
        Rally.checkGLError( "Shader load end" );
    }
    
    public void use()
    {
        glUseProgram( programId );
    }
    
    public void setMatrix44( String name, float[] matrix )
    {
        mat44buffer.clear();
        mat44buffer.put( matrix );
        mat44buffer.flip();
        glUniformMatrix4( glGetUniformLocation( programId, name ), false, mat44buffer );
    }
    
    public void setVector4( String name, float[] vector )
    {
        glUniform4f( glGetUniformLocation( programId, name ), vector[ 0 ], vector[ 1 ],
                     vector[ 2 ], vector[ 3 ] );
    }
    
    private int compile( String source, int shaderType )
    {
        assert shaderType == GL_VERTEX_SHADER || shaderType == GL_FRAGMENT_SHADER;

        int shader = glCreateShader( shaderType );
        glShaderSource( shader, source );
        glCompileShader( shader );
        
        int comp = glGetShaderi( shader, GL_COMPILE_STATUS );
        int len = glGetShaderi( shader, GL_INFO_LOG_LENGTH );
        String err = glGetShaderInfoLog( shader, len );

        if (comp == GL11.GL_FALSE)
        {
            System.out.println( "Could not compile " + (shaderType == GL_VERTEX_SHADER ? "vertex shader" : "fragment shader"));
            if (err != null && err.length() != 0)
            {
                System.out.println( err );
                return 0;
            }
        }
        
        return shader;
    }
}
