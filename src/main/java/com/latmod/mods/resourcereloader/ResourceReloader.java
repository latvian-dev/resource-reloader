package com.latmod.mods.resourcereloader;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.LanguageManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.CloudRenderer;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
@Mod(
		modid = ResourceReloader.MOD_ID,
		name = ResourceReloader.MOD_NAME,
		version = ResourceReloader.VERSION,
		clientSideOnly = true,
		dependencies = "required-after:forge@[0.0.0.forge,)"
)
public class ResourceReloader
{
	public static final String MOD_ID = "resourcereloader";
	public static final String MOD_NAME = "Resource Reloader";
	public static final String VERSION = "0.0.0.resourcereloader";

	public static final Map<String, IResourceType> TYPE_MAP = new HashMap<>();
	public static final Map<Class, String> LISTENER_NAME_MAP = new HashMap<>();

	public static void addType(String name, IResourceType type)
	{
		TYPE_MAP.put("#" + name, type);
	}

	public static void addListenerMapping(Class<? extends IResourceManagerReloadListener> listener, String name)
	{
		LISTENER_NAME_MAP.put(listener, name);
	}

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		for (VanillaResourceType type : VanillaResourceType.values())
		{
			addType(type.name().toLowerCase(), type);
		}

		addListenerMapping(LanguageManager.class, "lang");
		addListenerMapping(TextureManager.class, "textures");
		addListenerMapping(SoundHandler.class, "sounds");
		addListenerMapping(ModelManager.class, "models");
		addListenerMapping(FoliageColorReloadListener.class, "foliage");
		addListenerMapping(GrassColorReloadListener.class, "grass");
		addListenerMapping(CloudRenderer.class, "clouds");
		addListenerMapping(EntityRenderer.class, "shaders");

		ClientCommandHandler.instance.registerCommand(new CommandReloadResources());
	}
}