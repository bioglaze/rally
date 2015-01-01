/**
   @author Timo Wiren
   @date 2015-01-01
 */
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.io.IOException;

// Container class for assets.
public class Assets
{
    public Model car;
    public Model track;
    public Model lap;
    public Model mine;
    private Texture timeBarTexture = new Texture();
    private Texture lapTexture = new Texture();
    private Texture trackTexture = new Texture();
    private Texture carTexture = new Texture();
    private Texture mineTexture = new Texture();
    
    public void init()
    {
        car = createModel( "assets/car.obj" );
        track = createModel( "assets/track.obj" );
        lap = createModel( "assets/lap.obj" );
        mine = createModel( "assets/mine.obj" );
        timeBarTexture.loadImage( "assets/white.png" );
        lapTexture.loadImage( "assets/lap.png" );
        trackTexture.loadImage( "assets/grass.jpg" );
        carTexture.loadImage( "assets/player.png" );
        mineTexture.loadImage( "assets/mine.png" );
        
        car.setPosition( new Vec3( 0, 0, -15 ) );
        car.setRotation( new Vec3( 0, 0, -1 ) );
        
        track.setPosition( new Vec3( 0, 0, -15 ) );
        track.setRotation( new Vec3( 0, 0, -1 ) );
        track.setScale( 5 );
        
        lap.setPosition( new Vec3( 0, 0, -20 ) );
        mine.setPosition( new Vec3( 0, 1, -10 ) );
    }
    
    public void draw( Renderer renderer )
    {
        renderer.draw( track, trackTexture );
        renderer.draw( car, carTexture );
        renderer.draw( lap, lapTexture );
        renderer.draw( mine, mineTexture );
        renderer.draw( timeBarTexture, 20, 20, 300 - 40, 20 );
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

