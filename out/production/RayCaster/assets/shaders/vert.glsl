#version 400

layout (location=0) in vec2 position;

out vec2 outTexCoord;

void main() {
    gl_Position = vec4(position, 0.0, 1.0);
    outTexCoord = vec2((position.x + 1.f) / 2.f, (position.y + 1.f) / 2.f);
}