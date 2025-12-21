// EntityAccessor.java
package net.wurstclient.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor
{
    @Accessor("stuckSpeedMultiplier")
    void wurst_setStuckSpeedMultiplier(Vec3 value);
}
