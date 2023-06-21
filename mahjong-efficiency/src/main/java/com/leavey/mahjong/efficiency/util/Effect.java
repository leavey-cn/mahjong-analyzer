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
import com.leavey.mahjong.efficiency.bean.Combination;
import com.leavey.mahjong.efficiency.bean.EfficiencyEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Leavey
 */
@Data
@EqualsAndHashCode
public class Effect {
    public EfficiencyType efficiencyType;
    public int value;
    //可进的牌
    public List<Tile> tiles;
    //可进的将牌
    public List<Tile> leaderTiles;

    //对手牌的影响
    public List<Tile> handTiles;

    public void applyAndRevoke(int[] tiles, EfficiencyEntry entry, Runnable callback) {
        if (canApply(tiles)) {
            applyHandTiles(tiles);
            entry.apply(this);
            callback.run();
            revokeHandTiles(tiles);
            entry.revoke(this);
        }
    }

    public Combination toCombination() {
        return new Combination(handTiles);
    }

    /**
     * 根据当前手牌的余量，判断是否存在此可能性
     *
     * @param tiles 手牌
     * @return /
     */
    private boolean canApply(int[] tiles) {
        return handTiles.stream().collect(Collectors.groupingBy(Tile::getValue, Collectors.counting()))
                .entrySet().stream().noneMatch(entry -> tiles[entry.getKey()] < entry.getValue());
    }

    private void applyHandTiles(int[] tiles) {
        if (handTiles != null) {
            handTiles.forEach(t -> tiles[t.getValue()]--);
        }
    }

    private void revokeHandTiles(int[] tiles) {
        if (handTiles != null) {
            handTiles.forEach(t -> tiles[t.getValue()]++);
        }
    }
}
