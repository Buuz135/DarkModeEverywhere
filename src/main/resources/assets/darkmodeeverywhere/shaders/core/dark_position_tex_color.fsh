#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform vec3 PerceptionScale;
uniform float DivideFactor;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float sum(vec3 v);
void vanillaPreProcess();
void vanillaPostProcess();
void modMixin();

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    if (color.a == 0.0) {
        discard;
    }

    float grey = sum(color.rgb * PerceptionScale);
    vec3 dif = abs(color.rgb - vec3(grey));
    vec3 scaled = pow(dif, 1.0 - PerceptionScale);
    float perceivedSaturation = length(scaled);
    color.rgb /= mix(DivideFactor, 1.0, pow(perceivedSaturation, 0.35));

    fragColor = color * ColorModulator;
}

float sum(vec3 v) {
    return v.r + v.g + v.b;
}
