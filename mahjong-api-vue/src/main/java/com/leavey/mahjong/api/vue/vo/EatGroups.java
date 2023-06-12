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

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Leavey
 */
@Data
@NoArgsConstructor
public class EatGroups {
    private int selected;
    List<GroupVo> groups;

    public EatGroups(List<GroupVo> groups) {
        this.selected = -1;
        this.groups = groups;
    }

    public GroupVo getSelectedGroup() {
        if (selected != -1) {
            return this.groups.get(selected);
        }
        return null;
    }
}
