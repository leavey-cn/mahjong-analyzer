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
import com.leavey.mahjong.common.exception.ErrorTileException;
import com.leavey.mahjong.efficiency.bean.EfficiencyEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 基于手牌的牌效分析
 *
 * @author Leavey
 */
public class EfficiencyUtils {

    public static void main(String[] args) throws ErrorTileException {
        List<Tile> tiles = Arrays.asList(Tile.parseCode(11), Tile.parseCode(12), Tile.parseCode(13), Tile.parseCode(14));
        List<EfficiencyEntry> entries = analyzeEfficiency(tiles, tile -> tile.getCode() < 40 && (tile.getValue() == 2 || tile.getValue() == 5 || tile.getValue() == 8));
        entries.forEach(System.out::println);
    }

    /**
     * 分析一组麻将牌的牌效
     *
     * @param tiles           牌
     * @param leaderPredicate 判断一张牌可否作为将牌
     */
    public static List<EfficiencyEntry> analyzeEfficiency(List<Tile> tiles, Predicate<Tile> leaderPredicate) {
        if (tiles == null || tiles.isEmpty()) {
            throw new IllegalArgumentException("牌的数量不能为空");
        }
        int size = tiles.size();
        if (size > 13 || (size - 1) % 3 != 0) {
            throw new IllegalArgumentException("牌的数量错误，只可分析1、4、7、10、13张牌");
        }
        int needGroups = (size - 1) / 3;
        int needLeaders = 1;
        //按类型分组
        List<Set<EfficiencyEntry>> list = tiles.stream().collect(Collectors.groupingBy(Tile::getType)).entrySet().stream().map(entry -> analyzeEfficiency(entry.getKey(), entry.getValue(), leaderPredicate)).collect(Collectors.toList());

        List<EfficiencyEntry> sourceEntries = new ArrayList<>(list.get(0));
        List<EfficiencyEntry> nextEntries = new ArrayList<>();

        if (list.size() == 1) {
            nextEntries = sourceEntries;
        } else {
            for (int i = 1; i < list.size(); i++) {
                for (EfficiencyEntry entry : list.get(i)) {
                    nextEntries.addAll(sourceEntries.stream().map(e -> e.join(entry)).collect(Collectors.toList()));
                }
                sourceEntries = nextEntries;
                nextEntries = new ArrayList<>();
            }
        }
        nextEntries.sort(EfficiencyEntry::compareTo);
        List<EfficiencyEntry> wins = nextEntries.stream().filter(entry -> entry.getKey().getGroups() == needGroups && entry.getKey().getLeaders() == needLeaders).collect(Collectors.toList());
        if (!wins.isEmpty()) {
            return wins;
        }
        return nextEntries;
    }

    /**
     * 分析一色牌的牌效
     *
     * @param type  牌的颜色
     * @param tiles 牌的集合
     */
    private static Set<EfficiencyEntry> analyzeEfficiency(Type type, List<Tile> tiles, Predicate<Tile> leaderPredicate) {
        int[] tileArray = new int[type.getMaxValue() + 1];
        tiles.forEach(tile -> tileArray[tile.getValue()]++);
        return analyzeEfficiency(type, tileArray, leaderPredicate);
    }

    /**
     * 分析一色牌的牌效
     *
     * @param type            牌的颜色
     * @param tiles           牌的集合
     * @param leaderPredicate 判断一张牌可否作为将牌
     */
    private static Set<EfficiencyEntry> analyzeEfficiency(Type type, int[] tiles, Predicate<Tile> leaderPredicate) {
        //遍历从任意一张牌开始，之前的牌弃掉的可能性
        return IntStream.rangeClosed(1, type.getMaxValue()).boxed().flatMap(i -> analyzeEfficiency(type, tiles, leaderPredicate, i)).collect(Collectors.toSet());
    }


    /**
     * 分析一色牌的牌效
     *
     * @param type            牌的颜色
     * @param tiles           牌的集合
     * @param leaderPredicate 判断一张牌可否作为将牌
     * @param val             开始分析的索引
     */
    private static Stream<EfficiencyEntry> analyzeEfficiency(Type type, int[] tiles, Predicate<Tile> leaderPredicate, int val) {
        List<EfficiencyEntry> entries = new ArrayList<>();
        analyzeEfficiency(type, tiles, leaderPredicate, val, new EfficiencyEntry(), entries);
        return entries.stream();
    }

    /**
     * 分析一色牌的牌效
     *
     * @param type            牌的颜色
     * @param tiles           牌的集合
     * @param leaderPredicate 判断一张牌可否作为将牌
     * @param val             开始分析的索引，也是牌的内容
     * @param entry           当前的牌效信息
     */
    private static void analyzeEfficiency(Type type, int[] tiles, Predicate<Tile> leaderPredicate, int val, EfficiencyEntry entry, List<EfficiencyEntry> entries) {
        if (val >= tiles.length) {
            //结束了 收集复制的可能性
            if (entry.isValid()) {
                entries.add(entry.copy(type, tiles));
            }
            return;
        }
        if (tiles[val] <= 0) {
            analyzeEfficiency(type, tiles, leaderPredicate, val + 1, entry, entries);
            return;
        }
        Tile tile;
        try {
            tile = type.tile(val);
        } catch (ErrorTileException e) {
            throw new RuntimeException(e);
        }
        PossibilityEffect.possibleEffects(tile, leaderPredicate.test(tile))
                .forEach(effect -> effect.applyAndRevoke(tiles, entry, () -> analyzeEfficiency(type, tiles, leaderPredicate, val, entry, entries)));

        //没有更多的可能性了，分析下一张牌
        analyzeEfficiency(type, tiles, leaderPredicate, val + 1, entry, entries);
    }
}
