#version 410 core

uniform mat4 uViewProjectionMatrix;
uniform mat4 uModelMatrix;

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTexcoord;
layout (location = 2) in vec3 aNormal;

out vec2 vUV;
out vec3 vNormal;

void main()
{
    gl_Position = uViewProjectionMatrix * uModelMatrix * vec4( aPosition, 1.0 );
    vUV = vec2( aTexcoord.x, 1.0 - aTexcoord.y );
    vNormal = aNormal;
}
