package net.winepicfin.extrabiomes.entity.client.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class TreefrogRenderState extends LivingEntityRenderState {
    // Blocks per second, as Bedrock's query.vertical_speed.
    public float verticalSpeed;
    public float swimAmount;
    public float strokePhase;
}
