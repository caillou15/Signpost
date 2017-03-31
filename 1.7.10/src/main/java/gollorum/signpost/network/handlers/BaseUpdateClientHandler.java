package gollorum.signpost.network.handlers;

import java.util.Map.Entry;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import gollorum.signpost.management.PostHandler;
import gollorum.signpost.network.messages.BaseUpdateClientMessage;
import gollorum.signpost.util.BaseInfo;
import gollorum.signpost.util.BlockPos;
import gollorum.signpost.util.DoubleBaseInfo;
import gollorum.signpost.util.StonedHashSet;

public class BaseUpdateClientHandler implements IMessageHandler<BaseUpdateClientMessage, IMessage> {

	@Override
	public IMessage onMessage(BaseUpdateClientMessage message, MessageContext ctx) {
		/*StonedHashSet toDelete = new StonedHashSet();
		toDelete.addAll(PostHandler.allWaystones);
		for (BaseInfo now : message.waystones) {
			boolean hasChanged = false;
			for (BaseInfo now2 : PostHandler.allWaystones) {
				if (now2.update(now)) {
					hasChanged = true;
					toDelete.remove(now2);
					break;
				}
			}
			if (!hasChanged) {
				PostHandler.allWaystones.add(now);
			}
		}
		PostHandler.allWaystones.removeAll(toDelete);*/
		PostHandler.allWaystones = message.waystones;
		for(Entry<BlockPos, DoubleBaseInfo> now: PostHandler.posts.entrySet()){
			BaseInfo base = now.getValue().base1;
			if(base!=null){
				now.getValue().base1 = PostHandler.allWaystones.getByPos(base.pos);
			}
			base = now.getValue().base2;
			if(base!=null){
				now.getValue().base2 = PostHandler.allWaystones.getByPos(base.pos);
			}
		}
		return null;
	}

}
