package com.palacemc.show.actions;

import com.palacemc.show.Show;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.PlayerData;

import java.util.UUID;

/**
 * Created by Marc on 1/10/15
 */
public class ParticleAction extends ShowAction {
    public ParticleEffect effect;
    public Location loc;
    public double offsetX;
    public double offsetY;
    public double offsetZ;
    public float speed;
    public int amount;
    private Show show;

    public ParticleAction(Show show, long time, ParticleEffect effect, Location loc, double offsetX, double offsetY,
                          double offsetZ, float speed, int amount) {
        super(show, time);
        this.show = show;
        this.effect = effect;
        this.loc = loc;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.amount = amount;
    }

    @Override
    public void play() {
        for (UUID uuid : show.getNearPlayers()) {
            Player tp = Bukkit.getPlayer(uuid);
            if (tp == null) {
                continue;
            }
            if (tp.getLocation().distance(loc) > 50) {
                continue;
            }
            PlayerData data = ParkManager.getPlayerData(tp.getUniqueId());
            if (!data.getFlash()) {
                continue;
            }
            ParticleUtil.spawnParticleForPlayer(effect, loc, (float) offsetX, (float) offsetY, (float) offsetZ, speed,
                    amount, tp);
        }
    }
}
