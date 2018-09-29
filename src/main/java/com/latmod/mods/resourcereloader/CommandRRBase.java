package com.latmod.mods.resourcereloader;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public abstract class CommandRRBase extends CommandBase
{
	private final String name;

	public CommandRRBase(String n)
	{
		name = n;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.resourcereloader." + name + ".usage";
	}

	@Override
	public final List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		Collection<String> tab = getTabCompletions(Minecraft.getMinecraft(), args);
		return tab.isEmpty() ? Collections.emptyList() : getListOfStringsMatchingLastWord(args, tab);
	}

	public Collection<String> getTabCompletions(Minecraft mc, String[] args)
	{
		return Collections.emptyList();
	}

	@Override
	public final void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		execute(Minecraft.getMinecraft(), args);
	}

	public abstract void execute(Minecraft mc, String[] args) throws CommandException;
}
