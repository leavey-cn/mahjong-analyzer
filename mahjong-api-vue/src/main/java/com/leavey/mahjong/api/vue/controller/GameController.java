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

package com.leavey.mahjong.api.vue.controller;

import com.leavey.mahjong.api.vue.vo.*;
import com.leavey.mahjong.engine.bean.*;
import com.leavey.mahjong.engine.factory.GameFactory;
import com.leavey.mahjong.engine.rule.ChangShaRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Leavey
 */
@Slf4j
@RestController
public class GameController {

    @PostMapping("/games")
    public Overview newGame() {
        return Overview.of(GameFactory.newGame(new ChangShaRule()));
    }

    @PutMapping("/games")
    public Overview action(@RequestBody ActionVo actionVo) {
        long gameId = actionVo.getGameId();
        int player = actionVo.getPlayer();
        if (Action.DRAW == actionVo.getAction()) {
            //摸牌，前端将摸牌与开杠摸牌组合了，这里需要判断是什么摸牌
            if (GameFactory.get(gameId).getPrevAction() == Action.GANG) {
                GameFactory.play(gameId, new ActionRequest(player, Action.GANG_DRAW));
                executeGangPlayIfNecessary(gameId, player, actionVo.getPlaySelectors());
            } else {
                GameFactory.play(gameId, new ActionRequest(player, Action.DRAW));
                executePlayIfNecessary(gameId, player, actionVo.getPlaySelectors());
            }
        } else if (Action.PLAY == actionVo.getAction()) {
            //打牌，前端将打牌与开杠后打牌组合了，这里需要判断是什么打牌
            if (GameFactory.get(gameId).getPrevAction() == Action.GANG_DRAW) {
                GameFactory.play(gameId, new ActionRequest(player, Action.GANG_PLAY, actionVo.getPlaySelectors().toTiles()));
            } else {
                GameFactory.play(gameId, new ActionRequest(player, Action.PLAY, actionVo.getPlaySelectors().toTiles()));
            }
        } else if (Action.PEN == actionVo.getAction()) {
            //碰牌
            Tile target;
            List<Tile> tiles = actionVo.getSelectors().toTiles();
            Assert.isTrue(tiles.size() <= 1, "仅能碰一张牌");
            if (tiles.isEmpty()) {
                //查找上一次的打出牌
                tiles = GameFactory.get(gameId).getPrevKeyTiles();
                Assert.isTrue(tiles.size() == 1, "无法从之前的打出牌中找到要碰的牌");
            }
            target = tiles.get(0);
            //执行碰牌
            GameFactory.play(gameId, new ActionRequest(player, actionVo.getAction(), List.of(target)));
            executePlayIfNecessary(gameId, player, actionVo.getPlaySelectors());
        } else if (Action.GANG == actionVo.getAction()) {
            //杠牌
            Tile target;
            List<Tile> tiles = actionVo.getSelectors().toTiles();
            Assert.isTrue(tiles.size() <= 1, "仅能杠一张牌");
            if (tiles.isEmpty()) {
                //查找上一次的打出牌
                tiles = GameFactory.get(gameId).getPrevKeyTiles();
                Assert.isTrue(tiles.size() == 1, "无法从之前的打出牌中找到要杠的牌");
            }
            target = tiles.get(0);
            //执行杠牌
            GameFactory.play(gameId, new ActionRequest(player, actionVo.getAction(), List.of(target)));
            //自动摸x张杠牌
            GameFactory.play(gameId, new ActionRequest(player, Action.GANG_DRAW));
            executeGangPlayIfNecessary(gameId, player, actionVo.getPlaySelectors());
        } else if (Action.EAT == actionVo.getAction()) {
            GroupVo selectedGroup = actionVo.getEatGroups().getSelectedGroup();
            Tile tile;
            EatPosition eatPosition;
            if (selectedGroup == null) {
                List<Tile> tiles = GameFactory.get(gameId).getPrevKeyTiles();
                Assert.isTrue(tiles.size() == 1 && tiles.get(0).isBoundary(), "无法从之前的打出牌中找到要吃的牌");
                tile = tiles.get(0);
                eatPosition = tile.getValue() == 1 ? EatPosition.LEFT : EatPosition.RIGHT;
            } else {
                List<TileVo> tiles = selectedGroup.getTiles();
                tile = Tile.parseCode(tiles.get(1).getCode());
                if (tiles.get(0).getCode() > tiles.get(1).getCode()) {
                    eatPosition = EatPosition.LEFT;
                } else if (tiles.get(2).getCode() < tiles.get(1).getCode()) {
                    eatPosition = EatPosition.RIGHT;
                } else {
                    eatPosition = EatPosition.MIDDLE;
                }
            }
            GameFactory.play(gameId, new ActionRequest(player, Action.EAT, tile, eatPosition));
            executePlayIfNecessary(gameId, player, actionVo.getPlaySelectors());
        } else if (Action.WIN == actionVo.getAction()) {
            //胡牌
            Tile target;
            List<Tile> tiles = actionVo.getSelectors().toTiles();
            Assert.isTrue(tiles.size() <= 1, "仅能胡一张牌");
            if (tiles.isEmpty()) {
                //查找上一次的打出牌
                tiles = GameFactory.get(gameId).getPrevKeyTiles();
                Assert.isTrue(tiles.size() == 1, "无法从之前的打出牌中找到要胡的牌");
            }
            GameFactory.play(gameId, new ActionRequest(player, Action.WIN, tiles));
        } else if (Action.SHOW == actionVo.getAction()) {
            GameFactory.play(gameId, new ActionRequest(player, Action.SHOW, actionVo.getPlaySelectors().toTiles()));
        }
        return Overview.of(GameFactory.get(gameId));
    }

    private void executePlayIfNecessary(long gameId, int player, Selectors paySelectors) {
        List<Tile> playTiles = paySelectors.toTiles();
        Assert.isTrue(playTiles.size() <= 1, "仅能打出一张牌");
        if (!playTiles.isEmpty()) {
            //打出牌
            GameFactory.play(gameId, new ActionRequest(player, Action.PLAY, playTiles));
        }
    }

    private void executeGangPlayIfNecessary(long gameId, int player, Selectors paySelectors) {
        List<Tile> playTiles = paySelectors.toTiles();
        if (!playTiles.isEmpty()) {
            //打出牌
            GameFactory.play(gameId, new ActionRequest(player, Action.GANG_PLAY, playTiles));
        }
    }

    /**
     * 查询可以进行该操作的牌
     *
     * @param gameId 游戏ID
     * @param player 玩家ID
     * @param action 操作
     * @return 选择器
     */
    @GetMapping("/games/selectors")
    public Selectors getSelectors(long gameId, int player, Action action) {
        if (action == Action.PEN || action == Action.GANG) {


            Map<Type, List<Tile>> titleMap = Optional.ofNullable(GameFactory.get(gameId).getPrevOperation()).map(Operation::getKeyTiles).orElse(new ArrayList<>()).stream().collect(Collectors.groupingBy(Tile::getType));

        }
        return null;
    }
}
