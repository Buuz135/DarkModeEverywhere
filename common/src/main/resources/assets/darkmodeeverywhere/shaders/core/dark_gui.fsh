#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};

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
    vec4 color = vertexColor;
    if (color.a == 0.0) {
        discard;
    }
    color.rgb = darkModeEverywhere_darken(color.rgb);
    fragColor = color * ColorModulator;
}
