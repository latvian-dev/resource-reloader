package com.latmod.mods.resourcereloader;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.CloudRenderer;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ModelDynBucket;
import net.minecraftforge.client.model.ModelFluid;
import net.minecraftforge.client.model.MultiLayerModel;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInterModComms;
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
		clientSideOnly = true
)
public class ResourceReloader
{
	public static final String MOD_ID = "resourcereloader";
	public static final String MOD_NAME = "Resource Reloader";
	public static final String VERSION = "0.0.0.resourcereloader";

	public static Map<Class, String> classToName;

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		ClientCommandHandler.instance.registerCommand(new CommandRR());

		classToName = new HashMap<>();
		addMapping(CloudRenderer.class, "clouds");
		addMapping(B3DLoader.class, "b3d_models");
		addMapping(EntityRenderer.class, "shaders");
		addMapping(ModelFluid.FluidLoader.class, "");
		addMapping(FoliageColorReloadListener.class, "foliage");
		addMapping(GrassColorReloadListener.class, "grass");
		addMapping(LanguageManager.class, "lang");
		addMapping(ItemLayerModel.Loader.class, "");
		addMapping(MultiLayerModel.Loader.class, "");
		addMapping(ModelDynBucket.LoaderDynBucket.class, "");
		addMapping(ModelManager.class, "models");
		addMapping(OBJLoader.class, "obj_models");
		addMapping(RenderGlobal.class, "block_destroy_stages");
		addMapping(RenderItem.class, "item_models");
		addMapping(SearchTreeManager.class, "search_tree");
		addMapping(SoundHandler.class, "sounds");
		addMapping(TextureManager.class, "textures");
	}

	public void addMapping(Class<? extends IResourceManagerReloadListener> clazz, String name)
	{
		classToName.put(clazz, name);
	}

	@Mod.EventHandler
	public void onIMC(FMLInterModComms.IMCEvent event)
	{
		for (FMLInterModComms.IMCMessage message : event.getMessages())
		{
			if (message.key.equals("listener_mapping"))
			{
				String[] s = message.getStringValue().split(":", 2);

				if (s.length == 2)
				{
					try
					{
						Class clazz = Class.forName(s[0]);

						if (clazz != null && IResourceManagerReloadListener.class.isAssignableFrom(clazz))
						{
							classToName.put(clazz, s[1].trim().toLowerCase());
						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
	}
}