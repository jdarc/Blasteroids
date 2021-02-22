#version 300 es

struct Transforms { mat4 world; mat4 normal; mat4 view; mat4 projection; };
struct Vertex { vec3 position; vec3 normal; vec2 uv; };

uniform Transforms u_transforms;

in vec3 a_position;
in vec3 a_normal;
in vec2 a_uv;

out Vertex v_world;

void main() {
    vec4 transformed = u_transforms.world * vec4(a_position, 1.0);

    v_world.position = vec3(transformed);
    v_world.normal = vec3(u_transforms.normal * vec4(a_normal, 0.0));
    v_world.uv = a_uv;

    gl_Position = u_transforms.projection * u_transforms.view * transformed;
}
