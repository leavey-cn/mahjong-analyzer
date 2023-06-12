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
public class GangExecutor implements Executor {
    @Override
    public Action supportAction() {
        return Action.GANG;
    }

    @Override
    public boolean validate(Game game, int player) {
        return allowGang(game, player) || allowDarkGang(game, player);
    }

    private boolean allowGang(Game game, int player) {
        Action action = game.getPrevAction();
        //其他家打牌，打出杠牌，允许开杠，且为明杠
        return !game.isFocus(player) && (action == Action.PLAY || action == Action.GANG_PLAY);
    }

    private boolean allowDarkGang(Game game, int player) {
        Action action = game.getPrevAction();
        //自身摸牌、吃牌、碰牌后，允许开杠，且为暗杠
        if (game.isFocus(player) && (action == Action.DRAW || action == Action.EAT || action == Action.PEN)) {
            return true;
        }
        //杠自身之前杠出的牌，允许开杠，且为暗杠
        return game.isFocus(player) && action == Action.GANG_PLAY;
    }


    @Override
    public boolean validate(Game game, ActionRequest actionRequest) {
        if (actionRequest.getKeyTiles().size() != 1) {
            //要杠的牌不存在
            return false;
        }
        if (allowGang(game, actionRequest.getPlayer())) {
            //明杠情况下，要杠的牌需是人家打出的牌
            return game.getPrevOperation().getKeyTiles().contains(actionRequest.getKeyTiles().get(0));
        } else {
            //暗杠的情况下
            //从手牌开杠，杠牌不限制
            return true;
        }
    }

    @Override
    public Operation execute(Game game, ActionRequest actionRequest) {
        boolean dark = allowDarkGang(game, actionRequest.getPlayer());
        boolean outside = true;
        Action action = game.getPrevAction();
        if (dark && (action == Action.DRAW || action == Action.EAT || action == Action.PEN)) {
            //暗杠，且前一个操作是自身摸牌，吃牌，碰牌，开杠的4张牌都在手里
            //此处不需要再判断是否自身，因为dark的变量为true已经代表自身了
            outside = false;
        }
        game.gang(actionRequest.getPlayer(), actionRequest.getKeyTiles().get(0), dark, outside);
        Operation operation = new Operation(actionRequest.getPlayer(), actionRequest.getAction(), actionRequest.getKeyTiles());
        operation.setDark(dark);
        return operation;
    }
}
