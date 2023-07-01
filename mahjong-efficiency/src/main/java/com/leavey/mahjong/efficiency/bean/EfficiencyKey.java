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

package com.leavey.mahjong.efficiency.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 牌效键
 *
 * @author Leavey
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EfficiencyKey implements Comparable<EfficiencyKey> {
    /**
     * 已完成的组合数
     */
    private int groups;
    /**
     * 存在的将牌对数
     */
    private int leaderPairs;
    /**
     * 存在的搭子数
     */
    private int pairs;
    /**
     * 散将数量
     */
    private int leaders;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EfficiencyKey that = (EfficiencyKey) o;
        return groups == that.groups && leaderPairs == that.leaderPairs && pairs == that.pairs && leaders == that.leaders;
    }

    @Override
    public int hashCode() {
        return Objects.hash(groups, leaderPairs, pairs, leaders);
    }

    public EfficiencyKey copy() {
        return new EfficiencyKey(groups, leaderPairs, pairs, leaders);
    }

    public EfficiencyKey join(EfficiencyKey other) {
        return new EfficiencyKey(groups + other.groups, leaderPairs + other.leaderPairs, pairs + other.pairs, leaders + other.leaders);
    }

    public boolean isValid() {
        return groups > 0 || leaderPairs > 0 || pairs > 0 || leaders > 0;
    }

    @Override
    public int compareTo(EfficiencyKey o) {
        int cmp = Integer.compare(groups, o.groups);
        if (cmp == 0) {
            cmp = Integer.compare(leaders, o.leaders);
        }
        if (cmp == 0) {
            cmp = Integer.compare(pairs, o.pairs);
        }
        if (cmp == 0) {
            cmp = Integer.compare(leaderPairs, o.leaderPairs);
        }
        return cmp * -1;
    }

    @Override
    public String toString() {
        return "EfficiencyKey{" +
                "groups=" + groups +
                ", leaders=" + leaders +
                ", pairs=" + pairs +
                ", leaderPairs=" + leaderPairs +
                '}';
    }
}
