/*
  @author Timo Wiren
  @date 2015-01-02
 
  Note: Uniform locations not cached, but does not matter in a simple game like this.
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

    public void setVector3( String name, float[] vector )
    {
        glUniform3f( glGetUniformLocation( programId, name ), vector[ 0 ], vector[ 1 ],
                    vector[ 2 ] );
    }

    public void setVector4( String name, float[] vector )
    {
        glUniform4f( glGetUniformLocation( programId, name ), vector[ 0 ], vector[ 1 ],
                     vector[ 2 ], vector[ 3 ] );
    }
    
    public void setFloat( String name, float value )
    {
        glUniform1f( glGetUniformLocation( programId, name ), value );
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
