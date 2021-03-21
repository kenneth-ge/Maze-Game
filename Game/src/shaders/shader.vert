#version 330

layout (location = 0) in vec4 in_position;
layout (location = 1) in vec2 in_TexCoord;
layout (location = 2) in vec4 in_normal;

out vec2 texCoord;
out vec3 normal;
out vec3 fragPos;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 transformationMatrix;

void main(){
	gl_Position = projectionMatrix * viewMatrix * transformationMatrix * in_position;
	texCoord = in_TexCoord;
	normal = in_normal.xyz;
	fragPos = (transformationMatrix * in_position).xyz;
}
