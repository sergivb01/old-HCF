package net.veilmc.hcf.faction.event;

<<<<<<< HEAD
=======
import net.veilmc.hcf.faction.struct.Relation;
>>>>>>> origin/new
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.veilmc.hcf.faction.struct.Relation;
import net.veilmc.hcf.faction.type.PlayerFaction;

public class FactionRelationRemoveEvent
		extends Event
		implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private final PlayerFaction senderFaction;
	private final PlayerFaction targetFaction;
	private final Relation relation;
	private boolean cancelled;

	public FactionRelationRemoveEvent(PlayerFaction senderFaction, PlayerFaction targetFaction, Relation relation){
		this.senderFaction = senderFaction;
		this.targetFaction = targetFaction;
		this.relation = relation;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}

	public PlayerFaction getSenderFaction(){
		return this.senderFaction;
	}

	public PlayerFaction getTargetFaction(){
		return this.targetFaction;
	}

	public Relation getRelation(){
		return this.relation;
	}

	public boolean isCancelled(){
		return this.cancelled;
	}

	public void setCancelled(boolean cancel){
		this.cancelled = cancel;
	}

	public HandlerList getHandlers(){
		return handlers;
	}
}

