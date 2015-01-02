/**
   @author Timo Wiren
   @date 2015-01-02
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
    private double remainingLapTime = 5;
    private float signSpeed = 0;
    private long lapFadeStartTime;
    private long mineExplodeTime;

    public Game( Renderer renderer )
    {
        assets.init();
        this.renderer = renderer;
    }
    
    public void draw()
    {
        assets.draw( renderer, solveTimeBarWidth() );
    }
    
    public void update()
    {
        updateCarMovement();
        updateCamera();
        updateLap();
        updateMine();
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
        if (mineExplodeTime != 0)
        {
            return;
        }
        
        Vec3 rot = assets.car.getRotation();
        float newRotY = assets.car.getRotation().y + carDirection.y;
        assets.car.setRotation( new Vec3( rot.x, newRotY, rot.z ) );

        signSpeed *= 0.9f;
        
        if (isAccelerating || isReversing)
        {
            signSpeed = isReversing ? -0.25f : 0.5f;
        }

        float dirX = (float)Math.sin( -assets.car.getRotation().y * Math.PI / 180.0 ) * signSpeed;
        float dirZ = (float)Math.cos( -assets.car.getRotation().y * Math.PI / 180.0 ) * signSpeed;
        Vec3 newPosition = Vec3.add( assets.car.getPosition(), new Vec3( dirX, 0, dirZ ) );
        assets.car.setPosition( newPosition );
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

        if (lapFadeStartTime == 0 && Vec3.distance( assets.car.getPosition(), assets.lap.getPosition() ) < lapChangeDistance)
        {
            lapFadeStartTime = System.currentTimeMillis();
        }
        
        if (lapFadeStartTime > 0)
        {
            float lapOpacity = (float)(System.currentTimeMillis() - lapFadeStartTime) / 1000.0f;
            lapOpacity = 1 - lapOpacity;
            assets.lap.setOpacity( lapOpacity );
        }
        
        if (lapFadeStartTime > 0 && (System.currentTimeMillis() - lapFadeStartTime) > 1000)
        {
            setRandomPositionForModel( assets.lap );
            assets.lap.setOpacity( 1 );
            lapFadeStartTime = 0;
        }
    }
    
    private void updateMine()
    {
        final float mineExplodeDistance = 2;
        
        if (mineExplodeTime == 0 && Vec3.distance( assets.car.getPosition(), assets.mine.getPosition() ) < mineExplodeDistance)
        {
            mineExplodeTime = System.currentTimeMillis();
            setRandomPositionForModel( assets.mine );
        }
        
        if (mineExplodeTime > 0)
        {
            //float carRedTint = (float)(System.currentTimeMillis() - mineExplodeTime) / 1000.0f;
            //carRedTint = 1 - carRedTint;
            assets.car.setTint( new Vec3( 1, 0, 0 ) );
        }
        
        if (mineExplodeTime > 0 && (System.currentTimeMillis() - mineExplodeTime) > 1000)
        {
            assets.car.setTint( new Vec3( 1, 1, 1 ) );
            mineExplodeTime = 0;
        }
    }

    private void setRandomPositionForModel( Model model )
    {
        float x = (float)Math.max( (float)Math.random() * 15, 10 );
        float z = (float)Math.max( (float)Math.random() * 15, 10 );
        Vec3 oldPos = model.getPosition();
        model.setPosition( new Vec3( oldPos.x + x, 0, oldPos.z + z ) );
    }

    // Returns percentage.
    private float solveTimeBarWidth()
    {
        if (lapFadeStartTime == 0)
        {
            return 1;
        }
        
        return 1 - (float)(System.currentTimeMillis() - lapFadeStartTime) / 1000.f;
    }
}
