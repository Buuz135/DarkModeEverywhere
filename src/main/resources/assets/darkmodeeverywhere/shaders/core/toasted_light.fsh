#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

in vec2 texCoord0;

out vec4 fragColor;
float sum(vec3 v);
/*
int toFloat(int r, int g, int b);
bool isGrey(float red, float green, float blue);
*/

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a == 0.0) {
        discard;
    }
    //0.54 slot
    float threshhold = 0.54;
    float res = 0.35;
    float div = 2;
    /*
    if (color.r > threshhold && color.g > threshhold && color.b > threshhold){
        color.r = color.r / div;
        color.g = color.g / div;
        color.b = color.b / div;
    } else if (color.r < 0.34 && color.g < 0.34 && color.b < 0.34){
        color.r = color.r / div;
        color.g = color.g / div;
        color.b = color.b / div;
    }*/
    vec3 perceptionScale = vec3(0.299, 0.587, 0.114);
    float grey = sum(color.rgb * perceptionScale);
    vec3 dif = abs(color.rgb - vec3(grey));
    vec3 scaled = pow(dif, 1.0 - perceptionScale);
    float perceivedSaturation = length(scaled);
    /*
    if (perceivedSaturation <= 0.3){
        color.r = color.r / div;
        color.g = color.g / div;
        color.b = color.b / div;
    }*/
    color.rgb /= mix(div, 1.0, pow(perceivedSaturation, 0.35));
    fragColor = color * ColorModulator;
}

float sum(vec3 v) {
    return v.r + v.g + v.b;
}

/*
bool isGrey(float red, float green, float blue){
    return toFloat(red * 256, green * 256, blue * 256) - ((red * 0.3 + green*0.6 + blue*0.1) / 3);
}

int toFloat(int r, int g, int b){
    return ((r & 0xFF) << 16) |
    ((g & 0xFF) << 8)  |
    ((b & 0xFF) << 0);
}*/
