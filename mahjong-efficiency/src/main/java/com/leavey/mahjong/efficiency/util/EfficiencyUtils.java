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

import java.util.*;
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
        List<Tile> tiles = Arrays.asList(Tile.parseCode(11), Tile.parseCode(12), Tile.parseCode(13), Tile.parseCode(14), Tile.parseCode(33), Tile.parseCode(34), Tile.parseCode(35));
        Map<Integer, Set<Tile>> result = analyzeEfficiency(tiles, tile -> tile.getCode() < 40 && (tile.getValue() == 2 || tile.getValue() == 5 || tile.getValue() == 8));
        System.out.println(tiles);
        System.out.println(result);
    }

    /**
     * 分析一组麻将牌的牌效
     *
     * @param tiles           牌
     * @param leaderPredicate 判断一张牌可否作为将牌
     */
    public static Map<Integer, Set<Tile>> analyzeEfficiency(List<Tile> tiles, Predicate<Tile> leaderPredicate) {
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
        List<EfficiencyEntry> entries = new ArrayList<>();

        if (list.size() == 1) {
            entries = sourceEntries;
        } else {
            for (int i = 1; i < list.size(); i++) {
                for (EfficiencyEntry entry : list.get(i)) {
                    nextEntries.addAll(sourceEntries.stream().map(e -> e.join(entry)).collect(Collectors.toList()));
                }
                sourceEntries = nextEntries;
                entries = nextEntries;
                nextEntries = new ArrayList<>();
            }
        }
        entries.sort(EfficiencyEntry::compareTo);
        List<EfficiencyEntry> wins = entries.stream().filter(entry -> entry.getKey().getGroups() == needGroups && entry.getKey().getLeaders() == needLeaders).collect(Collectors.toList());
        if (!wins.isEmpty()) {
            return Map.of(0, Set.of());
        }

        Map<Integer, Set<Tile>> stepMap = new TreeMap<>();
        for (EfficiencyEntry entry : entries) {

            if (entry.getKey().getLeaders() == 0 && entry.getKey().getLeaderPairs() == 0) {
                //没有将对，也没有将搭子，只存在于将牌需要特殊牌的情况，例如长沙麻将258做将
                //此时可进特殊将牌成为将搭子
                Tile.allTiles().stream().filter(leaderPredicate).forEach(entry::addTile);
            }

            int step = 0;
            int groups = needGroups - entry.getKey().getGroups();
            int leaders = needLeaders - entry.getKey().getLeaders();
            int giveUpPairs = 0;
            int giveUpLeaderPairs = 0;
            if (groups > 0) {
                //缺x句话，是否存在搭子
                if (entry.getKey().getPairs() >= groups) {
                    //多出的搭子数
                    giveUpPairs = entry.getKey().getPairs() - groups;
                    //还需要几步成牌
                    step += groups;
                } else {
                    //现有的搭子成牌步数
                    step += entry.getKey().getPairs();
                    //剩余零牌要组成搭子成牌步数
                    step += 2 * (groups - entry.getKey().getPairs());
                }
            }
            if (leaders > 0) {
                //将对只需要一对，此时leaders只会=1
                if (entry.getKey().getLeaderPairs() > 0) {
                    //多出的将搭子
                    giveUpLeaderPairs = entry.getKey().getLeaderPairs() - 1;
                    //将搭子成将对步数
                    step++;
                } else {
                    //没有将搭子，将牌是特殊牌，需要摸两张
                    step += 2;
                }
            }
            stepMap.compute(step, (key, tiles1) -> {
                if (tiles1 == null) {
                    tiles1 = new TreeSet<>();
                }
                tiles1.addAll(entry.getTiles().keySet());
                return tiles1;
            });
            System.out.println("还需" + step + "步胡牌，缺" + groups + "句话，缺" + leaders + "对将");
            System.out.println(entry);
        }
        return stepMap;
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
        //从任意一张牌开始分析，循环分析结束回到该索引
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
        analyzeEfficiency(type, tiles, leaderPredicate, val, 0, new EfficiencyEntry(), entries);
        return entries.stream();
    }

    /**
     * 分析一色牌的牌效
     *
     * @param type            牌的颜色
     * @param tiles           牌的集合
     * @param leaderPredicate 判断一张牌可否作为将牌
     * @param val             开始分析的索引，也是牌的内容
     * @param depth           遍历深度
     * @param entry           当前的牌效信息
     */
    private static void analyzeEfficiency(Type type, int[] tiles, Predicate<Tile> leaderPredicate, int val, int depth, EfficiencyEntry entry, List<EfficiencyEntry> entries) {
        if (depth >= type.getMaxValue()) {
            //结束了 收集复制的可能性
            if (entry.isValid()) {
                EfficiencyEntry copyed = entry.copy(type, tiles);
                entries.add(copyed);
            }
            return;
        }
        if (val >= tiles.length) {
            analyzeEfficiency(type, tiles, leaderPredicate, 1, depth + 1, entry, entries);
            return;
        }
        if (tiles[val] <= 0) {
            analyzeEfficiency(type, tiles, leaderPredicate, val + 1, depth + 1, entry, entries);
            return;
        }
        Tile tile;
        try {
            tile = type.tile(val);
        } catch (ErrorTileException e) {
            throw new RuntimeException(e);
        }
        PossibilityEffect.possibleEffects(tile, leaderPredicate.test(tile)).forEach(effect -> effect.applyAndRevoke(tiles, entry, () -> analyzeEfficiency(type, tiles, leaderPredicate, val, depth, entry, entries)));

        //没有更多的可能性了，分析下一张牌
        analyzeEfficiency(type, tiles, leaderPredicate, val + 1, depth + 1, entry, entries);
    }
}
