package com.shiliuyinyu.examplemod.entity.spawner;

import com.shiliuyinyu.examplemod.entity.ModEntities;
import com.shiliuyinyu.examplemod.entity.monster.FrostBeeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.AABB;

public class FrostBeeSpawner implements CustomSpawner {
    private static final int MIN_SPAWN_DELAY = 500;
    private static final int MAX_SPAWN_DELAY = 900;
    private static final int SPAWN_RADIUS_MIN = 24;
    private static final int SPAWN_RADIUS_MAX = 48;
    private static final int MAX_NEARBY = 3;
    private static final int GROUP_SIZE_MIN = 1;
    private static final int GROUP_SIZE_MAX = 2;

    private int nextTick;

    @Override
    public void tick(ServerLevel level, boolean spawnEnemies) {
        if (!spawnEnemies) {
            return;
        }
        if (!level.getGameRules().get(GameRules.SPAWN_MOBS)) {
            return;
        }

        this.nextTick--;
        if (this.nextTick > 0) {
            return;
        }

        RandomSource random = level.getRandom();
        this.nextTick = MIN_SPAWN_DELAY + random.nextInt(MAX_SPAWN_DELAY - MIN_SPAWN_DELAY);

        Player player = level.getRandomPlayer();
        if (player == null || player.isSpectator()) {
            return;
        }

        BlockPos playerPos = player.blockPosition();
        int attempts = 0;

        while (attempts < 10) {
            int x = playerPos.getX() + (SPAWN_RADIUS_MIN + random.nextInt(SPAWN_RADIUS_MAX - SPAWN_RADIUS_MIN))
                    * (random.nextBoolean() ? 1 : -1);
            int z = playerPos.getZ() + (SPAWN_RADIUS_MIN + random.nextInt(SPAWN_RADIUS_MAX - SPAWN_RADIUS_MIN))
                    * (random.nextBoolean() ? 1 : -1);
            int y = playerPos.getY() + 4 + random.nextInt(16) - random.nextInt(8);
            BlockPos spawnPos = new BlockPos(x, y, z);

            if (canSpawnAt(level, spawnPos, random)) {
                spawnGroup(level, spawnPos, random);
                return;
            }
            attempts++;
        }
    }

    private boolean canSpawnAt(ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.hasChunksAt(pos.getX() - 4, pos.getZ() - 4, pos.getX() + 4, pos.getZ() + 4)) {
            return false;
        }

        Biome biome = level.getBiome(pos).value();
        if (biome.getBaseTemperature() >= 0.35F) {
            return false;
        }

        int nearby = level.getEntitiesOfClass(
                FrostBeeEntity.class,
                new AABB(pos).inflate(32.0, 16.0, 32.0)
        ).size();
        if (nearby >= MAX_NEARBY) {
            return false;
        }

        BlockState blockState = level.getBlockState(pos);
        if (!NaturalSpawner.isValidEmptySpawnBlock(level, pos, blockState, blockState.getFluidState(),
                ModEntities.FROST_BEE.get())) {
            return false;
        }

        return SpawnPlacements.checkSpawnRules(
                ModEntities.FROST_BEE.get(), level, EntitySpawnReason.NATURAL, pos, random);
    }

    private void spawnGroup(ServerLevel level, BlockPos pos, RandomSource random) {
        int groupSize = GROUP_SIZE_MIN + random.nextInt(GROUP_SIZE_MAX - GROUP_SIZE_MIN + 1);

        for (int i = 0; i < groupSize; i++) {
            FrostBeeEntity bee = ModEntities.FROST_BEE.get().create(level, EntitySpawnReason.NATURAL);
            if (bee == null) {
                continue;
            }

            int offsetX = random.nextInt(4) - random.nextInt(4);
            int offsetY = random.nextInt(3) - random.nextInt(3);
            int offsetZ = random.nextInt(4) - random.nextInt(4);
            BlockPos spawnPos = pos.offset(offsetX, offsetY, offsetZ);

            if (!NaturalSpawner.isValidEmptySpawnBlock(level, spawnPos,
                    level.getBlockState(spawnPos), level.getBlockState(spawnPos).getFluidState(),
                    ModEntities.FROST_BEE.get())) {
                bee.discard();
                continue;
            }

            bee.snapTo(spawnPos, 0.0F, 0.0F);
            bee.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.NATURAL, null);
            level.addFreshEntityWithPassengers(bee);
        }
    }
}
