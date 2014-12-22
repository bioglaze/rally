#version 410 core

uniform mat4 uProjectionMatrix;
uniform vec4 uScaleAndTranslation;

// .zw contains UV
layout (location = 0) in vec4 aPosition;
out vec2 vUV;

void main()
{
    gl_Position = uProjectionMatrix * vec4( aPosition.xy * uScaleAndTranslation.xy + uScaleAndTranslation.zw, 0, 1 );
    vUV = aPosition.zw;
}
