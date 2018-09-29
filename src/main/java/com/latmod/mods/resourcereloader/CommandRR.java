package com.latmod.mods.resourcereloader;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CommandRR extends CommandTreeBase
{
	public CommandRR()
	{
		addSubcommand(new CommandRRegistry());
		addSubcommand(new CommandRTexture());
	}

	@Override
	public String getName()
	{
		return "resourcereloader";
	}

	@Override
	public List<String> getAliases()
	{
		return Collections.singletonList("rreload");
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "command.resourcereloader.usage";
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