/*
  @author Timo Wiren
  @date 2015-01-02
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Transparency;

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

public class Texture
{
    private int id;
    private int width;
    private int height;
    private boolean opaque;

    public int getId()
    {
        return id;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
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
    
    private ByteBuffer loadImageData( String path )
    {
        BufferedImage image = null;
        
        try
        {
            image = ImageIO.read( Texture.class.getResource( path ) );
        }
        catch (IOException e)
        {
            System.out.println("Could not load " + path );
            return null;
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("Could not load " + path );
            return null;
        }
        
        width = image.getWidth();
        height = image.getHeight();
        int BYTES_PER_PIXEL = image.getTransparency() == Transparency.OPAQUE ? 3 : 4;
        opaque = BYTES_PER_PIXEL == 3;
        
        int[] pixels = new int[ image.getWidth() * image.getHeight() ];
        image.getRGB( 0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth() );
        
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL);
        
        for(int y = 0; y < image.getHeight(); ++y)
        {
            for(int x = 0; x < image.getWidth(); ++x)
            {
                int pixel = pixels[ y * image.getWidth() + x ];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));             // Blue component
                
                if (BYTES_PER_PIXEL == 4)
                {
                    buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component.
                }
            }
        }
        
        buffer.flip();
        return buffer;
    }
    
    public void loadImage( String path )
    {
        ByteBuffer imageBuffer = loadImageData( path );
        id = glGenTextures();
        
        glBindTexture( GL_TEXTURE_2D, id );
        glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST );
        glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST );
        glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE );
        glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE );
        glTexImage2D( GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, opaque ? GL_RGB : GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer );
    }
}