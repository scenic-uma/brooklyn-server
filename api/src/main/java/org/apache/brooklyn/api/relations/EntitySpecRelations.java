/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.brooklyn.api.relations;

import java.util.List;
import java.util.Map;

import org.apache.brooklyn.api.entity.EntitySpec;
import org.apache.brooklyn.util.collections.MutableList;
import org.apache.brooklyn.util.collections.MutableMap;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;


public class EntitySpecRelations {

    public final static EntitySpecRelation TARGET_TYPE =
            new EntitySpecRelation("has_target", "targetter", "targetters", "target", "targets", "target_by");

    private final Map<EntitySpecRelation, List<EntitySpec>> relations;
    private final EntitySpec source;

    public EntitySpecRelations(EntitySpec source) {
        this.source = source;
        this.relations = MutableMap.of();
    }

    public void addTargetRelationSpec(EntitySpec targetSpec) {
        addRelationSpec(TARGET_TYPE, targetSpec);
    }

    public void addTargetRelationSpec(List<EntitySpec> targetSpecs) {
        for (EntitySpec targetSpec : targetSpecs) {
            addRelationSpec(TARGET_TYPE, targetSpec);
        }
    }

    public Optional<List<EntitySpec>> getRelations(EntitySpecRelation relation) {
        return Optional.fromNullable(relations.get(relation));
    }

    public Map<EntitySpecRelation, List<EntitySpec>> relations() {
        return ImmutableMap.copyOf(this.relations);
    }

    private void addRelationSpec(EntitySpecRelation relationType, EntitySpec targetSpec) {
        if (!relations.containsKey(relationType)) {
            addRelationType(relationType);
        }
        addTargetSpec(relationType, targetSpec);
    }

    private void addTargetSpec(EntitySpecRelation relationType, EntitySpec targetSpec) {
        List<EntitySpec> currentRelations = relations.get(relationType);
        currentRelations.add(targetSpec);
    }

    private void addRelationType(EntitySpecRelation target) {
        relations.put(target, MutableList.<EntitySpec>of());
    }

    public int numberOfRelations() {
        return relations.size();
    }


}
