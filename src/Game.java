/**
   @author Timo Wiren
   @date 2014-12-27
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
        TurnRight,
        EndTurning
    }

    private Assets assets = new Assets();
    private Renderer renderer;
    private Vec3 carDirection = new Vec3( 0, 0, 0 );
    private boolean isAccelerating = false;
    private boolean isReversing = false;
    
    public Game( Renderer renderer )
    {
        assets.init();
        assets.car.setPosition( new Vec3( 0, 0, -15 ) );
        assets.car.setRotation( new Vec3( 0, 0, -1 ) );

        assets.track.setPosition( new Vec3( 0, 0, -15 ) );
        assets.track.setRotation( new Vec3( 0, 0, -1 ) );
        assets.track.setScale( 5 );

        this.renderer = renderer;
        
        Texture texture = new Texture();
        texture.loadImage( "assets/player.png" );
    }
    
    public void draw()
    {
        renderer.draw( assets.track );
        renderer.draw( assets.car );
    }
    
    public void update()
    {
        updateCarMovement();
        
        Vec3 camPos = Vec3.add( assets.car.getPosition(), new Vec3( 0, 16, 10 ) );
        Vec3 camTarget = Vec3.add( assets.car.getPosition(), new Vec3( 0, -80, -50 ) );
        
        renderer.lookAt( camPos, camTarget );
    }
    
    public void doAction( InputAction action )
    {
        if (action == InputAction.Accelerate)
        {
            isAccelerating = true;
        }
        else if (action == InputAction.Decelerate)
        {
            isAccelerating = false;
        }
        else if (action == InputAction.ReverseAccel)
        {
            isReversing = true;
        }
        else if (action == InputAction.ReverseDecel)
        {
            isReversing = false;
        }
        else if (action == InputAction.TurnLeft)
        {
            carDirection.y = -2;
        }
        else if (action == InputAction.TurnRight)
        {
            carDirection.y = 2;
        }
        else if (action == InputAction.EndTurning)
        {
            carDirection.y = 0;
        }
        else
        {
            assert false : "Unhandled input action!";
        }
    }
    
    private void updateCarMovement()
    {
        Vec3 rot = assets.car.getRotation();
        float newRotY = assets.car.getRotation().y + carDirection.y;
        assets.car.setRotation( new Vec3( rot.x, newRotY, rot.z ) );
        
        if (isAccelerating || isReversing)
        {
            float signSpeed = isReversing ? 0.5f : -0.5f;
            float dirX = (float)Math.sin( -assets.car.getRotation().y * Math.PI / 180.0 ) * signSpeed;
            float dirZ = (float)Math.cos( -assets.car.getRotation().y * Math.PI / 180.0 ) * signSpeed;
            Vec3 newPosition = Vec3.add( assets.car.getPosition(), new Vec3( dirX, 0, dirZ ) );
            assets.car.setPosition( newPosition );
        }
    }
}
