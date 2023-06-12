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

package com.leavey.mahjong.api.vue.vo;

import com.leavey.mahjong.engine.bean.Tile;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leavey
 */
@Data
@NoArgsConstructor
public class GroupVo {
    private List<TileVo> tiles;

    public GroupVo(List<TileVo> tiles) {
        this.tiles = tiles;
    }

    public static GroupVo of(Tile left, Tile middle, Tile right) {
        return new GroupVo(List.of(TileVo.view(left), TileVo.view(middle), TileVo.view(right)));
    }

    public static List<GroupVo> createEatGroups(Tile tile) {
        List<GroupVo> result = new ArrayList<>();
        if (tile.getValue() <= 7) {
            result.add(GroupVo.of(tile.next(), tile, tile.next().next()));
        }
        if (tile.getValue() >= 3) {
            result.add(GroupVo.of(tile.prev().prev(), tile, tile.prev()));
        }
        if (tile.getValue() >= 2 && tile.getValue() <= 8) {
            result.add(GroupVo.of(tile.prev(), tile, tile.next()));
        }
        return result;
    }
}
