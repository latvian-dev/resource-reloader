package com.latmod.mods.resourcereloader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.fml.client.FMLClientHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class CommandReloadResources extends CommandBase
{
	private Map<String, IResourceManagerReloadListener> cache;

	@Override
	public String getName()
	{
		return "reload_resources";
	}

	@Override
	public final List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		if (args.length == 1)
		{
			List<String> list = new ArrayList<>(ResourceReloader.TYPE_MAP.keySet());
			list.addAll(getMap().keySet());
			list.sort(null);
			return getListOfStringsMatchingLastWord(args, list);
		}

		return Collections.emptyList();
	}

	public Map<String, IResourceManagerReloadListener> getMap()
	{
		if (cache != null)
		{
			return cache;
		}

		cache = new HashMap<>();

		for (IResourceManagerReloadListener listener : ((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).reloadListeners)
		{
			String id = ResourceReloader.LISTENER_NAME_MAP.get(listener.getClass());

			if (id == null)
			{
				String className = listener.getClass().getName();
				int ci = className.lastIndexOf('.');
				id = (ci < 0 ? className : className.substring(ci + 1)).replace('$', '_').toLowerCase();

				if (id.contains("/"))
				{
					id = "";
				}
			}

			if (!id.isEmpty())
			{
				cache.put(id, listener);
			}
		}

		return cache;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length < 1)
		{
			throw new WrongUsageException("command.reload_resources.usage");
		}

		if (args[0].startsWith("#"))
		{
			IResourceType type = ResourceReloader.TYPE_MAP.get(args[0].substring(1));

			if (type == null)
			{
				throw new CommandException("command.reload_resources.no_type");
			}

			long start = System.currentTimeMillis();
			FMLClientHandler.instance().refreshResources(type);
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

			ITextComponent n = new TextComponentString(args[0].substring(1));
			n.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(type.getClass().getName())));
			sender.sendMessage(new TextComponentTranslation("command.reload_resources.reloaded_type", n, t));
		}
		else
		{
			cache = null;
			IResourceManagerReloadListener listener = getMap().get(args[0]);

			if (listener == null)
			{
				throw new CommandException("command.reload_resources.no_name");
			}

			long start = System.currentTimeMillis();
			listener.onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
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

			ITextComponent n = new TextComponentString(args[0]);
			n.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(listener.getClass().getName())));
			sender.sendMessage(new TextComponentTranslation("command.reload_resources.reloaded_name", n, t));
		}
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "command.reload_resources.usage";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		return true;
	}
}