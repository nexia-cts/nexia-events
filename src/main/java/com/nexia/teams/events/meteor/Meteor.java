package com.nexia.teams.events.meteor;

import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.time.ServerTime;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class Meteor {
    public static Long scheduledTimestamp = null;
    public static ServerPlayer serverPlayer = null;
    public static int[] meteorCoordinates = new int[]{0, 324, 0};

    public static void tick() {
        if (scheduledTimestamp == null) return;

        long unixTime = System.currentTimeMillis() / 1000L;
        if (unixTime == scheduledTimestamp) {
            scheduledTimestamp = null;
            spawn(serverPlayer);
        }
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static void generateRandomCoordinates() {
        int randomX = getRandomNumber(300, 2400);
        int randomZ = getRandomNumber(300, 2400);

        if (getRandomNumber(1, 3) % 2 == 0) randomX = randomX * -1;
        if (getRandomNumber(1, 3) % 2 == 0) randomZ = randomZ * -1;

        meteorCoordinates[0] = randomX;
        meteorCoordinates[2] = randomZ;
    }

    public static void spawn(ServerPlayer serverPlayer) {
        for (ServerPlayer player : ServerTime.minecraftServer.getPlayerList().getPlayers()) {
            player.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text(String.format("Meteor is landing at (%s, %s)", meteorCoordinates[0], meteorCoordinates[2])))));
        }

        Level world = serverPlayer.getCommandSenderWorld();

        LargeFireball largeFireball = new LargeFireball(EntityType.FIREBALL, world);
        largeFireball.explosionPower = 12;
        largeFireball.addTag("meteor");
        largeFireball.setPos(meteorCoordinates[0], meteorCoordinates[1], meteorCoordinates[2]);
        largeFireball.setDeltaMovement(calculateViewVector(90, 0).normalize());
        largeFireball.yPower = largeFireball.getDeltaMovement().y * 0.1;

        world.addFreshEntity(largeFireball);
    }

    public static Vec3 calculateViewVector(float f, float g) {
        float h = f * ((float)Math.PI / 180);
        float i = -g * ((float)Math.PI / 180);
        float j = Mth.cos(i);
        float k = Mth.sin(i);
        float l = Mth.cos(h);
        float m = Mth.sin(h);
        return new Vec3(k * l, -m, j * l);
    }
}
