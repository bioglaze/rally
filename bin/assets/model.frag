#version 410 core

uniform sampler2D colorMap;
uniform float uOpacity;
uniform vec3 uTint;

in vec2 vUV;
in vec3 vNormal;
out vec4 fragColor;

void main()
{
    fragColor = texture( colorMap, vUV ) * vec4( uTint, 1.0 );
    fragColor.a = uOpacity;
}
