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
import com.leavey.mahjong.common.bean.Type;
import com.leavey.mahjong.efficiency.bean.EfficiencyEntry;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Leavey
 */
@Data
public class Effect {
    private EfficiencyType efficiencyType;
    private int value;
    //可进的牌
    private List<Tile> tiles;
    //可进的将牌
    private List<Tile> leaderTiles;

    //对手牌的影响
    private List<Tile> handTiles;

    public void applyAndRevoke(int[] tiles, EfficiencyEntry entry, Runnable callback) {
        applyHandTiles(tiles);
        entry.apply(this);
        callback.run();
        revokeHandTiles(tiles);
        entry.revoke(this);
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


    public static Effect sameGroup(Tile tile) {
        Effect effect = new Effect();
        effect.efficiencyType = EfficiencyType.GROUP;
        effect.value = 1;
        effect.handTiles = List.of(tile, tile, tile);
        return effect;
    }

    public static Effect diffGroup(Tile tile) {
        Effect effect = new Effect();
        effect.efficiencyType = EfficiencyType.GROUP;
        effect.value = 1;
        effect.handTiles = List.of(tile, tile.next(), tile.next().next());
        return effect;
    }

    public static Effect leaderPair(Tile tile) {
        Effect effect = new Effect();
        effect.efficiencyType = EfficiencyType.LEADER_PAIR;
        effect.value = 1;
        effect.handTiles = List.of(tile, tile);
        return effect;
    }
}
