#version 300 es

struct Transforms { mat4 modelView; mat4 normal; mat4 projection; };
struct Vertex { vec3 position; vec3 normal; vec2 uv; };

uniform Transforms u_transforms;

in vec3 a_position;
in vec3 a_normal;
in vec2 a_uv;

out Vertex v_world;

void main() {
    vec4 transformed = u_transforms.modelView * vec4(a_position, 1.0);
    v_world.position = vec3(transformed) / transformed.w;
    v_world.normal = vec3(u_transforms.normal * vec4(a_normal, 0.0));
    v_world.uv = a_uv;
    gl_Position = u_transforms.projection * transformed;
}
