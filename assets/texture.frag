#version 410

uniform sampler2D colorMap;
uniform vec4 tintColor;

in vec2 vUV;
out vec4 fragColor;

void main()
{
    //fragColor = vec4( 1.0, 0.0, 0.0, 1.0 );
    fragColor = texture( colorMap, vUV );// * tintColor;
}
