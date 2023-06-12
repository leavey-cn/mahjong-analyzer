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

package com.leavey.mahjong.engine.rule;

import com.leavey.mahjong.engine.bean.Action;
import com.leavey.mahjong.engine.bean.Tile;
import com.leavey.mahjong.engine.bean.Type;
import com.leavey.mahjong.engine.executor.Executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 长沙麻将规则
 *
 * @author Leavey
 */
public class ChangShaRule implements Rule {
    @Override
    public List<Tile> newTiles(Type type) {
        if (type == Type.CHARACTER || type == Type.DOT || type == Type.BAMBOO) {
            return Rule.super.newTiles(type);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void customizeExecutors(Map<Action, Executor> executorMap) {

    }

    @Override
    public int allowGangDrawAmount() {
        return 2;
    }
}
