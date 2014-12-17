/**
   @author Timo Wiren
   @date 2014-12-15
 */
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.io.IOException;

public class Assets
{
    public Model car;
    
    public void init()
    {
        car = new Model();

        try
        {
            List< String > objLines = Files.readAllLines( Paths.get( "../assets/car.obj" ), StandardCharsets.UTF_8 );
            String[] objLineArray = new String[ objLines.size() ];
            objLines.toArray( objLineArray );
            car.initFromObjFileContents( objLineArray );
        }
        catch (IOException e)
        {
            System.out.println( "Could not open .obj file!" );
        }
    }
}

