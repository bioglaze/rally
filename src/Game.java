/**
   @author Timo Wiren
   @date 2015-01-05
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
    private long timeBarAnimationStartTime;
    
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
        updateTrackPosition();
        updateCamera();
        updateLap();
        updateMine();
        updateCompass();
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
        if (mineExplodeTime == 0)
        {
            Vec3 rot = assets.car.getRotation();
            float newRotY = assets.car.getRotation().y + carDirection.y;
            assets.car.setRotation( new Vec3( rot.x, newRotY, rot.z ) );
        }
        
        signSpeed *= 0.9f;
        
        if (mineExplodeTime == 0 && isAccelerating || isReversing)
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
            timeBarAnimationStartTime = System.currentTimeMillis();
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
            setRandomPositionForModel( assets.mines[ 0 ] );
            setRandomPositionForModel( assets.mines[ 1 ] );
            setRandomPositionForModel( assets.mines[ 2 ] );

            assets.lap.setOpacity( 1 );
            lapFadeStartTime = 0;
        }
    }
    
    private void updateMine()
    {
        final float mineExplodeDistance = 2;
        
        for (int i = 0; i < assets.mines.length; ++i)
        {
            if (mineExplodeTime == 0 && Vec3.distance( assets.car.getPosition(), assets.mines[ i ].getPosition() ) < mineExplodeDistance)
            {
                mineExplodeTime = System.currentTimeMillis();
                setRandomPositionForModel( assets.mines[ i ] );
            }
        }
        
        if (mineExplodeTime > 0)
        {
            assets.car.setTint( new Vec3( 1, 0, 0 ) );
        }
        
        if (mineExplodeTime > 0 && (System.currentTimeMillis() - mineExplodeTime) > 1000)
        {
            assets.car.setTint( new Vec3( 1, 1, 1 ) );
            mineExplodeTime = 0;
        }
    }

    private void updateTrackPosition()
    {
        assets.track.setPosition( Vec3.add( assets.car.getPosition(), new Vec3( -2, 5, -45 ) ) );
        assets.track.setTexTranslation( -assets.car.getPosition().z / 55.0f, assets.car.getPosition().x / 55.0f);
    }
    
    private void updateCompass()
    {
        Vec3 pos = Vec3.add( assets.car.getPosition(), new Vec3( -3, 10, 8 ));
        assets.compass.setPosition( pos );
        
        Vec3 dirFromCompassToLap = Vec3.subtract( assets.lap.getPosition(), assets.compass.getPosition() ).normalized();
        float angleRad = (float)Math.atan2( (double)dirFromCompassToLap.z, (double)dirFromCompassToLap.x );
        assets.compass.setRotation( new Vec3( 0, (float)Math.toDegrees( angleRad ), 0 ) );
    }
    
    private void setRandomPositionForModel( Model model )
    {
        float x = (float)Math.random() * 10 - (float)Math.random() * 10;
        float z = (float)Math.random() * 10 - (float)Math.random() * 10;
        x *= Math.max( x, 5 );
        z *= Math.max( z, 5 );
        Vec3 oldPos = assets.lap.getPosition();
        model.setPosition( new Vec3( oldPos.x + x, 0, oldPos.z + z ) );
    }

    // Returns a percentage.
    private float solveTimeBarWidth()
    {
        return 1 - lerp( 0, 1, 4, (System.currentTimeMillis() - timeBarAnimationStartTime) / 1000.0f );
    }
    
    public static float lerp( float start, float target, float duration, float timeSinceStart )
    {
        float value = start;

        if (timeSinceStart > 0.0f && timeSinceStart < duration)
        {
            final float range = target - start;
            final float percent = timeSinceStart / duration;
            value = start + (range * percent);
        }
        else if (timeSinceStart >= duration)
        {
            value = target;
        }

        return value;
    }
}
