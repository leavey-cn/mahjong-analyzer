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

package com.leavey.mahjong.api.vue.vo;

import com.leavey.mahjong.common.bean.Tile;
import com.leavey.mahjong.engine.bean.*;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Leavey
 */
@Data
public class Overview {
    private long gameId;
    private List<PlayerVo> players;
    private EatGroups eatGroups;
    private Selectors selectors;
    private Selectors drawSelectors;
    private Selectors playSelectors;

    public static Overview of(Game game) {
        Overview ov = new Overview();
        ov.gameId = game.getId();
        List<PlayerVo> players = new ArrayList<>();
        for (int i = 0; i < game.getPlayers().length; i++) {
            players.add(of(i, game));
        }
        ov.players = players;
        ov.eatGroups = eatGroups(game);
        ov.selectors = Selectors.getPrevKeyTileSelectors(game);
        ov.drawSelectors = Selectors.fullSelectors();
        ov.playSelectors = Selectors.fullSelectors();
        return ov;
    }

    private static EatGroups eatGroups(Game game) {
        List<GroupVo> groups = Optional.ofNullable(game.getPrevOperation()).map(Operation::getKeyTiles).orElse(new ArrayList<>()).stream().filter(Tile::canEat).map(GroupVo::createEatGroups).flatMap(Collection::stream).collect(Collectors.toList());
        return new EatGroups(groups);
    }

    private static PlayerVo of(int id, Game game) {
        PlayerVo vo = new PlayerVo();
        vo.setId(id);
        Set<Action> actions = game.allowActions(id);
        if (actions.contains(Action.GANG_DRAW)) {
            actions.remove(Action.GANG_DRAW);
            actions.add(Action.DRAW);
        }
        if (actions.contains(Action.GANG_PLAY)) {
            actions.remove(Action.GANG_PLAY);
            actions.add(Action.PLAY);
        }
        vo.setActions(actions.stream().sorted(Comparator.comparingInt(Enum::ordinal)).map(Enum::toString).collect(Collectors.toList()));

        Player player = game.getPlayers()[id];

        List<TileVo> handTiles = player.getCompleteGroups().stream().flatMap(group -> group.getTiles().stream()).map(TileVo::view).collect(Collectors.toList());
        handTiles.addAll(IntStream.range(0, player.getHand().getDarkAmount()).mapToObj(i -> new TileVo(0, TileVo.VIEW, 0)).collect(Collectors.toList()));
        handTiles.addAll(player.getHand().getOpenTiles().stream().map(TileVo::view).collect(Collectors.toList()));
        vo.setHandTiles(handTiles);

        vo.setPlayTiles(player.getPlayTiles().stream().map(TileVo::view).collect(Collectors.toList()));
        return vo;
    }
}
