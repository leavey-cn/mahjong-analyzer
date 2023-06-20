/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leavey.mahjong.efficiency.util;

import com.leavey.mahjong.common.bean.Tile;
import com.leavey.mahjong.common.exception.ErrorTileException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Leavey
 */
public class PossibilityEffect {

    /**
     * 计算一张牌可能的成牌影响
     * <p>
     * 这张牌进作为最小牌分析
     *
     * @param tile      牌
     * @param canLeader 判断牌是否可以作为将牌
     * @return /
     */
    public static List<Effect> possibleEffects(Tile tile, boolean canLeader) {
        return Stream.of(
                        analyzeSameGroup(tile),
                        analyzeDiffGroup(tile),
                        canLeader ? analyzeLeader(tile) : null,
                        pairsXX(tile),
                        pairsXX1(tile),
                        pairsXX2(tile),
                        pairsXX2X4(tile),
                        pairsXXX1(tile),
                        pairsXXX2(tile),
                        pairsXX1X1(tile),
                        canLeader ? analyzeLeaderPairs(tile) : null
                ).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static Effect analyzeSameGroup(Tile tile) {
        Effect effect = new Effect();
        effect.setEfficiencyType(EfficiencyType.GROUP);
        effect.setValue(1);
        effect.setHandTiles(List.of(tile, tile, tile));
        return effect;
    }

    private static Effect analyzeDiffGroup(Tile tile) {
        try {
            Effect effect = new Effect();
            effect.efficiencyType = EfficiencyType.GROUP;
            effect.value = 1;
            effect.handTiles = List.of(tile, tile.next(), tile.next().next());
            return effect;
        } catch (ErrorTileException e) {
            return null;
        }
    }

    private static Effect analyzeLeader(Tile tile) {
        Effect effect = new Effect();
        effect.efficiencyType = EfficiencyType.LEADER;
        effect.value = 1;
        effect.handTiles = List.of(tile, tile);
        return effect;
    }

    private static Effect pairsXX(Tile tile) {
        Effect effect = new Effect();
        effect.efficiencyType = EfficiencyType.PAIR;
        effect.value = 1;
        effect.handTiles = List.of(tile, tile);
        effect.tiles = List.of(tile);
        return effect;
    }

    private static Effect pairsXX1(Tile tile) {
        try {
            Effect effect = new Effect();
            effect.efficiencyType = EfficiencyType.PAIR;
            effect.value = 1;
            effect.handTiles = List.of(tile, tile.next());
            effect.tiles = List.of(tile.prev(), tile.next().next());
            return effect;
        } catch (ErrorTileException e) {
            return null;
        }
    }

    private static Effect pairsXX2(Tile tile) {
        try {
            Effect effect = new Effect();
            effect.efficiencyType = EfficiencyType.PAIR;
            effect.value = 1;
            effect.handTiles = List.of(tile, tile.next().next());
            effect.tiles = List.of(tile.next());
            return effect;
        } catch (ErrorTileException e) {
            return null;
        }
    }

    private static Effect pairsXX2X4(Tile tile) {
        try {
            Effect effect = new Effect();
            effect.efficiencyType = EfficiencyType.PAIR;
            effect.value = 1;
            effect.handTiles = List.of(tile, tile.next().next(), tile.next().next().next().next());
            effect.tiles = List.of(tile.next(), tile.next().next().next());
            return effect;
        } catch (ErrorTileException e) {
            return null;
        }
    }

    private static Effect pairsXXX1(Tile tile) {
        try {
            Effect effect = new Effect();
            effect.efficiencyType = EfficiencyType.PAIR;
            effect.value = 1;
            effect.handTiles = List.of(tile, tile, tile.next());
            effect.tiles = List.of(tile.prev(), tile, tile.next().next());
            return effect;
        } catch (ErrorTileException e) {
            return null;
        }
    }

    private static Effect pairsXXX2(Tile tile) {
        try {
            Effect effect = new Effect();
            effect.efficiencyType = EfficiencyType.PAIR;
            effect.value = 1;
            effect.handTiles = List.of(tile, tile, tile.next().next());
            effect.tiles = List.of(tile, tile.next());
            return effect;
        } catch (ErrorTileException e) {
            return null;
        }
    }

    private static Effect pairsXX1X1(Tile tile) {
        try {
            Effect effect = new Effect();
            effect.efficiencyType = EfficiencyType.PAIR;
            effect.value = 1;
            effect.handTiles = List.of(tile, tile.next(), tile.next());
            effect.tiles = List.of(tile.prev(), tile.next(), tile.next().next());
            return effect;
        } catch (ErrorTileException e) {
            return null;
        }
    }

    private static Effect analyzeLeaderPairs(Tile tile) {
        Effect effect = new Effect();
        effect.efficiencyType = EfficiencyType.LEADER_PAIR;
        effect.value = 1;
        effect.handTiles = List.of(tile);
        effect.tiles = List.of(tile);
        return effect;
    }
}
