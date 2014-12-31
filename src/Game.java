/**
   @author Timo Wiren
   @date 2014-12-31
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
    private Texture timeBarTexture = new Texture();
    private double remainingLapTime = 5;

    public Game( Renderer renderer )
    {
        assets.init();
        assets.car.setPosition( new Vec3( 0, 0, -15 ) );
        assets.car.setRotation( new Vec3( 0, 0, -1 ) );

        assets.track.setPosition( new Vec3( 0, 0, -15 ) );
        assets.track.setRotation( new Vec3( 0, 0, -1 ) );
        assets.track.setScale( 5 );

        assets.lap.setPosition( new Vec3( 0, 0, -20 ) );
        
        this.renderer = renderer;
        
        Texture texture = new Texture();
        texture.loadImage( "assets/player.png" );
        
        timeBarTexture.loadImage( "assets/player.png" );
    }
    
    public void draw()
    {
        renderer.draw( assets.track );
        renderer.draw( assets.car );
        renderer.draw( assets.lap );
        renderer.draw( timeBarTexture, 20, 20, 300 - 40, 20 );
    }
    
    public void update()
    {
        updateCarMovement();
        updateCamera();
        updateLap();
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
            float signSpeed = isReversing ? -0.5f : 0.5f;
            float dirX = (float)Math.sin( -assets.car.getRotation().y * Math.PI / 180.0 ) * signSpeed;
            float dirZ = (float)Math.cos( -assets.car.getRotation().y * Math.PI / 180.0 ) * signSpeed;
            Vec3 newPosition = Vec3.add( assets.car.getPosition(), new Vec3( dirX, 0, dirZ ) );
            assets.car.setPosition( newPosition );
        }
    }
    
    private void updateCamera()
    {
        Vec3 camPos = Vec3.add( assets.car.getPosition(), new Vec3( 0, 16, 10 ) );
        Vec3 camTarget = Vec3.add( assets.car.getPosition(), new Vec3( 0, -80, -50 ) );
        
        renderer.lookAt( camPos, camTarget );
    }

    private void updateLap()
    {
        final float lapChangeDistance = 2;

        if (Vec3.distance( assets.car.getPosition(), assets.lap.getPosition() ) < lapChangeDistance)
        {
            float x = (float)Math.random() * 10;
            float z = (float)Math.random() * 10;
            assets.lap.setPosition( new Vec3( x, 0, z ) );
        }
    }
}
