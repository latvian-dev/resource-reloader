package com.latmod.mods.resourcereloader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static final Map<String, IResourceType> GROUP_MAP = new HashMap<>();
	public static final Map<String, Class> LISTENER_NAME_MAP = new HashMap<>();
	public static final List<String> COMMAND_TAB = new ArrayList<>();

	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{
		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();

		Map<String, String> groups = new HashMap<>();
		Map<String, String> listeners = new HashMap<>();

		for (String domain : manager.getResourceDomains())
		{
			try
			{
				for (IResource resource : Minecraft.getMinecraft().getResourceManager().getAllResources(new ResourceLocation(domain, "resource_reloader.json")))
				{
					try (InputStream stream = resource.getInputStream())
					{
						JsonElement element = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8));

						if (element instanceof JsonObject)
						{
							JsonObject json = element.getAsJsonObject();

							if (json.has("groups"))
							{
								for (Map.Entry<String, JsonElement> entry : json.get("groups").getAsJsonObject().entrySet())
								{
									groups.put(entry.getKey(), entry.getValue().getAsString());
								}
							}

							if (json.has("listeners"))
							{
								for (Map.Entry<String, JsonElement> entry : json.get("listeners").getAsJsonObject().entrySet())
								{
									listeners.put(entry.getKey(), entry.getValue().getAsString());
								}
							}
						}
					}
				}
			}
			catch (Exception ex)
			{
			}
		}

		for (Map.Entry<String, String> entry : groups.entrySet())
		{
			try
			{
				int idx = entry.getValue().lastIndexOf('.');
				Class clazz = Class.forName(entry.getValue().substring(0, idx));
				Field field = clazz.getField(entry.getValue().substring(idx + 1));
				Object object = field.get(null);

				if (object instanceof IResourceType)
				{
					GROUP_MAP.put(entry.getKey().trim().toLowerCase(), (IResourceType) object);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		for (Map.Entry<String, String> entry : listeners.entrySet())
		{
			try
			{
				Class clazz = Class.forName(entry.getValue());

				if (IResourceManagerReloadListener.class.isAssignableFrom(clazz))
				{
					LISTENER_NAME_MAP.put(entry.getKey().trim().toLowerCase(), clazz);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		COMMAND_TAB.addAll(LISTENER_NAME_MAP.keySet());

		for (String s : GROUP_MAP.keySet())
		{
			COMMAND_TAB.add('#' + s);
		}

		COMMAND_TAB.sort(null);
		COMMAND_TAB.add("$");

		LOGGER.info("Loaded " + groups.size() + " groups and " + LISTENER_NAME_MAP.size() + " reload listeners");
		ClientCommandHandler.instance.registerCommand(new CommandReloadResources());
	}
}