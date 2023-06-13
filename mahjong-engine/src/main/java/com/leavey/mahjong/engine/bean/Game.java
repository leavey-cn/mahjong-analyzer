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

package com.leavey.mahjong.engine.bean;

import com.leavey.mahjong.engine.executor.Executor;
import com.leavey.mahjong.engine.factory.GameFactory;
import com.leavey.mahjong.engine.rule.Rule;
import com.leavey.mahjong.engine.util.PositionUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Leavey
 */
public class Game implements DeepCopy<Game> {
    private final long id;
    private final Player[] players;
    private final Rule rule;
    private final List<Tile> pool;
    /**
     * 牌堆数量
     */
    private int pileSize;
    private Integer focus;
    private Operation operation;
    private final Map<Action, Executor> executorMap;


    public Game(long id, Rule rule) {
        this.id = id;
        this.rule = rule;
        this.executorMap = GameFactory.defaultExecutorMap();
        rule.customizeExecutors(executorMap);
        this.pool = Arrays.stream(Type.values()).map(rule::newTiles).flatMap(Collection::stream).collect(Collectors.toList());
        this.pileSize = pool.size();
        this.players = new Player[this.rule.playerAmount()];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(rule.firstHandAmount());
            pileSize -= rule.firstHandAmount();
        }
        this.operation = null;
    }

    public long getId() {
        return id;
    }

    /**
     * 查询某个玩家当前可进行的操作
     *
     * @param player 玩家
     * @return 可进行操作集合
     */
    public Set<Action> allowActions(int player) {
        return executorMap.entrySet().stream().filter(entry -> entry.getValue().validate(this, player)).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    /**
     * 执行一个玩家的操作
     *
     * @param actionRequest 操作请求
     */
    public void play(ActionRequest actionRequest) {
        //本次操作的执行器
        Executor executor = executorMap.get(actionRequest.getAction());
        //校验操作是否有效
        if (!executor.validate(this, actionRequest)) {
            throw new IllegalStateException("非法操作");
        }
        //调用对应的操作执行器，执行操作
        Operation op = executor.execute(this, actionRequest);
        op.setPrev(this.operation);
        //记录本次日志
        this.operation = op;
        //焦点转移到当前操作人，明牌不设置Focus
        if (actionRequest.getAction() != Action.SHOW) {
            this.focus = actionRequest.getPlayer();
        }
    }

    private Game(long id, Player[] players, Rule rule, List<Tile> pool, int pileSize, Integer focus, Operation operation, Map<Action, Executor> executorMap) {
        this.id = id;
        this.players = players;
        this.rule = rule;
        this.pool = pool;
        this.pileSize = pileSize;
        this.focus = focus;
        this.operation = operation;
        this.executorMap = executorMap;
    }

    @Override
    public Game deepCopy() {
        Player[] copyPlayers = new Player[players.length];
        for (int i = 0; i < players.length; i++) {
            copyPlayers[i] = players[i].deepCopy();
        }
        return new Game(id, copyPlayers, rule, new ArrayList<>(pool), pileSize, focus, operation, executorMap);
    }

    public Integer getFocus() {
        return focus;
    }

    public boolean focusNotExist() {
        return focus == null;
    }

    public boolean isFocus(int player) {
        return focus != null && focus == player;
    }

    public boolean isFocusNext(int player) {
        return focus != null && PositionUtil.isNext(focus, player, getPlayerAmount());
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getPlayerAmount() {
        return players.length;
    }

    public Operation getPrevOperation() {
        if (operation == null) {
            return null;
        }
        Operation res = operation;
        while (res != null && res.getAction() == Action.SHOW) {
            //跳过明牌的日志
            res = res.getPrev();
        }
        return res;
    }


    public List<Tile> getPrevKeyTiles() {
        return Optional.ofNullable(getPrevOperation()).map(Operation::getKeyTiles).orElse(new ArrayList<>());
    }

    public Action getPrevAction() {
        return Optional.ofNullable(getPrevOperation()).map(Operation::getAction).orElse(null);
    }

    public Rule getRule() {
        return rule;
    }

    public void draw(int player) {
        players[player].draw();
        pileSize--;
    }

    public void draw(int player, Tile tile) {
        players[player].draw(tile);
        pileSize--;
    }

    public void play(int player, Tile tile) {
        players[player].play(tile);
    }

    public void eat(int player, Tile tile, EatPosition position) {
        players[player].eat(tile, position);
    }

    public void pen(int player, Tile tile) {
        players[player].pen(tile);
    }

    public int gangDraw(int player) {
        int amount = rule.allowGangDrawAmount();
        players[player].gangDraw(amount);
        pileSize -= amount;
        return amount;
    }

    public void gangPlay(int player, List<Tile> tiles) {
        players[player].gangPlay(tiles);
    }

    /**
     * 杠牌
     *
     * @param player  玩家
     * @param tile    牌
     * @param dark    是否暗杠
     * @param outside 要杠的牌从外部获取还是内部的
     *                明杠的牌、暗杠自身上次杠出的牌 是外部获取
     *                其他暗杠的牌 都是内部获取
     */
    public void gang(int player, Tile tile, boolean dark, boolean outside) {
        players[player].gang(tile, dark, outside);
    }

    /**
     * 胡牌
     *
     * @param player  玩家
     * @param tile    牌
     * @param outsize 牌是否外部的
     */
    public void win(int player, Tile tile, boolean outsize) {
        players[player].win(tile, outsize);
    }

    public void show(int player, List<Tile> tiles) {
        players[player].show(tiles);
    }
}
