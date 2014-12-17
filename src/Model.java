import java.util.ArrayList;

public class Model
{
    private class Vec3
    {
        public float x, y, z;

        public Vec3( float ax, float ay, float az )
        {
            x = ax;
            y = ay;
            z = az;
        }
    }
    
    private class Face
    {
        int[] vertexIndices = new int[ 3 ];
        int[] tcoordIndices = new int[ 3 ];
        int[] normalIndices = new int[ 3 ];
    }
    
    /**
      Limitations: Supports only one mesh and it must contain position, texcoords and normals.
                   Faces must be triangulated.

      Tested only on models exported from Blender 2.7x.

      @param objContents .obj file contents in lines.
    */
    public void initFromObjFileContents( String[] objContents )
    {
        int lineNo = 0;
        
        ArrayList< Vec3 > vertices = new ArrayList< Vec3 >();
        ArrayList< Vec3 > texCoords = new ArrayList< Vec3 >();
        ArrayList< Vec3 > normals = new ArrayList< Vec3 >();
        ArrayList< Face > faces = new ArrayList< Face >();
        
        while (lineNo < objContents.length)
        {
            //System.out.println( objContents[ lineNo ].substring( 0, 2 )  );
            String line = objContents[ lineNo ];
            
            if (line.substring( 0, 2 ).equals( "o " ))
            {
                System.out.println( "Found mesh in .obj." );
            }
            else if (line.substring( 0, 2 ).equals( "v " ))
            {
                String[] tokens = line.split( " " );
                
                float x = Float.parseFloat( tokens[ 1 ] );
                float y = Float.parseFloat( tokens[ 2 ] );
                float z = Float.parseFloat( tokens[ 3 ] );
                Vec3 v = new Vec3( x, y, z );
                vertices.add( v );
            }
            else if (line.substring( 0, 2 ).equals( "vt" ))
            {
                String[] tokens = line.split( " " );
                
                float u = Float.parseFloat( tokens[ 1 ] );
                float v = Float.parseFloat( tokens[ 2 ] );
                Vec3 texCoord = new Vec3( u, v, 0 );
                texCoords.add( texCoord );
            }
            else if (line.substring( 0, 2 ).equals( "vn" ))
            {
                String[] tokens = line.split( " " );
                float x = Float.parseFloat( tokens[ 1 ] );
                float y = Float.parseFloat( tokens[ 2 ] );
                float z = Float.parseFloat( tokens[ 3 ] );
                Vec3 normal = new Vec3( x, y, z );
                normals.add( normal );
            }
            else if (line.substring( 0, 2 ).equals( "f " ))
            {
                String[] tokens = line.split( "/" );
                Face face = new Face();
                
                //for (int i = 0; i < tokens.length; ++i)
                //    System.out.println( "face token " + i + ": " + tokens[i] );

                String[] firstIndexTokens = tokens[ 0 ].split( " " );
                face.vertexIndices[ 0 ] = Integer.parseInt( firstIndexTokens[ 1 ] );
                face.tcoordIndices[ 0 ] = Integer.parseInt( tokens[ 1 ] );
                
                String[] secondIndexTokens = tokens[ 2 ].split( " " );
                face.normalIndices[ 0 ] = Integer.parseInt( secondIndexTokens[ 0 ] );

                face.vertexIndices[ 1 ] = Integer.parseInt( secondIndexTokens[ 1 ] );
                face.tcoordIndices[ 1 ] = Integer.parseInt( tokens[ 3 ] );

                String[] fourthIndexTokens = tokens[ 4 ].split( " " );
                face.normalIndices[ 1 ] = Integer.parseInt( fourthIndexTokens[ 0 ] );

                face.vertexIndices[ 2 ] = Integer.parseInt( fourthIndexTokens[ 1 ] );
                face.tcoordIndices[ 2 ] = Integer.parseInt( tokens[ 5 ] );
                face.normalIndices[ 2 ] = Integer.parseInt( tokens[ 6 ] );
                
                faces.add( face );
            }

            ++lineNo;
        }
        
        // TODO: Convert faces into index format supported by OpenGL.
    }
}
