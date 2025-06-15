package net.bxx2004.netzach.mixin;

import net.bxx2004.netzach.resources.GifTexture;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * @author 6hisea
 * @date 2025/6/14 10:08
 * @description: None
 */

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {
    @Shadow @Final private Map<ResourceLocation, AbstractTexture> byPath;

    @Shadow public abstract void register(ResourceLocation path, AbstractTexture texture);

    @Inject(cancellable = true,at=@At("HEAD"),method = "getTexture(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/texture/AbstractTexture;")
    public void getTexture(ResourceLocation path, CallbackInfoReturnable<AbstractTexture> cir){
        if (path.getPath().endsWith(".gif")){
            AbstractTexture abstracttexture = this.byPath.get(path);
            if (abstracttexture == null) {
                abstracttexture = new GifTexture(path);
                this.register(path, abstracttexture);
            }
            cir.setReturnValue(abstracttexture);
        }
    }
}
