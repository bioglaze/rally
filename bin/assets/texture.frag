#version 410 core

uniform sampler2D colorMap;
uniform vec4 uTint;

in vec2 vUV;
out vec4 fragColor;

void main()
{
    fragColor = texture( colorMap, vUV ) * uTint;
}
