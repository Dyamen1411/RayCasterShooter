#version 430

#define PI 3.141592653589793238462643383279

#define WORLD_X_SIZE 16
#define WORLD_Y_SIZE 16
#define WORLD_NUMBER_COLOR 4

#define RENDER_DISTANCE int(ceil((sqrt(WORLD_X_SIZE*WORLD_X_SIZE + WORLD_Y_SIZE*WORLD_Y_SIZE))))

struct Player {
    float x;
    float y;
    float r;
};

layout (local_size_x = 100) in;
layout (rgba32f, binding = 0) uniform image2D img_output;

uniform int width;
uniform int height;
uniform float fov;
uniform Player player;
uniform vec3 world_colors[WORLD_NUMBER_COLOR];
uniform int world_positions[WORLD_X_SIZE * WORLD_Y_SIZE];

void main() {
    if (gl_LocalInvocationIndex >= width) {
        return;
    }

    int xc = int(width-gl_GlobalInvocationID.x);

    float px = player.x;
    float py = player.y;
    float pr = player.r + fov * ((float(xc) / float(width)) - .5f);
    
    while (pr > 2.*PI) pr -= 2.*PI;
    while (pr < 0) pr += 2.*PI;
	
	float tan_angle = tan(pr);
	
	int offset;

	float angle_state_1;
	if (0 < pr && pr < PI) {
		angle_state_1 = 0;
	} else if (PI < pr && pr < 2.* PI) {
		angle_state_1 = 1;
	} else {
		angle_state_1 = 2;
	}

	float angle_state_2;
	if (3.*PI/2. < pr || pr < PI/2.) {
		angle_state_2 = 0;
	} else if (PI/2. < pr && pr < 3.*PI/2.) {
		angle_state_2 = 1;
	} else {
		angle_state_2 = 2;
	}
	
	//-------------------------
	
	float lambda_horizontal;
	float dx_horizontal = 1. / tan_angle;
	float dy_horizontal;
	
	if (angle_state_1 == 0) {
		lambda_horizontal = ceil(py) - py;
		dy_horizontal = 1;
		offset = 0;
	} else if (angle_state_1 == 1) {
		lambda_horizontal = py - floor(py);
		dy_horizontal = -1;
		offset = 1;
	} else {
		lambda_horizontal = 0;
		dy_horizontal = 0;
		offset = 0;
	}

	if (angle_state_1 == 1) {
		dx_horizontal = -dx_horizontal;
	}
	
	float distance_horizontal;
	int block_horizontal = 0;
	
	if (dy_horizontal != 0) {
		float step_horizontal = sqrt(1 + dx_horizontal*dx_horizontal);
		float x_horizontal = px + dx_horizontal * lambda_horizontal;
		float y_horizontal = py + dy_horizontal * lambda_horizontal;
		distance_horizontal = step_horizontal * lambda_horizontal;
		
		for (int i = 0; i < RENDER_DISTANCE; ++i) {
			int x = int(floor(x_horizontal));
			int y = int(floor(y_horizontal)) - offset;
			if (x < 0 || WORLD_X_SIZE <= x || y < 0 || WORLD_Y_SIZE <= y) {
				break;
			} else {
				int index = y * WORLD_X_SIZE + x;
				block_horizontal = world_positions[index];
				
				if (block_horizontal != 0) {
					break;
				} else {
					x_horizontal += dx_horizontal;
					y_horizontal += dy_horizontal;
					distance_horizontal += step_horizontal;
				}
			}
		}
	} else {
		distance_horizontal = 64;
	}
	
	//-------------------------
	
	float lambda_vertical;
	float dx_vertical;
	float dy_vertical = tan_angle;
	
	if (angle_state_2 == 0) {
		lambda_vertical = ceil(px) - px;
		dx_vertical = 1;
		offset = 0;
	} else if (angle_state_2 == 1) {
		lambda_vertical = px - floor(px);
		dx_vertical = -1;
		offset = 1;
	} else {
		lambda_vertical = 0;
		dx_vertical = 0;
		offset = 0;
	}

	if (angle_state_2 == 1) {
		dy_vertical = -dy_vertical;
	}

	float distance_vertical;
	int block_vertical = 0;

	if (dy_vertical != 0) {
		float step_vertical = sqrt(1 + dy_vertical*dy_vertical);
		float x_vertical = px + dx_vertical * lambda_vertical;
		float y_vertical = py + dy_vertical * lambda_vertical;
		distance_vertical = step_vertical * lambda_vertical;

		for (int i = 0; i < RENDER_DISTANCE; ++i) {
			int x = int(floor(x_vertical)) - offset;
			int y = int(floor(y_vertical));
			if (x < 0 || WORLD_X_SIZE <= x || y < 0 || WORLD_Y_SIZE <= y) {
				break;
			} else {
				int index = y * WORLD_X_SIZE + x;
				block_vertical = world_positions[index];
				
				if (block_vertical != 0) {
					break;
				} else {
					x_vertical += dx_vertical;
					y_vertical += dy_vertical;
					distance_vertical += step_vertical;
				}
			}
		}
	} else {
		distance_vertical = 64;
	}
	
	//-------------------------

	int block;
	float distance;

	if (distance_horizontal < distance_vertical) {
		distance = distance_horizontal;
		block = block_horizontal;
	} else {
		distance = distance_vertical;
		block = block_vertical;
	}

	if (block == 0) {
		for (int y = 0; y < height; ++y) {
			imageStore(img_output, ivec2(xc, y), vec4(0, 0, 0, 1));
		}
	} else {
		int line_height = int(float(height + 1) / (distance * cos(pr - player.r)));
		if (line_height >= height) line_height = height - 1;
		int line_offset = (height - line_height) / 2;
		vec3 color = world_colors[block - 1];

		for (int y = 0; y < line_offset; ++y) {
			imageStore(img_output, ivec2(xc, y), vec4(0, 0, 0, 1));
			imageStore(img_output, ivec2(xc, height - y - 1), vec4(0, 0, 0, 1));
		}

		for (int y = 0; y < line_height; ++y) {
			imageStore(img_output, ivec2(xc, y + line_offset), vec4(color, 1));
		}
	}
}