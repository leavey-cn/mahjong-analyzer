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

package com.leavey.mahjong.engine.bean;


import com.leavey.mahjong.common.bean.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author Leavey
 */
public class HandTiles implements DeepCopy<HandTiles> {

    private int darkAmount;
    private List<Tile> openTiles;

    public HandTiles(int darkAmount) {
        this(darkAmount, new ArrayList<>());
    }

    public HandTiles(int darkAmount, List<Tile> openTiles) {
        this.darkAmount = darkAmount;
        this.openTiles = openTiles;
    }

    @Override
    public HandTiles deepCopy() {
        return new HandTiles(darkAmount, new ArrayList<>(openTiles));
    }

    /**
     * 从此区域内打出一张目标牌
     * <p>
     * 先从明牌寻找，找不到就减少一张暗牌
     *
     * @param tile 牌
     */
    public void play(Tile tile) {
        peel(tile);
    }

    /**
     * 抽离出一张目标牌
     *
     * @param tile 目标牌
     */
    private void peel(Tile tile) {
        //先从明牌寻找
        for (int i = 0; i < openTiles.size(); i++) {
            if (openTiles.get(i).equals(tile)) {
                openTiles.remove(i);
                return;
            }
        }
        //减少一张暗牌数量
        this.darkAmount--;
    }

    /**
     * 增加区域中的暗牌数量
     *
     * @param amount 增加数量
     */
    public void incrDarkAmount(int amount) {
        this.darkAmount += amount;
    }

    /**
     * 添加一张明牌
     *
     * @param tile 牌
     */
    public void add(Tile tile) {
        this.openTiles.add(tile);
    }

    /**
     * 将手中暗牌转明牌
     * <p>
     * 每次传入的暗牌，不是简单的转换，需要增量转换
     * <p>
     * 例如：上一次明牌了3万2张，这次又传入3万3张，那么实际只转换1张暗牌为明牌
     *
     * @param tiles 已知的明牌
     */
    public void show(List<Tile> tiles) {
        Map<Integer, Long> tileMap = openTiles.stream().collect(Collectors.groupingBy(Tile::getCode, Collectors.counting()));
        Map<Integer, Long> newMap = tiles.stream().collect(Collectors.groupingBy(Tile::getCode, Collectors.counting()));
        newMap.forEach((code, amount) -> tileMap.compute(code, (integer, old) -> old == null ? amount : Math.max(old, amount)));
        List<Tile> newOpenTiles = tileMap.entrySet().stream().flatMap(entry -> LongStream.range(0, entry.getValue()).mapToObj(i -> Tile.parseCode(entry.getKey()))).collect(Collectors.toList());
        if (newOpenTiles.size() > openTiles.size()) {
            darkAmount -= newOpenTiles.size() - openTiles.size();
        }
        this.openTiles = newOpenTiles;
    }

    /**
     * 吃牌
     *
     * @param tile     要吃的牌
     * @param position 吃牌的位置
     * @return 吃完后的组合
     */
    public CompleteGroup eat(Tile tile, EatPosition position) {
        Tile left = null;
        Tile right = null;
        if (position == EatPosition.LEFT) {
            left = tile.next();
            right = tile.next().next();
        } else if (position == EatPosition.MIDDLE) {
            left = tile.prev();
            right = tile.next();
        } else if (position == EatPosition.RIGHT) {
            left = tile.prev().prev();
            right = tile.prev();
        }
        peel(left);
        peel(right);
        return new CompleteGroup(List.of(left, tile, right));
    }

    /**
     * 碰牌
     *
     * @param tile 牌
     * @return 组合
     */
    public CompleteGroup pen(Tile tile) {
        peel(tile);
        peel(tile);
        return new CompleteGroup(List.of(tile, tile, tile));
    }

    /**
     * 杠牌
     *
     * @param tile    牌
     * @param outside 要杠的牌从外部获取还是内部的
     *                明杠的牌、暗杠自身上次杠出的牌 是外部获取
     *                其他暗杠的牌 都是内部获取
     * @return 组合
     */
    public CompleteGroup gang(Tile tile, boolean outside) {
        peel(tile);
        peel(tile);
        peel(tile);
        if (!outside) {
            peel(tile);
        }
        return new CompleteGroup(List.of(tile, tile, tile, tile));
    }

    public int getDarkAmount() {
        return darkAmount;
    }

    public List<Tile> getOpenTiles() {
        return openTiles;
    }
}
