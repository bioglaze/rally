/*
  @author Timo Wiren
  @date 2014-12-10
 
  Adapted from:
  https://github.com/mattdesl/lwjgl-basics/blob/master/src/mdesl/graphics/Texture.java
*/
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.net.URL;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.String;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;

import de.matthiasmann.twl.utils.PNGDecoder;

public class Texture
{
    private int id;
    private int width;
    private int height;
    
    private static File createFile(String ref)
    {
        final File ROOT = new File( "." );
        File file = new File( ROOT, ref );
        
        if (!file.exists())
        {
            file = new File( ref );
        }
        
        return file;
    }
    
    private static URL getResource( String ref )
    {
        URL url = Texture.class.getClassLoader().getResource( ref );
        
        if (url==null)
        {
            try
            {
                File f = createFile(ref);
                if (f.exists())
                {
                    return f.toURI().toURL();
                }
            }
            catch (IOException e) {}
        }
        return url;
    }
    
    // Doesn't work for grayscale format.
    public void loadPNG( String path )
    {
        URL pngRef = getResource( path );
        
        if (pngRef == null)
        {
            System.out.println( "Could not open " + path );
            return;
        }
        
        InputStream input = null;
        
        try
        {
            PNGDecoder dec;
            
            try
            {
                input = pngRef.openStream();
                dec = new PNGDecoder(input);
                width = dec.getWidth();
                height = dec.getHeight();
                ByteBuffer buf = BufferUtils.createByteBuffer(4 * width * height);
                dec.decode(buf, width * 4, PNGDecoder.Format.RGBA);
                buf.flip();
                
                id = glGenTextures();
                
                glBindTexture( GL_TEXTURE_2D, id );
                glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST );
                glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST );
                glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE );
                glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE );
                glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
            }
            catch (IOException e)
            {
            }
            
            System.out.println( "Texture dimension: " + width + "x" + height );
        }
        finally
        {
            if (input == null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }
}