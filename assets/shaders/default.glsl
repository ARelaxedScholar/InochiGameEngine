#type vertex
#version 330 core
layout (location=0) in vec3 aPosition;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTextCoordinates;

uniform mat4 uProjection;
uniform mat4 uView;


out vec4 fColor;
out vec2 fTexCoordinates;

void main(){
    fColor = aColor;
    fTexCoordinates = aTextCoordinates;
    gl_Position = uProjection * uView * vec4(aPosition, 1.0);
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTexCoordinates;
uniform float uTime;
uniform sampler2D TEX_SAMPLER;
out vec4 color;

void main(){

    color = texture(TEX_SAMPLER, fTexCoordinates);
}