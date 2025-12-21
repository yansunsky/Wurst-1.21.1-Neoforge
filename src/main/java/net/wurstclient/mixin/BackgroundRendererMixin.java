package net.wurstclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.material.FogType;
import net.wurstclient.WurstClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class BackgroundRendererMixin
{
	// ★ Invoker 완전히 제거

	@Inject(
			at = @At("HEAD"),
			method = "setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZF)V"
	)
	private static void onApplyFog(Camera camera,
								   FogRenderer.FogMode fogMode,
								   float viewDistance,
								   boolean thickFog,
								   float tickDelta,
								   CallbackInfo ci)
	{
		// NoFog 모드가 꺼져있거나, 지형 안개가 아니면 건드리지 않음
		if(!WurstClient.INSTANCE.getHax().noFogHack.isEnabled()
				|| fogMode != FogRenderer.FogMode.FOG_TERRAIN)
			return;

		FogType fogType = camera.getFluidInCamera();
		if(fogType != FogType.NONE)
			return;

		Entity entity = camera.getEntity();

		// 원래: if (getPriorityFogFunction(entity, tickDelta) != null) return;
		// 대체: 실명/어둠 효과가 있으면 우선순위 안개 활성 상태로 보고 그냥 리턴
		if(entity instanceof LivingEntity living)
		{
			if(living.hasEffect(MobEffects.BLINDNESS)
					|| living.hasEffect(MobEffects.DARKNESS))
				return;
		}

		// 안개 완전히 제거
		RenderSystem.setShaderFogColor(0, 0, 0, 0);
	}

	@Inject(
			at = @At("HEAD"),
			method = "getPriorityFogFunction(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/FogRenderer$MobEffectFogFunction;",
			cancellable = true
	)
	private static void onGetFogModifier(Entity entity,
										 float tickDelta,
										 CallbackInfoReturnable<Object> ci)
	{
		// AntiBlind 켜져 있으면 기존 MobEffectFogFunction을 아예 null로 덮어버리기
		if(WurstClient.INSTANCE.getHax().antiBlindHack.isEnabled())
			ci.setReturnValue(null);
	}
}
