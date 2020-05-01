/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.flexiblepathfinding.helpers;

import com.google.common.collect.Maps;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.WorldChangeListener;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockUri;
import org.terasology.world.internal.ChunkViewCore;
import org.terasology.world.internal.WorldInfo;
import org.terasology.world.time.WorldTime;

import java.util.Collection;
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
        groundBlock.setUri(new BlockUri("CoreBlocks:Dirt"));


        waterBlock.setPenetrable(true);
        waterBlock.setUri(new BlockUri("CoreBlocks:Water"));
        waterBlock.setLiquid(true);
        waterBlock.setWater(true);

        parseMap(map);
    }

    public static Map<Integer, Vector3i> parseExpectedPath(String[] pathData, final TestDataPojo testData) {
        Vector3i pos = Vector3i.zero();
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
        Vector3i pos = Vector3i.zero();
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
    public boolean isBlockRelevant(Vector3i pos) {
        return false;
    }

    @Override
    public boolean isBlockRelevant(Vector3ic pos) {
        return false;
    }

    @Override
    public boolean isBlockRelevant(Vector3f pos) {
        return false;
    }

    @Override
    public boolean isBlockRelevant(Vector3fc pos) {
        return false;
    }

    @Override
    public Block getBlock(Vector3f pos) {
        return getBlock((int) pos.x, (int) pos.y, (int) pos.z);
    }

    @Override
    public Block getBlock(Vector3fc pos) {
        return getBlock((int)pos.x(),(int)pos.y(),(int)pos.z());
    }

    @Override
    public Block getBlock(Vector3i pos) {
        return getBlock(pos.x, pos.y, pos.z);
    }

    @Override
    public Block getBlock(Vector3ic pos) {
        return getBlock(pos.x(), pos.y(), pos.z());
    }

    @Override
    public byte getLight(Vector3f pos) {
        return 0;
    }

    @Override
    public byte getSunlight(Vector3f pos) {
        return 0;
    }

    @Override
    public byte getTotalLight(Vector3f pos) {
        return 0;
    }

    @Override
    public byte getLight(Vector3i pos) {
        return 0;
    }

    @Override
    public byte getSunlight(Vector3i pos) {
        return 0;
    }

    @Override
    public byte getTotalLight(Vector3i pos) {
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
    public ChunkViewCore getLocalView(Vector3i chunkPos) {
        return null;
    }

    @Override
    public ChunkViewCore getWorldViewAround(Vector3i chunk) {
        return null;
    }

    @Override
    public boolean isBlockRelevant(int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isRegionRelevant(Region3i region) {
        return false;
    }

    @Override
    public Block setBlock(Vector3i pos, Block type) {
        return null;
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
    public Collection<Region3i> getRelevantRegions() {
        return null;
    }

    @Override
    public int getExtraData(int index, Vector3i pos) {
        return 0;
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
    public int getExtraData(String fieldName, Vector3i pos) {
        return 0;
    }

    @Override
    public int setExtraData(String fieldName, int x, int y, int z, int value) {
        return 0;
    }

    @Override
    public int setExtraData(String fieldName, Vector3i pos, int value) {
        return 0;
    }

    @Override
    public int getExtraData(int index, int x, int y, int z) {
        return 0;
    }

    @Override
    public int setExtraData(int index, Vector3i pos, int value) {
        return 0;
    }
}
