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

package com.leavey.mahjong.engine.executor;

import com.leavey.mahjong.engine.bean.Action;
import com.leavey.mahjong.engine.bean.ActionRequest;
import com.leavey.mahjong.engine.bean.Game;
import com.leavey.mahjong.engine.bean.Operation;
import com.leavey.mahjong.engine.util.PositionUtil;

/**
 * @author Leavey
 */
public class EatExecutor implements Executor {
    @Override
    public Action supportAction() {
        return Action.EAT;
    }

    @Override
    public boolean validate(Game game, int player) {
        //上家打出普通牌 || 开杠后打的牌，作为下家可以吃牌
        Action prevAction = game.getPrevAction();
        return (prevAction == Action.PLAY || prevAction == Action.GANG_PLAY) && game.isFocusNext(player);
    }

    @Override
    public boolean validate(Game game, ActionRequest actionRequest) {
        //允许吃牌 && 要吃牌数量=1 && 要吃的牌类型允许吃牌 && 吃牌位置不能为空 && 吃牌位置合法
        return validate(game, actionRequest.getPlayer()) && actionRequest.getKeyTiles().size() == 1 && actionRequest.getKeyTiles().get(0).getType().isAllowEat() && actionRequest.getEatPosition() != null && PositionUtil.isValidEat(actionRequest.getKeyTiles().get(0), actionRequest.getEatPosition());
    }

    @Override
    public Operation execute(Game game, ActionRequest actionRequest) {
        game.eat(actionRequest.getPlayer(), actionRequest.getKeyTiles().get(0), actionRequest.getEatPosition());
        Operation operation = new Operation(actionRequest.getPlayer(), actionRequest.getAction());
        operation.setKeyTiles(actionRequest.getKeyTiles());
        operation.setEatPosition(actionRequest.getEatPosition());
        return operation;
    }
}
