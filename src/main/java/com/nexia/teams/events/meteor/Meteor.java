package com.nexia.teams.events.meteor;

import com.mojang.math.Transformation;
import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.time.ServerTime;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

        Display.BlockDisplay blockDisplay = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, world);
        blockDisplay.setBlockState(Blocks.SMOOTH_BASALT.defaultBlockState());
        Transformation transformation = new Transformation(new Vector3f(-2, -4, -2), new Quaternionf(0, 0, 0, 1), new Vector3f(4, 4, 4), new Quaternionf(0, 0, 0, 1));
        blockDisplay.setTransformation(transformation);
        blockDisplay.setPos(meteorCoordinates[0], meteorCoordinates[1], meteorCoordinates[2]);

        ArmorStand armorStand = new ArmorStand(world, meteorCoordinates[0], meteorCoordinates[1], meteorCoordinates[2]);
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);

        LargeFireball largeFireball = new LargeFireball(EntityType.FIREBALL, world);
        largeFireball.explosionPower = 20;
        largeFireball.setPos(meteorCoordinates[0], meteorCoordinates[1], meteorCoordinates[2]);
        largeFireball.yPower = -1.35;

        world.addFreshEntity(blockDisplay);
        world.addFreshEntity(armorStand);
        world.addFreshEntity(largeFireball);

        blockDisplay.startRiding(armorStand);
        armorStand.startRiding(largeFireball);
    }
}
