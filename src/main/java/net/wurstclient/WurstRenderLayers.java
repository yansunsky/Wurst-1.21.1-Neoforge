package net.wurstclient;

import java.util.OptionalDouble;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public enum WurstRenderLayers
{
	;

	public static final RenderType ONE_PIXEL_LINES =
			RenderType.create("wurst:1px_lines", DefaultVertexFormat.POSITION_COLOR,
					Mode.DEBUG_LINES, 1536, false, true,
					RenderType.CompositeState.builder()
							.setShaderState(RenderType.POSITION_COLOR_SHADER)
							.setLineState(
									new RenderStateShard.LineStateShard(OptionalDouble.of(1)))
							.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
							.setCullState(RenderType.NO_CULL).createCompositeState(false));

	public static final RenderType ONE_PIXEL_LINE_STRIP =
			RenderType.create("wurst:1px_line_strip",
					DefaultVertexFormat.POSITION_COLOR, Mode.DEBUG_LINE_STRIP, 1536,
					false, true,
					RenderType.CompositeState.builder()
							.setShaderState(RenderType.POSITION_COLOR_SHADER)
							.setLineState(
									new RenderStateShard.LineStateShard(OptionalDouble.of(1)))
							.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
							.setCullState(RenderType.NO_CULL).createCompositeState(false));

	public static final RenderType LINES = RenderType
			.create("wurst:lines", DefaultVertexFormat.POSITION_COLOR_NORMAL,
					VertexFormat.Mode.LINES, 1536, false, true,
					RenderType.CompositeState.builder()
							.setShaderState(RenderType.RENDERTYPE_LINES_SHADER)
							.setLineState(
									new RenderStateShard.LineStateShard(OptionalDouble.of(2)))
							.setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
							.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
							.setOutputState(RenderType.ITEM_ENTITY_TARGET)
							.setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
							.setDepthTestState(RenderType.LEQUAL_DEPTH_TEST)
							.setCullState(RenderType.NO_CULL).createCompositeState(false));

	public static final RenderType ESP_LINES = RenderType
			.create("wurst:esp_lines", DefaultVertexFormat.POSITION_COLOR_NORMAL,
					VertexFormat.Mode.LINES, 1536, false, true,
					RenderType.CompositeState.builder()
							.setShaderState(RenderType.RENDERTYPE_LINES_SHADER)
							.setLineState(
									new RenderStateShard.LineStateShard(OptionalDouble.of(2)))
							.setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
							.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
							.setOutputState(RenderType.ITEM_ENTITY_TARGET)
							.setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
							.setDepthTestState(RenderType.NO_DEPTH_TEST)
							.setCullState(RenderType.NO_CULL).createCompositeState(false));

	public static final RenderType LINE_STRIP = RenderType
			.create("wurst:line_strip", DefaultVertexFormat.POSITION_COLOR_NORMAL,
					VertexFormat.Mode.LINE_STRIP, 1536, false, true,
					RenderType.CompositeState.builder()
							.setShaderState(RenderType.RENDERTYPE_LINES_SHADER)
							.setLineState(
									new RenderStateShard.LineStateShard(OptionalDouble.of(2)))
							.setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
							.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
							.setOutputState(RenderType.ITEM_ENTITY_TARGET)
							.setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
							.setDepthTestState(RenderType.LEQUAL_DEPTH_TEST)
							.setCullState(RenderType.NO_CULL).createCompositeState(false));

	public static final RenderType ESP_LINE_STRIP =
			RenderType.create("wurst:esp_line_strip",
					DefaultVertexFormat.POSITION_COLOR_NORMAL,
					VertexFormat.Mode.LINE_STRIP, 1536, false, true,
					RenderType.CompositeState.builder()
							.setShaderState(RenderType.RENDERTYPE_LINES_SHADER)
							.setLineState(
									new RenderStateShard.LineStateShard(OptionalDouble.of(2)))
							.setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
							.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
							.setOutputState(RenderType.ITEM_ENTITY_TARGET)
							.setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
							.setDepthTestState(RenderType.NO_DEPTH_TEST)
							.setCullState(RenderType.NO_CULL).createCompositeState(false));

	public static final RenderType QUADS =
			RenderType.create("wurst:quads", DefaultVertexFormat.POSITION_COLOR,
					VertexFormat.Mode.QUADS, 1536, false, true,
					RenderType.CompositeState.builder()
							.setShaderState(RenderType.POSITION_COLOR_SHADER)
							.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
							.setDepthTestState(RenderType.LEQUAL_DEPTH_TEST)
							.createCompositeState(false));

	public static final RenderType ESP_QUADS =
			RenderType.create("wurst:esp_quads", DefaultVertexFormat.POSITION_COLOR,
					VertexFormat.Mode.QUADS, 1536, false, true,
					RenderType.CompositeState.builder()
							.setShaderState(RenderType.POSITION_COLOR_SHADER)
							.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
							.setDepthTestState(RenderType.NO_DEPTH_TEST)
							.createCompositeState(false));

	public static final RenderType ESP_QUADS_NO_CULLING =
			RenderType.create("wurst:esp_quads_no_culling",
					DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536,
					false, true,
					RenderType.CompositeState.builder()
							.setShaderState(RenderType.POSITION_COLOR_SHADER)
							.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
							.setCullState(RenderType.NO_CULL)
							.setDepthTestState(RenderType.NO_DEPTH_TEST)
							.createCompositeState(false));

	public static RenderType getQuads(boolean depthTest)
	{
		return depthTest ? QUADS : ESP_QUADS;
	}

	public static RenderType getLines(boolean depthTest)
	{
		return depthTest ? LINES : ESP_LINES;
	}

	public static RenderType getLineStrip(boolean depthTest)
	{
		return depthTest ? LINE_STRIP : ESP_LINE_STRIP;
	}
}
