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

package com.leavey.mahjong.common.bean;

import com.leavey.mahjong.common.exception.ErrorTileException;

/**
 * @author Leavey
 */
public enum Type {
    //万
    CHARACTER(9, 4, true, "万", 10, true), //饼
    DOT(9, 4, true, "饼", 20, true), //条
    BAMBOO(9, 4, true, "条", 30, true),
    /**
     * 风
     * <p>
     * 东南西北
     */
    WIND(4, 4, false, "WIND", 40, false),
    /**
     * 中发白
     */
    DRAGON(3, 4, false, "DRAGON", 50, false),
    /**
     * 春夏秋冬梅兰竹菊
     */
    FLOWER(8, 1, false, "FLOWER", 60, false);

    private final int maxValue;
    private final int amount;
    private final boolean allowEat;
    private final String displayText;
    private final int base;
    /**
     * 是否允许组合中存在不一致的牌
     * <p>
     * 例如万/筒/条   1万2万3万 一句话
     * <p>
     * 中发白  只能一坎
     */
    private final boolean allowDiffGroup;

    Type(int maxValue, int amount, boolean allowEat, String displayText, int base, boolean allowDiffGroup) {
        this.maxValue = maxValue;
        this.amount = amount;
        this.allowEat = allowEat;
        this.displayText = displayText;
        this.base = base;
        this.allowDiffGroup = allowDiffGroup;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isAllowEat() {
        return allowEat;
    }

    public String getDisplayText() {
        return displayText;
    }

    public int getBase() {
        return base;
    }

    public boolean isAllowDiffGroup() {
        return allowDiffGroup;
    }

    public Tile tile(int value) throws ErrorTileException {
        return new Tile(value, this);
    }
}
