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

package com.leavey.mahjong.efficiency.bean;

import com.leavey.mahjong.common.bean.Tile;
import com.leavey.mahjong.efficiency.util.Effect;
import com.leavey.mahjong.efficiency.util.EfficiencyType;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * 牌效的信息
 *
 * @author Leavey
 */
@RequiredArgsConstructor
public class EfficiencyEntry {
    /**
     * 牌效的键
     */
    private final EfficiencyKey key;
    /**
     * 可进的牌
     */
    private final Map<Tile, Integer> tiles;
    /**
     * 可进的将牌
     */
    private final Map<Tile, Integer> leaderTiles;

    private final List<Combination> combinations;

    public EfficiencyEntry() {
        this.key = new EfficiencyKey();
        this.tiles = new HashMap<>();
        this.leaderTiles = new HashMap<>();
        this.combinations = new ArrayList<>();
    }

    public void apply(Effect effect) {
        apply(effect, false);
    }

    public void revoke(Effect effect) {
        apply(effect, true);
    }

    private void apply(Effect effect, boolean isNegated) {
        EfficiencyType efficiencyType = effect.getEfficiencyType();
        int applyAmount = isNegated ? -effect.getValue() : effect.getValue();
        if (efficiencyType == EfficiencyType.GROUP) {
            key.setGroups(key.getGroups() + applyAmount);
        } else if (efficiencyType == EfficiencyType.PAIR) {
            key.setPairs(key.getPairs() + applyAmount);
        } else if (efficiencyType == EfficiencyType.LEADER_PAIR) {
            key.setLeaderPairs(key.getLeaderPairs() + applyAmount);
        } else if (efficiencyType == EfficiencyType.LEADER) {
            key.setLeaders(key.getLeaders() + applyAmount);
        } else {
            throw new IllegalStateException(efficiencyType.toString());
        }
        int additional = isNegated ? -1 : 1;
        Optional.ofNullable(effect.getTiles()).orElse(List.of())
                .forEach(tile -> {
                    int newValue = tiles.getOrDefault(tile, 0) + additional;
                    if (newValue == 0) {
                        tiles.remove(tile);
                    } else {
                        tiles.put(tile, newValue);
                    }
                });
        Optional.ofNullable(effect.getLeaderTiles()).orElse(List.of())
                .forEach(tile -> {
                    int newValue = leaderTiles.getOrDefault(tile, 0) + additional;
                    if (newValue == 0) {
                        leaderTiles.remove(tile);
                    } else {
                        leaderTiles.put(tile, newValue);
                    }
                });
        if (isNegated) {
            this.combinations.remove(effect.toCombination());
        } else {
            this.combinations.add(effect.toCombination());
        }
    }

    public boolean isValid() {
        return key.isValid();
    }

    public EfficiencyEntry copy() {
        return new EfficiencyEntry(key.copy(), new HashMap<>(tiles), new HashMap<>(leaderTiles), new ArrayList<>(combinations));
    }

    public EfficiencyEntry join(EfficiencyEntry other) {
        ArrayList<Combination> newCombinations = new ArrayList<>(combinations);
        newCombinations.addAll(other.combinations);
        return new EfficiencyEntry(key.join(other.key), combine(tiles, other.tiles), combine(leaderTiles, other.leaderTiles), newCombinations);
    }

    private static Map<Tile, Integer> combine(Map<Tile, Integer> map1, Map<Tile, Integer> map2) {
        Map<Tile, Integer> result = new HashMap<>(map1);
        map2.forEach((key, amount) -> result.compute(key, (tile, oldValue) -> oldValue == null ? amount : amount + oldValue));
        return result;
    }

}
