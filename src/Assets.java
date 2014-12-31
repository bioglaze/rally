/**
   @author Timo Wiren
   @date 2014-12-31
 */
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.io.IOException;

// Container class for assets.
public class Assets
{
    public Model car;
    public Model track;
    public Model lap;
    //public Model mine;
    
    public void init()
    {
        car = createModel( "assets/car.obj" );
        track = createModel( "assets/track.obj" );
        lap = createModel( "assets/lap.obj" );
        //mine = createModel( "assets/mine.obj" );
    }
    
    private Model createModel( String objPath )
    {
        Model outModel = new Model();
        
        try
        {
            List< String > objLines = Files.readAllLines( Paths.get( objPath ), StandardCharsets.UTF_8 );
            String[] objLineArray = new String[ objLines.size() ];
            objLines.toArray( objLineArray );
            outModel.initFromObjFileContents( objLineArray );
        }
        catch (IOException e)
        {
            System.out.println( "Could not open " + objPath );
        }
        
        return outModel;
    }
}

