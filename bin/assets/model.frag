#version 410 core

uniform sampler2D colorMap;
uniform vec4 tintColor;

in vec2 vUV;
in vec3 vNormal;
out vec4 fragColor;

void main()
{
    //fragColor = vec4( 0.0, 0.0, 0.0, 1.0 );
    fragColor = texture( colorMap, vUV );// * tintColor;
}
