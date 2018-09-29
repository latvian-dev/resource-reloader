package com.latmod.mods.resourcereloader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.command.CommandException;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class CommandRRegistry extends CommandRRBase
{
	public CommandRRegistry()
	{
		super("registry");
	}

	public Map<String, IResourceManagerReloadListener> getMap(Minecraft mc)
	{
		Map<String, IResourceManagerReloadListener> map = new HashMap<>();

		for (IResourceManagerReloadListener listener : ((SimpleReloadableResourceManager) mc.getResourceManager()).reloadListeners)
		{
			String id = ResourceReloader.classToName.get(listener.getClass());

			if (id == null)
			{
				id = listener.getClass().getSimpleName().toLowerCase();

				if (id.isEmpty())
				{
					id = listener.getClass().getName().toLowerCase();
				}

				if (id.contains("$"))
				{
					id = "";
				}
			}

			if (!id.isEmpty())
			{
				map.put(id, listener);
			}
		}

		return map;
	}

	public Collection<String> getTabCompletions(Minecraft mc, String[] args)
	{
		List<String> list = new ArrayList<>(getMap(mc).keySet());
		list.sort(String::compareToIgnoreCase);
		return list;
	}

	@Override
	public void execute(Minecraft mc, String[] args) throws CommandException
	{
		if (args.length < 1)
		{
			throw new CommandException("command.resourcereloader.registry.no_id");
		}

		IResourceManagerReloadListener listener = getMap(mc).get(args[0]);

		if (listener == null)
		{
			throw new CommandException("command.resourcereloader.registry.no_id");
		}

		long start = System.currentTimeMillis();
		listener.onResourceManagerReload(mc.getResourceManager());
		long time = System.currentTimeMillis() - start;

		String t;

		if (time < 1000L)
		{
			t = time + "ms";
		}
		else
		{
			t = String.format("%.02fs", time / 1000D);
		}

		mc.player.sendMessage(new TextComponentTranslation("command.resourcereloader.registry.reloaded", args[0], t));
	}
}