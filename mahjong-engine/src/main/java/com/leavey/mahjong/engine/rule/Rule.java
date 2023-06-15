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

import com.leavey.mahjong.common.bean.Tile;
import com.leavey.mahjong.common.bean.Type;
import com.leavey.mahjong.engine.bean.Action;
import com.leavey.mahjong.engine.executor.Executor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 麻将规则
 *
 * @author Leavey
 */
public interface Rule {
    /**
     * 初始化牌堆，根据规则不同，每种牌初始化的数量不一样
     *
     * @param type 麻将牌类型
     * @return 该类型初始化后的牌堆
     */
    default List<Tile> newTiles(Type type) {
        return IntStream.rangeClosed(1, type.getMaxValue()).boxed().flatMap(val -> IntStream.range(0, type.getAmount()).mapToObj(i -> new Tile(val, type))).collect(Collectors.toList());
    }

    /**
     * 参与人数
     *
     * @return 参与人数
     */
    default int playerAmount() {
        return 4;
    }

    /**
     * 起手牌数量
     *
     * @return 起手牌数量
     */
    default int firstHandAmount() {
        return 13;
    }

    /**
     * 定制操作执行器
     *
     * @param executorMap 执行器
     */
    void customizeExecutors(Map<Action, Executor> executorMap);

    /**
     * 开杠后允许的摸牌数量
     *
     * @return 开杠后允许的摸牌数量
     */
    int allowGangDrawAmount();
//
//    /**
//     * 校验当前是否可进行该操作
//     *
//     * @param game          牌局
//     * @param actionRequest 操作请求
//     */
//    default boolean validateActionRequest(Game game, ActionRequest actionRequest) {
//        Action action = actionRequest.getAction();
//        if (action == Action.DRAW) {
//            return validateDraw(game, actionRequest);
//        } else if (action == Action.GANG_DRAW) {
//            return validateGangDraw(game, actionRequest);
//        } else if (action == Action.PLAY) {
//            return validatePlay(game, actionRequest);
//        }
//        return false;
//    }
//
//    /**
//     * 校验当前是否可进行摸牌操作
//     *
//     * @param game          牌局
//     * @param actionRequest 操作请求
//     */
//    default boolean validateDraw(Game game, ActionRequest actionRequest) {
//        //牌局刚开始，任意一家都可作为庄家摸牌
//        if (game.getFocus() == null) {
//            return true;
//        }
//        //上家打出牌，作为下家可以开始摸牌
//        return game.getOperation().getAction() == Action.PLAY && PositionUtil.isNext(game.getFocus(), actionRequest.getPlayer(), game.getPlayers());
//        //其他情况禁止摸牌
//    }
//
//    /**
//     * 校验当前是否可进行开杠摸牌操作
//     *
//     * @param game          牌局
//     * @param actionRequest 操作请求
//     */
//    default boolean validateGangDraw(Game game, ActionRequest actionRequest) {
//        //自身开杠后，允许开杠摸牌
//        if ((game.getOperation().getAction() == Action.GANG || game.getOperation().getAction() == Action.DARK_GANG) && game.getFocus() == actionRequest.getPlayer()) {
//            //且允许摸牌数量与规则一致，但预检时不校验数量
//            if (actionRequest.isPrepare()) {
//                return true;
//            } else {
//                return actionRequest.getDarkTileAmount() == allowGangDrawAmount();
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 校验当前是否可进行打牌操作
//     *
//     * @param game          牌局
//     * @param actionRequest 操作请求
//     */
//    default boolean validatePlay(Game game, ActionRequest actionRequest) {
//        //自身摸牌后，允许打牌
//        return game.getOperation().getAction() == Action.DRAW && game.getFocus() == actionRequest.getPlayer();
//    }
}
