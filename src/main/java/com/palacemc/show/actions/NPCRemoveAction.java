package com.palacemc.show.actions;

import com.palacemc.show.Show;
import com.palacemc.show.handlers.ShowNPC;

public class NPCRemoveAction extends ShowAction {
    public String Name;

    public NPCRemoveAction(Show show, long time, String npc) {
        super(show, time);

        Name = npc;
    }

    @Override
    public void play() {
        //Remove Old
        ShowNPC npc = show.getNPCMap().remove(Name);
        if (npc != null)
            npc.clean();

        System.out.println("Removed NPC: " + Name);
    }
}
