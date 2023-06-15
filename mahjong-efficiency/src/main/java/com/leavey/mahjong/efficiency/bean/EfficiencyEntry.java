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
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private final Set<Tile> tiles;
    /**
     * 可进的将牌
     */
    private final Set<Tile> leaderTiles;

    public EfficiencyEntry() {
        this.key = new EfficiencyKey();
        this.tiles = new HashSet<>();
        this.leaderTiles = new HashSet<>();
    }

    /**
     * 添加一张有效进张牌
     */
    public void addEffectiveTile(Tile tile) {
        tiles.add(tile);
    }

    /**
     * 添加一张有效进张将牌
     */
    public void addEffectiveLeaderTile(Tile tile) {
        leaderTiles.add(tile);
    }

    /**
     * 清除掉单张将牌的分析
     */
    public void clearExistSingleLeader() {
        key.setExistSingleLeader(false);
        leaderTiles.clear();
    }

    public EfficiencyKey getKey() {
        return key;
    }

    public EfficiencyEntry copy() {
        return new EfficiencyEntry(key.copy(), new HashSet<>(tiles), new HashSet<>(leaderTiles));
    }
}
