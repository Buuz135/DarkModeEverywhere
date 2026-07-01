#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

vec3 darkModeEverywhere_darken(vec3 rgb) {
    vec3 perceptionScale = vec3(0.2126, 0.7152, 0.0722);
    float grey = dot(rgb, perceptionScale);
    vec3 difference = abs(rgb - vec3(grey));
    vec3 scaledDifference = pow(max(difference, vec3(0.0)), vec3(1.0) - perceptionScale);
    float perceivedSaturation = length(scaledDifference);
    return rgb / mix(DME_DIVIDE_FACTOR, 1.0, pow(perceivedSaturation, 0.35));
}

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    if (color.a == 0.0) {
        discard;
    }

#ifdef DME_PREMULTIPLIED_ALPHA
    vec3 straightRgb = color.rgb / max(color.a, 0.0001);
    color.rgb = darkModeEverywhere_darken(straightRgb) * color.a;
#else
    color.rgb = darkModeEverywhere_darken(color.rgb);
#endif

    fragColor = color * ColorModulator;
}
