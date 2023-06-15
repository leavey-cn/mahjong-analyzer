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
import com.leavey.mahjong.efficiency.bean.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


    /**
     * 分析一组麻将牌的牌效
     *
     * @param tiles           牌
     * @param leaderPredicate 判断一张牌可否作为将牌
     */
    public static void analyzeEfficiency(List<Tile> tiles, Predicate<Tile> leaderPredicate) {
        if (tiles == null || tiles.isEmpty()) {
            throw new IllegalArgumentException("牌的数量不能为空");
        }
        int size = tiles.size();
        if (size > 13 || (size - 1) % 3 != 0) {
            throw new IllegalArgumentException("牌的数量错误，只可分析1、4、7、10、13张牌");
        }
        int needGroups = (size - 1) / 3;
        //按类型分组
        Map<Type, List<Tile>> typeTiles = tiles.stream().collect(Collectors.groupingBy(Tile::getType));
        typeTiles.forEach((type, list) -> analyzeEfficiency(type, list, leaderPredicate));
    }

    /**
     * 分析一色牌的牌效
     *
     * @param type  牌的颜色
     * @param tiles 牌的集合
     */
    private static List<EfficiencyEntry> analyzeEfficiency(Type type, List<Tile> tiles, Predicate<Tile> leaderPredicate) {
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
    private static List<EfficiencyEntry> analyzeEfficiency(Type type, int[] tiles, Predicate<Tile> leaderPredicate) {
        //遍历从任意一张牌开始，之前的牌弃掉的可能性
        return IntStream.rangeClosed(1, type.getMaxValue()).boxed().flatMap(i -> analyzeEfficiency(type, tiles, leaderPredicate, i)).collect(Collectors.toList());
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
            //结束了
            //零牌是否存在单张将牌
            boolean existSingleLeader = false;
            for (int i = 1; i < tiles.length; i++) {
                //存在零牌，并且符合特殊将牌 或者 无特殊将牌规则
                Tile leader = type.tile(i);
                if (tiles[i] > 0 && (leaderPredicate == null || leaderPredicate.test(leader))) {
                    existSingleLeader = true;
                    entry.addEffectiveLeaderTile(leader);
                }
            }
            entry.getKey().setExistSingleLeader(existSingleLeader);
            //收集复制的可能性
            entries.add(entry.copy());
            //移除掉存在单张将牌的状态 & 有效进张
            entry.clearExistSingleLeader();
            return;
        }
        if (tiles[val] <= 0) {
            analyzeEfficiency(type, tiles, leaderPredicate, val + 1, entry, entries);
            return;
        }
        //提取一张牌
        tiles[val]--;

        //寻找一句话的可能性
        analyzeGroup(type, tiles, leaderPredicate, val, entry, entries, false);
        //寻找一坎的可能性
        analyzeGroup(type, tiles, leaderPredicate, val, entry, entries, true);
        //寻找1对将牌的可能性

        // TODO: 2023/6/16 寻找搭子的可能性
        //  3 4 两面搭子
        //  3 5 卡张搭子
        //  3 5 7 双卡张搭子
        //  3 3 4 两面搭子
        //  3 4 4 两面搭子


        //没有更多的可能性了，分析下一张牌
        analyzeEfficiency(type, tiles, leaderPredicate, val + 1, entry, entries);
    }

    /**
     * 分析存在一句话的可能性，例如a/b/c   a/a/a
     */
    private static void analyzeGroup(Type type, int[] tiles, Predicate<Tile> leaderPredicate, int val, EfficiencyEntry entry, List<EfficiencyEntry> entries, boolean isSame) {
        Group group = isSame ? peelSameGroup(type, tiles, val) : peelDiffGroup(type, tiles, val);
        if (group != null) {
            entry.getKey().increaseGroup();
            analyzeEfficiency(type, tiles, leaderPredicate, val, entry, entries);
            entry.getKey().decreaseGroup();
            restore(tiles, group);
        }
    }

    /**
     * 从手牌中抽离可以组合成一句话的牌
     *
     * @param type  牌的颜色
     * @param tiles 手牌
     * @param val   关键牌，组合的其他牌必须大于该牌，因为小于该牌的可能性，在前面的坐标中已经分析了
     * @return 组合，可能为空
     */
    private static Group peelDiffGroup(Type type, int[] tiles, int val) {
        if (type.isAllowDiffGroup() && tiles[val + 1] > 0 && tiles[val + 2] > 0) {
            tiles[val + 1]--;
            tiles[val + 2]--;
            return new Group(new Tile(val, type), false);
        }
        return null;
    }

    /**
     * 从手牌中抽离可以组合成一坎的牌
     *
     * @param type  牌的颜色
     * @param tiles 手牌
     * @param val   关键牌，组合的其他牌必须大于该牌，因为小于该牌的可能性，在前面的坐标中已经分析了
     * @return 组合，可能为空
     */
    private static Group peelSameGroup(Type type, int[] tiles, int val) {
        if (type.isAllowDiffGroup() && tiles[val] >= 2) {
            tiles[val]--;
            tiles[val]--;
            return new Group(new Tile(val, type), true);
        }
        return null;
    }

    /**
     * 分析完成后，将group重新填充进手牌
     *
     * @param tiles 手牌
     * @param group 组合
     */
    public static void restore(int[] tiles, Group group) {
        Tile tile = group.getTile();
        tiles[tile.getValue()]++;
        if (group.isSame()) {
            tiles[tile.getValue()]++;
            tiles[tile.getValue()]++;
        } else {
            tiles[tile.getValue() + 1]++;
            tiles[tile.getValue() + 2]++;
        }
    }
}
