/*
  @author Timo Wiren
  @date 2014-12-10
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

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

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
            }
            catch (IOException e)
            {
            }
            
            
            id = glGenTextures();
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