/**
   @author Timo Wiren
   @date 2015-01-05
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
    public Model mines[] = new Model[ 3 ];
    public Model compass;
    private Texture timeBarTexture = new Texture();
    private Texture lapTexture = new Texture();
    private Texture trackTexture = new Texture();
    private Texture carTexture = new Texture();
    private Texture mineTexture = new Texture();
    private Texture compassTexture = new Texture();
    
    public void init()
    {
        car = createModel( "assets/car.obj" );
        track = createModel( "assets/track.obj" );
        lap = createModel( "assets/lap.obj" );
        compass = createModel( "assets/compass.obj" );
        mines[ 0 ] = createModel( "assets/mine.obj" );
        mines[ 1 ] = createModel( "assets/mine.obj" );
        mines[ 2 ] = createModel( "assets/mine.obj" );

        timeBarTexture.loadImage( "assets/white.png" );
        lapTexture.loadImage( "assets/lap.png" );
        trackTexture.loadImage( "assets/grass.jpg" );
        carTexture.loadImage( "assets/player.png" );
        mineTexture.loadImage( "assets/mine.png" );
        compassTexture.loadImage( "assets/white.png" );
        
        car.setPosition( new Vec3( 0, 0, -15 ) );
        car.setRotation( new Vec3( 0, 0, -1 ) );
        
        track.setPosition( new Vec3( 0, 48, -15 ) );
        track.setRotation( new Vec3( 0, 0, -1 ) );
        track.setScale( 6.5f );
        
        lap.setPosition( new Vec3( 0, 0, -20 ) );
        mines[ 0 ].setPosition( new Vec3( 0, 1, -10 ) );
        mines[ 1 ].setPosition( new Vec3( 5, 1, -10 ) );
        mines[ 2 ].setPosition( new Vec3( -5, 1, -10 ) );
        
        compass.setScale( 0.25f );
    }
    
    public void draw( Renderer renderer, float timeBarWidthPercentage )
    {
        renderer.draw( track, trackTexture );
        renderer.draw( car, carTexture );
        renderer.draw( lap, lapTexture );
        renderer.draw( mines[ 0 ], mineTexture );
        renderer.draw( mines[ 1 ], mineTexture );
        renderer.draw( mines[ 2 ], mineTexture );
        
        Vec3 color = timeBarWidthPercentage < 0.1f ? new Vec3( 1, 0, 0 ) : new Vec3( 1, 1, 1 );
        int width = (int)(Math.max( timeBarWidthPercentage, 0.1f ) * 400);
        renderer.draw( timeBarTexture, 20, 20, width, 20, color );

        renderer.draw( compass, compassTexture );
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

