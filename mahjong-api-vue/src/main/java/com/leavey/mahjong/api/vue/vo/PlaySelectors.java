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
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Leavey
 */
public class PlaySelectors extends Selectors {

    @Override
    public List<Tile> toTiles() {
        List<TileVo> tiles = new ArrayList<>();
        if (!CollectionUtils.isEmpty(character)) {
            tiles.addAll(character);
        }
        if (!CollectionUtils.isEmpty(dot)) {
            tiles.addAll(dot);
        }
        if (!CollectionUtils.isEmpty(bamboo)) {
            tiles.addAll(bamboo);
        }
        //多次选择就生成几张
        return tiles.stream().filter(t -> t.getAmount() > 0).flatMap(t -> IntStream.range(0, t.getAmount()).mapToObj(i -> Tile.parseCode(t.getCode()))).collect(Collectors.toList());
    }
}
