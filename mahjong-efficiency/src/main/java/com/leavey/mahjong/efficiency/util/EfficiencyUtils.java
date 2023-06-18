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
import java.util.Arrays;
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

    public static void main(String[] args) {
        List<Tile> tiles = Arrays.asList(Tile.parseCode(11), Tile.parseCode(12), Tile.parseCode(13), Tile.parseCode(14));
        List<EfficiencyEntry> entries = analyzeEfficiency(tiles, tile -> tile.getCode() < 40 && (tile.getValue() == 2 || tile.getValue() == 5 || tile.getValue() == 8));
        System.out.println(entries);
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
        //按类型分组
        List<List<EfficiencyEntry>> list = tiles.stream().collect(Collectors.groupingBy(Tile::getType)).entrySet().stream().map(entry -> analyzeEfficiency(entry.getKey(), entry.getValue(), leaderPredicate)).collect(Collectors.toList());

        List<EfficiencyEntry> sourceEntries = list.get(0);
        List<EfficiencyEntry> nextEntries = new ArrayList<>();

        for (int i = 1; i < list.size(); i++) {
            for (EfficiencyEntry entry : list.get(i)) {
                nextEntries.addAll(sourceEntries.stream().map(e -> e.join(entry)).collect(Collectors.toList()));
            }
            sourceEntries = nextEntries;
            nextEntries = new ArrayList<>();
        }
        return nextEntries;
    }

    private void iterator(List<List<EfficiencyEntry>> entries, int floor) {
        entries.get(floor).forEach(entry -> {

        });
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
            //结束了 收集复制的可能性
            if (entry.isValid()) {
                entries.add(entry.copy());
            }
            return;
        }
        if (tiles[val] <= 0) {
            analyzeEfficiency(type, tiles, leaderPredicate, val + 1, entry, entries);
            return;
        }
        //寻找一句话的可能性
        analyzeGroup(type, tiles, leaderPredicate, val, entry, entries, false);
        //寻找一坎的可能性
        analyzeGroup(type, tiles, leaderPredicate, val, entry, entries, true);
        //寻找1对将牌的可能性
        analyzeLeaderPair(type, tiles, leaderPredicate, val, entry, entries);
        //寻找 x、x+1 两面搭子的可能性
        analyzePairs(type, tiles, leaderPredicate, val, entry, entries, val + 1, -1);
        //寻找 x、x+2 卡张搭子的可能性
        analyzePairs(type, tiles, leaderPredicate, val, entry, entries, val + 2, -1);
        //寻找 x、x+2、x+4 双卡张搭子的可能性
        analyzePairs(type, tiles, leaderPredicate, val, entry, entries, val + 2, val + 4);
        //寻找 x、x、x+1 两面搭子
        analyzePairs(type, tiles, leaderPredicate, val, entry, entries, val, val + 1);
        //寻找 x、x+1、x+1 两面搭子
        analyzePairs(type, tiles, leaderPredicate, val, entry, entries, val + 1, val + 1);
        //寻找单将的可能性
        analyzeLeader(type, tiles, leaderPredicate, val, entry, entries);
        //没有更多的可能性了，分析下一张牌
        analyzeEfficiency(type, tiles, leaderPredicate, val + 1, entry, entries);
    }

    /**
     * 组成搭子的可能性
     *
     * @param first  组成搭子的第一张牌
     * @param second 组成搭子的第二张牌
     * @param third  组成搭子的第三张牌，可不需要，填-1
     */
    private static void analyzePairs(Type type, int[] tiles, Predicate<Tile> leaderPredicate, int val, EfficiencyEntry entry, List<EfficiencyEntry> entries, int first, int second, int third) {
        // TODO: 2023/6/18 构建Effect对象，计算可进牌数
        if (third == -1) {
            //两张牌组成的搭子
            if (first < tiles.length && tiles[first] > 0) {
                tiles[first]--;
                entry.getKey().increasePairs();
                analyzeEfficiency(type, tiles, leaderPredicate, val, entry, entries);
                tiles[first]++;
                entry.getKey().decreasePairs();
            }
        } else {
            //三张牌组成的搭子
            if (first < tiles.length && second < tiles.length && tiles[first] > 0 && tiles[second] > 0) {
                tiles[first]--;
                tiles[second]--;
                entry.getKey().increasePairs();
                analyzeEfficiency(type, tiles, leaderPredicate, val, entry, entries);
                tiles[first]++;
                tiles[second]++;
                entry.getKey().decreasePairs();
            }
        }
    }

    /**
     * 寻找1对将牌的可能性
     */
    private static void analyzeLeaderPair(Type type, int[] tiles, Predicate<Tile> leaderPredicate, int val, EfficiencyEntry entry, List<EfficiencyEntry> entries) {
        if (leaderPredicate.test(type.tile(val)) && tiles[val] >= 2) {
            //该牌是否能作为将牌，且有两张牌
            Effect.leaderPair(type.tile(val)).applyAndRevoke(tiles, entry, () -> analyzeEfficiency(type, tiles, leaderPredicate, val, entry, entries));
        }
    }

    /**
     * 寻找单将的可能性
     */
    private static void analyzeLeader(Type type, int[] tiles, Predicate<Tile> leaderPredicate, int val, EfficiencyEntry entry, List<EfficiencyEntry> entries) {
        if (leaderPredicate.test(type.tile(val)) && tiles[val] > 0) {
            //该牌是否能作为将牌，且剩余还有一张余牌
            tiles[val]--;
            entry.getKey().setExistLeader(true);
            analyzeEfficiency(type, tiles, leaderPredicate, val, entry, entries);
            tiles[val]++;
            entry.getKey().setExistLeader(false);
        }
    }

    /**
     * 分析存在一句话的可能性，例如a/b/c   a/a/a
     */
    private static void analyzeGroup(Type type, int[] tiles, Predicate<Tile> leaderPredicate, int val, EfficiencyEntry entry, List<EfficiencyEntry> entries, boolean isSame) {
        Effect effect;
        if (isSame) {
            effect = peelSameGroup(type, tiles, val);
        } else {
            effect = peelDiffGroup(type, tiles, val);
        }
        if (effect != null) {
            effect.applyAndRevoke(tiles, entry, () -> analyzeEfficiency(type, tiles, leaderPredicate, val, entry, entries));
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
    private static Effect peelDiffGroup(Type type, int[] tiles, int val) {
        if (val + 2 < tiles.length && type.isAllowDiffGroup() && tiles[val] > 0 && tiles[val + 1] > 0 && tiles[val + 2] > 0) {
            return Effect.diffGroup(type.tile(val));
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
    private static Effect peelSameGroup(Type type, int[] tiles, int val) {
        if (tiles[val] >= 3) {
            return Effect.sameGroup(type.tile(val));
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
