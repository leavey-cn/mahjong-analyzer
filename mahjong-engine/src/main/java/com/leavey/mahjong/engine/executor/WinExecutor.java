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

/**
 * @author Leavey
 */
public class WinExecutor implements Executor {
    @Override
    public Action supportAction() {
        return Action.WIN;
    }

    @Override
    public boolean validate(Game game, int player) {
        Action action = game.getPrevAction();
        if (action == Action.GANG_PLAY) {
            //任意一家杠出的牌
            return true;
        }
        if (!game.isFocus(player) && action == Action.PLAY) {
            //其他家打出的牌
            return true;
        }
        //自身刚摸了牌
        return game.isFocus(player) && action == Action.DRAW;
    }

    @Override
    public boolean validate(Game game, ActionRequest actionRequest) {
        return validate(game, actionRequest.getPlayer()) && actionRequest.getKeyTiles().size() == 1;
    }

    @Override
    public Operation execute(Game game, ActionRequest actionRequest) {
        //是否自摸
        boolean self;
        //胡的牌是否来自外部，杠上开花时特殊处理，胡的牌来自外部，上一个操作是自身打出杠牌，为了清理玩家手中的杠牌引用
        boolean outside;
        Action action = game.getPrevAction();
        if (game.isFocus(actionRequest.getPlayer()) && action == Action.DRAW) {
            //自摸
            self = true;
            outside = false;
        } else if (game.isFocus(actionRequest.getPlayer()) && action == Action.GANG_PLAY) {
            //杠上开花
            self = true;
            outside = true;
        } else if (!game.isFocus(actionRequest.getPlayer()) && (action == Action.PLAY || action == Action.GANG_PLAY)) {
            //接炮，接杠上炮
            self = false;
            outside = true;
        } else {
            throw new IllegalStateException("数据异常，当前无法胡牌");
        }
        game.win(actionRequest.getPlayer(), actionRequest.getKeyTiles().get(0), outside);
        Operation operation = new Operation(actionRequest.getPlayer(), actionRequest.getAction(), actionRequest.getKeyTiles());
        operation.setSelf(self);
        return operation;
    }
}
