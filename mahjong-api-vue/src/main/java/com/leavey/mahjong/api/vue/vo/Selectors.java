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

import com.leavey.mahjong.common.bean.Tile;
import com.leavey.mahjong.common.bean.Type;
import com.leavey.mahjong.engine.bean.Game;
import com.leavey.mahjong.engine.bean.Operation;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 选择区
 *
 * @author Leavey
 */
@Data
public class Selectors {
    protected List<TileVo> character;
    protected List<TileVo> dot;
    protected List<TileVo> bamboo;


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
        //多次选择仅一张有效
        return tiles.stream().filter(t -> t.getAmount() > 0).map(TileVo::getCode).map(Tile::parseCode).collect(Collectors.toList());
    }

    public static PlaySelectors of(List<Tile> tiles, Function<Tile, TileVo> mapper) {
        Map<Type, List<Tile>> tileMap = Optional.ofNullable(tiles).orElse(new ArrayList<>()).stream().collect(Collectors.groupingBy(Tile::getType));
        PlaySelectors selectors = new PlaySelectors();
        selectors.character = tileMap.getOrDefault(Type.CHARACTER, new ArrayList<>()).stream().map(mapper).collect(Collectors.toList());
        selectors.dot = tileMap.getOrDefault(Type.DOT, new ArrayList<>()).stream().map(mapper).collect(Collectors.toList());
        selectors.bamboo = tileMap.getOrDefault(Type.BAMBOO, new ArrayList<>()).stream().map(mapper).collect(Collectors.toList());
        return selectors;
    }

    public static Selectors getPrevKeyTileSelectors(Game game) {
        List<Tile> tiles = Optional.ofNullable(game.getPrevOperation()).map(Operation::getKeyTiles).orElse(new ArrayList<>());
        return Selectors.of(tiles, TileVo::incrAmount);
    }

    public static Selectors fullSelectors() {
        List<Tile> tiles = Arrays.stream(Type.values()).flatMap(type -> IntStream.rangeClosed(1, type.getMaxValue()).mapToObj(val -> new Tile(val, type))).collect(Collectors.toList());
        return Selectors.of(tiles, TileVo::incrAmount);
    }
}
