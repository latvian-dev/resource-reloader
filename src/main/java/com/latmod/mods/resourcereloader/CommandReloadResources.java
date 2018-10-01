package com.latmod.mods.resourcereloader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.fml.client.FMLClientHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CommandReloadResources extends CommandBase
{
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
			return getListOfStringsMatchingLastWord(args, ResourceReloader.COMMAND_TAB);
		}

		return Collections.emptyList();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length < 1)
		{
			throw new WrongUsageException("command.reload_resources.usage");
		}

		if (args[0].equals("$"))
		{
			if (args.length < 2)
			{
				sender.sendMessage(new TextComponentString("---"));
				HashSet<String> set = new HashSet<>();

				for (IResourceManagerReloadListener listener : ((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).reloadListeners)
				{
					String className = listener.getClass().getName();

					if (!className.contains("$$"))
					{
						set.add(className);
					}
				}

				List<String> list = new ArrayList<>(set);
				list.sort(null);

				for (String className : list)
				{
					int idx = className.lastIndexOf('.');
					ITextComponent c = new TextComponentString(className.substring(0, idx) + ".");
					c.getStyle().setColor(TextFormatting.GRAY);
					ITextComponent c2 = new TextComponentString(className.substring(idx + 1));
					c2.getStyle().setColor(TextFormatting.WHITE);
					c.appendSibling(c2);
					c.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/reload_resources $ " + className));
					sender.sendMessage(c);
				}

				sender.sendMessage(new TextComponentString("---"));
			}
			else
			{
				Class clazz = null;

				try
				{
					clazz = Class.forName(args[1]);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}

				if (clazz == null)
				{
					throw new CommandException("command.reload_resources.no_name");
				}

				long start = System.currentTimeMillis();
				IResourceManager manager = Minecraft.getMinecraft().getResourceManager();

				for (IResourceManagerReloadListener listener : ((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).reloadListeners)
				{
					if (listener.getClass() == clazz)
					{
						listener.onResourceManagerReload(manager);
					}
				}

				long time = System.currentTimeMillis() - start;
				String t = time < 1000L ? (time + "ms") : String.format("%.02fs", time / 1000D);
				sender.sendMessage(new TextComponentTranslation("command.reload_resources.reloaded_name", args[1], t));
			}
		}
		else if (args[0].startsWith("#"))
		{
			String group = args[0].substring(1).trim();
			IResourceType type = ResourceReloader.GROUP_MAP.get(group);

			if (type == null)
			{
				throw new CommandException("command.reload_resources.no_group");
			}

			long start = System.currentTimeMillis();
			FMLClientHandler.instance().refreshResources(type);
			long time = System.currentTimeMillis() - start;
			String t = time < 1000L ? (time + "ms") : String.format("%.02fs", time / 1000D);
			ITextComponent n = new TextComponentString(group);
			n.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(type.getClass().getName())));
			sender.sendMessage(new TextComponentTranslation("command.reload_resources.reloaded_group", n, t));
		}
		else
		{
			Class clazz = ResourceReloader.LISTENER_NAME_MAP.get(args[0]);

			if (clazz == null)
			{
				throw new CommandException("command.reload_resources.no_name");
			}

			long start = System.currentTimeMillis();
			IResourceManager manager = Minecraft.getMinecraft().getResourceManager();

			for (IResourceManagerReloadListener listener : ((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).reloadListeners)
			{
				if (listener.getClass() == clazz)
				{
					listener.onResourceManagerReload(manager);
				}
			}

			long time = System.currentTimeMillis() - start;
			String t = time < 1000L ? (time + "ms") : String.format("%.02fs", time / 1000D);
			ITextComponent n = new TextComponentString(args[0]);
			n.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(clazz.getName())));
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