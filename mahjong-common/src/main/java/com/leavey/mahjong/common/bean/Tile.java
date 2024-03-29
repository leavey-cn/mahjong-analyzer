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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Leavey
 */
public class Tile implements Comparable<Tile> {
    // TODO: 2023/6/16 修改为享元模式
    private final int value;
    private final Type type;

    public Tile(int value, Type type) throws ErrorTileException {
        this.value = value;
        this.type = type;
        if (value <= 0) {
            throw new ErrorTileException(type + "类型的牌值必须大于0 ");
        }
        if (value > type.getMaxValue()) {
            throw new ErrorTileException(type + "类型的牌值必须小于等于 " + type.getMaxValue());
        }
    }

    public int getCode() {
        return type.getBase() + value;
    }

    public static Tile parseCode(int code) throws ErrorTileException {
        for (Type type : Type.values()) {
            int val = code - type.getBase();
            if (val >= 1 && val < 10) {
                return new Tile(val, type);
            }
        }
        throw new ErrorTileException(code + "");
    }

    public int getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return value == tile.value && type == tile.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }

    public Tile next() throws ErrorTileException {
        return new Tile(value + 1, type);
    }

    public Tile prev() throws ErrorTileException {
        return new Tile(value - 1, type);
    }

    @Override
    public int compareTo(Tile o) {
        int cmp = Integer.compare(this.type.ordinal(), o.type.ordinal());
        if (cmp == 0) {
            cmp = Integer.compare(this.value, o.value);
        }
        return cmp;
    }

    private static final String[] WINDS = {"", "东", "南", "西", "北"};
    private static final String[] DRAGONS = {"", "中", "发", "白"};
    private static final String[] FLOWERS = {"", "春", "夏", "秋", "东", "梅", "兰", "竹", "菊"};

    @Override
    public String toString() {
        if (type == Type.CHARACTER || type == Type.DOT || type == Type.BAMBOO) {
            return value + type.getDisplayText();
        } else if (type == Type.WIND) {
            return WINDS[value];
        } else if (type == Type.DRAGON) {
            return DRAGONS[value];
        } else if (type == Type.FLOWER) {
            return FLOWERS[value];
        }
        return value + type.toString();
    }

    public boolean canEat() {
        return type.getBase() <= 30;
    }

    public boolean isBoundary() {
        return canEat() && (value == 1 || value == 9);
    }


    public static void each(Consumer<Tile> consumer) {
        allTiles().forEach(consumer);
    }

    public static List<Tile> allTiles() {
        return Arrays.stream(Type.values()).flatMap(type -> IntStream.rangeClosed(1, type.getMaxValue()).mapToObj(val -> {
            try {
                return type.tile(val);
            } catch (ErrorTileException e) {
                throw new RuntimeException(e);
            }
        })).collect(Collectors.toList());
    }
}
