#version 330

layout (location = 0) out vec4 out_Color;

in vec2 texCoord;
in vec3 normal;
uniform vec3 camPos;
in vec3 fragPos;

uniform sampler2D textureSampler;
uniform float randomR, randomG, randomB;
uniform float addR, addG, addB;

void main(){
	out_Color = texture(textureSampler, texCoord);

	if(out_Color.a < 0.1)
		discard;

	float dist = length(camPos - fragPos); //Light attenuates with distance to the player; the player in essence holds a lamp

	out_Color = min(out_Color, out_Color * (3/dist));
	
	out_Color.r *= randomR;//0 to 1
	out_Color.g *= randomG;
	out_Color.b *= randomB;
	
	out_Color.r += addR;
	out_Color.g += addG;
	out_Color.b += addB;
	 //this one doesn't work well

}
