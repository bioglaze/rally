#version 410 core

uniform mat4 uProjectionMatrix;

// .zw contains UV
layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTexcoord;
layout (location = 2) in vec3 aNormal;

out vec2 vUV;
out vec3 vNormal;

void main()
{
    gl_Position = uProjectionMatrix * vec4( aPosition + vec3( 0, 0, -7 ), 1.0 );
    vUV = aTexcoord;
    vNormal = aNormal;
}
