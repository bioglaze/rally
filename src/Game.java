/**
   @author Timo Wiren
   @date 2014-12-26
 */
public class Game
{
    public enum InputAction
    {
        Accelerate,
        Decelerate,
        ReverseAccel,
        ReverseDecel,
        TurnLeft,
        TurnRight
    }

    private Assets assets = new Assets();
    private Renderer renderer;
    private Vec3 carDirection = new Vec3( 0, 0, 0 );
    
    public Game( Renderer renderer )
    {
        assets.init();
        assets.car.setPosition( new Vec3( 0, 0, -15 ) );
        assets.car.setRotation( new Vec3( 0, 0, -1 ) );

        assets.track.setPosition( new Vec3( 0, 0, -15 ) );
        assets.track.setRotation( new Vec3( 0, 0, -1 ) );
        
        this.renderer = renderer;
        
        Texture texture = new Texture();
        texture.loadPNG( "assets/player.png" );
    }
    
    public void draw()
    {
        renderer.draw( assets.track );
        renderer.draw( assets.car );
    }
    
    public void update()
    {
        Vec3 newPosition = Vec3.add( assets.car.getPosition(), Vec3.multiply( assets.car.getRotation(), carDirection ) );
        assets.car.setPosition( newPosition );
    }
    
    public void doAction( InputAction action )
    {
        if (action == InputAction.Accelerate)
        {
            carDirection.z = 0.2f;
        }
        else if (action == InputAction.Decelerate)
        {
            carDirection.z = 0;
        }
        else if (action == InputAction.ReverseAccel)
        {
            carDirection.z = -0.2f;
        }
        else if (action == InputAction.ReverseDecel)
        {
            carDirection.z = 0;
        }
        else if (action == InputAction.TurnLeft)
        {
            
        }
        else if (action == InputAction.TurnRight)
        {
            
        }
        else
        {
            assert false : "Unhandled input action!";
        }
    }

}
