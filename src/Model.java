/**
   @author Timo Wiren
   @date 2014-12-18
 */
import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Model
{
    private int vao;
    private int vbo;
    private int ibo;

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

    // Contains indices to an array of Vertex elements.
    private class Triangle
    {
        int[] indices = new int[ 3 ];
    }
    
    // Vertex that's used in OpenGL buffer object.
    private class Vertex
    {
        Vec3 position;
        Vec3 texcoord;
        Vec3 normal;
    }
    
    // .obj contains separate arrays for each element.
    private class Face
    {
        int[] vertexIndices = new int[ 3 ];
        int[] tcoordIndices = new int[ 3 ];
        int[] normalIndices = new int[ 3 ];
    }
    
    public void Draw()
    {
        glBindVertexArray( vao );
        //glDrawElements( GL_TRIANGLES, 0, );
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

                // .obj indices are one-based, so substracting 1 from all indices.
                
                String[] firstIndexTokens = tokens[ 0 ].split( " " );
                face.vertexIndices[ 0 ] = Integer.parseInt( firstIndexTokens[ 1 ] ) - 1;
                face.tcoordIndices[ 0 ] = Integer.parseInt( tokens[ 1 ] ) - 1;
                
                String[] secondIndexTokens = tokens[ 2 ].split( " " );
                face.normalIndices[ 0 ] = Integer.parseInt( secondIndexTokens[ 0 ] ) - 1;

                face.vertexIndices[ 1 ] = Integer.parseInt( secondIndexTokens[ 1 ] ) - 1;
                face.tcoordIndices[ 1 ] = Integer.parseInt( tokens[ 3 ] ) - 1;

                String[] fourthIndexTokens = tokens[ 4 ].split( " " );
                face.normalIndices[ 1 ] = Integer.parseInt( fourthIndexTokens[ 0 ] ) - 1;

                face.vertexIndices[ 2 ] = Integer.parseInt( fourthIndexTokens[ 1 ] ) - 1;
                face.tcoordIndices[ 2 ] = Integer.parseInt( tokens[ 5 ] ) - 1;
                face.normalIndices[ 2 ] = Integer.parseInt( tokens[ 6 ] ) - 1;
                
                faces.add( face );
            }

            ++lineNo;
        }
        
        ArrayList< Vertex > outVertices = new ArrayList< Vertex >();
        ArrayList< Triangle > outTriangles = new ArrayList< Triangle >();

        interleave( vertices, texCoords, normals, faces, outVertices, outTriangles );
        createVBO( outVertices, outTriangles );
    }
    
    private void addVertex( ArrayList< Vec3 > vertices,
                            ArrayList< Vec3 > texcoords,
                            ArrayList< Vec3 > normals,
                            ArrayList< Face > faces,
                            int faceIndex,
                            int vertexIndex,
                            Triangle newFace,
                            ArrayList< Vertex > outVertices,
                            ArrayList< Triangle > outTriangles )
    {
        Vec3 vertex = vertices.get( faces.get( faceIndex ).vertexIndices[ vertexIndex ] );
        Vec3 normal = normals.get( faces.get( faceIndex ).normalIndices[ vertexIndex ] );
        Vec3 tcoord = texcoords.get( faces.get( faceIndex ).tcoordIndices[ vertexIndex ] );
        
        boolean found = false;
 
        for (int f = 0; f < outTriangles.size(); ++f)
        {
            if (outVertices.get( outTriangles.get( f ).indices[ vertexIndex ] ).position.equals( vertex ) &&
                outVertices.get( outTriangles.get( f ).indices[ vertexIndex ] ).texcoord.equals( tcoord ) &&
                outVertices.get( outTriangles.get( f ).indices[ vertexIndex ] ).normal.equals( normal ))
            {
                found = true;
                newFace.indices[ vertexIndex ] = f;
                break;
            }
        }
        
        if (!found)
        {
            Vertex newVertex = new Vertex();
            newVertex.position = vertex;
            newVertex.normal = normal;
            newVertex.texcoord = tcoord;
            newFace.indices[ vertexIndex ] = outVertices.size();
            outVertices.add( newVertex );
        }
    }
    
    private void interleave( ArrayList< Vec3 > vertices,
                             ArrayList< Vec3 > texcoords,
                             ArrayList< Vec3 > normals,
                             ArrayList< Face > faces,
                             ArrayList< Vertex > outVertices,
                             ArrayList< Triangle > outTriangles )
    {
        for (int f = 0; f < faces.size(); ++f)
        {
            Triangle newTriangle = new Triangle();
            addVertex( vertices, texcoords, normals, faces, f, 0, newTriangle, outVertices, outTriangles );
            addVertex( vertices, texcoords, normals, faces, f, 1, newTriangle, outVertices, outTriangles );
            addVertex( vertices, texcoords, normals, faces, f, 2, newTriangle, outVertices, outTriangles );
            outTriangles.add( newTriangle );
        }
    }
    
    private void createVBO( ArrayList< Vertex > vertices, ArrayList< Triangle > triangles )
    {
        vao = glGenVertexArrays();
        glBindVertexArray( vao );

        int floatsInVertex = 3 + 2 + 3;
        float[] vertexBuf = new float[ vertices.size() * floatsInVertex ];
        int i = 0;
        
        for (int v = 0; v < vertices.size(); ++v)
        {
            vertexBuf[ i + 0 ] = vertices.get( v ).position.x;
            vertexBuf[ i + 1 ] = vertices.get( v ).position.y;
            vertexBuf[ i + 2 ] = vertices.get( v ).position.z;

            vertexBuf[ i + 3 ] = vertices.get( v ).texcoord.x;
            vertexBuf[ i + 4 ] = vertices.get( v ).texcoord.y;

            vertexBuf[ i + 5 ] = vertices.get( v ).normal.x;
            vertexBuf[ i + 6 ] = vertices.get( v ).normal.y;
            vertexBuf[ i + 7 ] = vertices.get( v ).normal.z;

            i += floatsInVertex;
        }
        
        FloatBuffer positionBuffer = BufferUtils.createFloatBuffer( vertexBuf.length );
        positionBuffer.put( vertexBuf );
        positionBuffer.flip();
        
        vbo = glGenBuffers();
        glBindBuffer( GL_ARRAY_BUFFER, vbo );
        glBufferData( GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW );
        glEnableVertexAttribArray( 0 );
        glVertexAttribPointer( 0, 3, GL_FLOAT, false, 0, 0 );
        glVertexAttribPointer( 1, 2, GL_FLOAT, false, 0, 3 * 4 );
        glVertexAttribPointer( 2, 3, GL_FLOAT, false, 0, 5 * 4 );
        
        short[] indexBuf = new short[ triangles.size() * 3 ];
        i = 0;
        
        for (int f = 0; f < triangles.size(); ++f)
        {
            indexBuf[ i + 0 ] = (short)triangles.get( f ).indices[ 0 ];
            indexBuf[ i + 1 ] = (short)triangles.get( f ).indices[ 1 ];
            indexBuf[ i + 2 ] = (short)triangles.get( f ).indices[ 2 ];
            i += 3;
        }
        
        ShortBuffer indexBuffer = BufferUtils.createShortBuffer( indexBuf.length );
        indexBuffer.put( indexBuf );
        indexBuffer.flip();

        ibo = glGenBuffers();
        glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, ibo );
        glBufferData( GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW );
    }
}
