package com.latmod.mods.resourcereloader;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * @author LatvianModder
 */
public class CommandRTexture extends CommandRRBase
{
	public CommandRTexture()
	{
		super("texture");
	}

	@Override
	public void execute(Minecraft mc, String[] args) throws CommandException
	{
		if (args.length < 1)
		{
			throw new CommandException("command.resourcereloader.texture.no_id");
		}

		mc.getTextureManager().deleteTexture(new ResourceLocation(args[0]));
		mc.player.sendMessage(new TextComponentTranslation("command.resourcereloader.texture.reloaded"));
	}
}