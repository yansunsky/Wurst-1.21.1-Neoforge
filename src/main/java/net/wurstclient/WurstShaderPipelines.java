package net.wurstclient;

import net.minecraft.client.renderer.RenderStateShard;

public enum WurstShaderPipelines {
	;

	public static final RenderStateShard.ShaderStateShard DEPTH_TEST_LINES = RenderStateShard.RENDERTYPE_LINES_SHADER;
	public static final RenderStateShard.ShaderStateShard ESP_LINES = RenderStateShard.RENDERTYPE_LINES_SHADER;

	public static final RenderStateShard.ShaderStateShard QUADS = RenderStateShard.POSITION_COLOR_SHADER;
	public static final RenderStateShard.ShaderStateShard ESP_QUADS = RenderStateShard.POSITION_COLOR_SHADER;
	public static final RenderStateShard.ShaderStateShard ESP_QUADS_NO_CULLING = RenderStateShard.POSITION_COLOR_SHADER;
}
