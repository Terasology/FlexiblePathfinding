// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.helpers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.world.WorldChangeListener;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockRegionc;
import org.terasology.engine.world.block.BlockUri;
import org.terasology.engine.world.internal.ChunkViewCore;
import org.terasology.engine.world.internal.WorldInfo;
import org.terasology.engine.world.time.WorldTime;

import java.util.Map;

public class MapWorldProvider implements WorldProvider {
    private static final char GROUND = ' ';
    private static final char AIR = 'X';
    private static final char WATER = '~';
    private static final char NEW_LEVEL = '|';
    private static Logger logger = LoggerFactory.getLogger(MapWorldProvider.class);
    private Map<Vector3i, Block> blocks = Maps.newHashMap();
    private Block airBlock = new Block();
    private Block groundBlock = new Block();
    private Block waterBlock = new Block();

    public MapWorldProvider(String[] map) {
        airBlock.setPenetrable(true);
        airBlock.setUri(new BlockUri("engine:air"));

        groundBlock.setPenetrable(false);
        groundBlock.setUri(new BlockUri("CoreAssets:Dirt"));


        waterBlock.setPenetrable(true);
        waterBlock.setUri(new BlockUri("CoreAssets:Water"));
        waterBlock.setLiquid(true);
        waterBlock.setWater(true);

        parseMap(map);
    }

    public static Map<Integer, Vector3i> parseExpectedPath(String[] pathData, final TestDataPojo testData) {
        Vector3i pos = new Vector3i(0, 0, 0);
        Map<Integer, Vector3i> expected = Maps.newHashMap();
        for (String line : pathData) {
            for (char c : line.toCharArray()) {
                parsePathCharacter(c, expected, testData, pos);
                switch (c) {
                    case '|':
                        pos.x = 0;
                        pos.y += 1;
                        break;
                    default:
                        pos.x += 1;
                        break;
                }
            }

            pos.z += 1;
            pos.x = 0;
            pos.y = 0;
        }

        expected.put(0, testData.start);
        expected.put(testData.expectedSize, testData.stop);
        for (int i : expected.keySet()) {
            logger.warn("{}: e {}", i, expected.get(i));
        }

        return expected;
    }

    private static void parsePathCharacter(char value, Map<Integer, Vector3i> expected, TestDataPojo testData,
                                           Vector3i pos) {
        Vector3i vec = new Vector3i(pos);
        switch (value) {
            case '?':
                testData.start = vec;
                logger.warn("Start: {}", vec);
                break;
            case '!':
                testData.stop = vec;
                logger.warn("End: {}", vec);
                break;
            default:
                int i = 0;
                if (value == '0') {
                    i = 10;
                } else if (value > '0' && value <= '9') {
                    i = value - '0';
                } else if (value >= 'a' && value <= 'z') {
                    i = value - 'a' + 10;
                } else if (value >= 'A' && value <= 'Z') {
                    i = value - 'A' + 11 + 27;
                } else {
                    break;
                }
                expected.put(i, vec);
                testData.expectedSize = i + 1;
                break;
        }
    }

    private void parseMap(String[] map) {
        Vector3i pos = new Vector3i(0, 0, 0);
        for (String line : map) {
            for (char c : line.toCharArray()) {
                Vector3i vec = new Vector3i(pos);
                switch (c) {
                    case GROUND:
                        blocks.put(vec, groundBlock);
                        break;
                    case AIR:
                        blocks.put(vec, airBlock);
                        break;
                    case WATER:
                        blocks.put(vec, waterBlock);
                        break;
                    case NEW_LEVEL:
                        pos.x = 0;
                        pos.y += 1;
                        break;
                    default:
                        break;
                }

                if (c != NEW_LEVEL) {
                    pos.x += 1;
                }
            }

            pos.z += 1;
            pos.x = 0;
            pos.y = 0;
        }
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        Vector3i vec = new Vector3i(x, y, z);
        Block result = blocks.get(vec);
        if (null == result) {
            result = groundBlock;
        }
        return result;
    }

    @Override
    public boolean isBlockRelevant(Vector3ic pos) {
        return false;
    }

    @Override
    public boolean isBlockRelevant(Vector3fc pos) {
        return false;
    }

    @Override
    public Block getBlock(Vector3fc pos) {
        return getBlock((int)pos.x(),(int)pos.y(),(int)pos.z());
    }

    @Override
    public byte getLight(Vector3fc pos) {
        return 0;
    }

    @Override
    public byte getSunlight(Vector3fc pos) {
        return 0;
    }

    @Override
    public byte getTotalLight(Vector3fc pos) {
        return 0;
    }

    @Override
    public byte getLight(Vector3ic pos) {
        return 0;
    }

    @Override
    public byte getSunlight(Vector3ic pos) {
        return 0;
    }

    @Override
    public byte getTotalLight(Vector3ic pos) {
        return 0;
    }

    @Override
    public EntityRef getWorldEntity() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSeed() {
        return null;
    }

    @Override
    public WorldInfo getWorldInfo() {
        return null;
    }

    @Override
    public void processPropagation() {

    }

    @Override
    public void registerListener(WorldChangeListener listener) {

    }

    @Override
    public void unregisterListener(WorldChangeListener listener) {

    }

    @Override
    public ChunkViewCore getLocalView(Vector3ic chunkPos) {
        return null;
    }

    @Override
    public ChunkViewCore getWorldViewAround(Vector3ic chunk) {
        return null;
    }

    @Override
    public boolean isBlockRelevant(int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isRegionRelevant(BlockRegionc region) {
        return false;
    }

    @Override
    public Block setBlock(Vector3ic pos, Block type) {
        return null;
    }

    @Override
    public byte getLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public byte getSunlight(int x, int y, int z) {
        return 0;
    }

    @Override
    public byte getTotalLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void dispose() {

    }

    @Override
    public WorldTime getTime() {
        return null;
    }

    @Override
    public ImmutableList<BlockRegionc> getRelevantRegions() {
        return null;
    }

    @Override
    public int setExtraData(int index, int x, int y, int z, int value) {
        return 0;
    }

    @Override
    public int getExtraData(String fieldName, int x, int y, int z) {
        return 0;
    }

    @Override
    public int setExtraData(String fieldName, int x, int y, int z, int value) {
        return 0;
    }

    @Override
    public int getExtraData(int index, int x, int y, int z) {
        return 0;
    }

    @Override
    public int setExtraData(int index, Vector3ic pos, int value) {
        return 0;
    }
}
