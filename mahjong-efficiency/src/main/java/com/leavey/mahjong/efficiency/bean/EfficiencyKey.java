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
public class EfficiencyKey {
    /**
     * 已完成的组合数
     */
    private int groups;
    /**
     * 是否已存在将牌
     */
    private boolean existLeader;
    /**
     * 存在的搭子数
     */
    private int pairs;
    /**
     * 是否存在将牌搭子
     */
    private boolean existSingleLeader;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EfficiencyKey that = (EfficiencyKey) o;
        return groups == that.groups && existLeader == that.existLeader && pairs == that.pairs && existSingleLeader == that.existSingleLeader;
    }

    @Override
    public int hashCode() {
        return Objects.hash(groups, existLeader, pairs, existSingleLeader);
    }

    public void increaseGroup() {
        groups++;
    }
    public void decreaseGroup(){
        groups--;
    }

    public void setExistSingleLeader(boolean existSingleLeader) {
        this.existSingleLeader = existSingleLeader;
    }

    public EfficiencyKey copy(){
        return new EfficiencyKey(groups,existLeader,pairs, existSingleLeader);
    }

}
