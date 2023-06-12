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

package com.leavey.mahjong.engine.util;

import com.leavey.mahjong.engine.bean.EatPosition;
import com.leavey.mahjong.engine.bean.Tile;

/**
 * @author Leavey
 */
public class PositionUtil {

    /**
     * 判断是否上下家关系
     *
     * @param prev    上家坐标
     * @param next    下家坐标
     * @param players 玩家数量
     * @return 上下家关系 返回 true
     */
    public static boolean isNext(int prev, int next, int players) {
        prev++;
        if (prev >= players) {
            prev = 0;
        }
        return prev == next;
    }

    /**
     * 判断吃牌位置是否合法
     *
     * @param tile     要吃的牌
     * @param position 吃牌位置
     * @return 是否合法
     */
    public static boolean isValidEat(Tile tile, EatPosition position) {
        if (position == EatPosition.LEFT) {
            //要吃的牌在最左侧，要吃的牌小于8
            return tile.getValue() < 8;
        } else if (position == EatPosition.MIDDLE) {
            //要吃的牌在中间，要吃的牌只能是2-8
            return tile.getValue() > 1 && tile.getValue() < 9;
        } else if (position == EatPosition.RIGHT) {
            //要吃的牌在最右侧，要吃的牌大于2
            return tile.getValue() > 2;
        }
        return false;
    }

}
