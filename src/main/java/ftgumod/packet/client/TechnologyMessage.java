package ftgumod.packet.client;

import ftgumod.FTGU;
import ftgumod.packet.MessageHandler;
import ftgumod.packet.server.RequestMessage;
import ftgumod.technology.CapabilityTechnology;
import ftgumod.technology.CapabilityTechnology.ITechnology;
import ftgumod.technology.Technology;
import ftgumod.technology.TechnologyHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;

public class TechnologyMessage implements IMessage {

	private Collection<String> tech;
	private boolean force;
	private Technology[] toasts;

	public TechnologyMessage() {
	}

	public TechnologyMessage(EntityPlayer player, boolean force, Technology... toasts) {
		ITechnology cap = player.getCapability(CapabilityTechnology.TECH_CAP, null);
		if (cap != null) {
			this.tech = cap.getResearched();
			this.force = force;
			this.toasts = toasts;
		} else
			throw new IllegalArgumentException();
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		force = buffer.readBoolean();

		this.tech = new HashSet<>();
		int size = buffer.readInt();
		for (int i = 0; i < size; i++)
			tech.add(ByteBufUtils.readUTF8String(buffer));

		toasts = new Technology[buffer.readInt()];
		for (int i = 0; i < toasts.length; i++)
			toasts[i] = TechnologyHandler.technologies.get(new ResourceLocation(ByteBufUtils.readUTF8String(buffer)));
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeBoolean(force);

		if (tech != null) {
			buffer.writeInt(tech.size());
			for (String s : tech)
				ByteBufUtils.writeUTF8String(buffer, s);
		} else
			buffer.writeInt(0);

		buffer.writeInt(toasts.length);
		for (Technology toast : toasts)
			ByteBufUtils.writeUTF8String(buffer, toast.getRegistryName().toString());
	}

	public static class TechnologyMessageHandler extends MessageHandler<TechnologyMessage> {

		@Override
		public IMessage handleMessage(EntityPlayer player, TechnologyMessage message, MessageContext ctx) {
			if (player == null)
				return null;

			try {
				ITechnology cap = player.getCapability(CapabilityTechnology.TECH_CAP, null);
				if (cap != null) {
					if (!message.force && cap.getResearched().size() == message.tech.size())
						return null;

					for (String name : cap.getResearched())
						if (!message.tech.contains(name)) {
							cap.removeResearched(name);

							String[] split = name.split("#");
							if (split.length == 2) {
								Technology tech = TechnologyHandler.technologies.get(new ResourceLocation(split[0]));
								TechnologyHandler.getProgress(player, tech).revokeCriterion(split[1]);
							}
						}

					for (String name : message.tech)
						if (!cap.isResearched(name)) {
							cap.setResearched(name);

							String[] split = name.split("#");
							if (split.length == 2) {
								Technology tech = TechnologyHandler.technologies.get(new ResourceLocation(split[0]));
								TechnologyHandler.getProgress(player, tech).grantCriterion(split[1]);
							}
						}

					for (Technology toast : message.toasts)
						FTGU.PROXY.displayToastTechnology(toast);

					FTGU.INSTANCE.runCompat("jei", message.tech);
				}
			} catch (ConcurrentModificationException ignore) {
				return new RequestMessage();
			}

			return null;
		}

	}

}
