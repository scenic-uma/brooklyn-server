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


import org.apache.brooklyn.api.entity.EntitySpec;


public class EntitySpecRelation implements RelationshipType<EntitySpec,EntitySpec> {

    private final String sourceTypeName;
    private final String sourceName;
    private final String sourceNamePlural;
    private final String targetName;
    private final String targetNamePlural;
    private final String targetTypeName;

    public EntitySpecRelation(String sourceTypeName,
                              String sourceName,
                              String sourceNamePlural,
                              String targetName,
                              String targetNamePlural,
                              String targetTypeName) {
        this.sourceTypeName = sourceTypeName;
                this.sourceName = sourceName;
        this.sourceNamePlural = sourceNamePlural;
                this.targetName = targetName;
        this.targetNamePlural = targetNamePlural;
                this.targetTypeName = targetTypeName;
    }

    @Override
    public String getRelationshipTypeName() {
        return sourceTypeName;
    }

    @Override
    public Class<EntitySpec> getSourceType() {
        return EntitySpec.class;
    }

    @Override
    public Class<EntitySpec> getTargetType() {
        return EntitySpec.class;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public String getSourceNamePlural() {
        return sourceNamePlural;
    }

    @Override
    public String getTargetName() {
        return targetName;
    }

    @Override
    public String getTargetNamePlural() {
        return targetNamePlural;
    }

    @Override
    public RelationshipType<EntitySpec, EntitySpec> getInverseRelationshipType() {
        return new EntitySpecRelation(targetTypeName,
                targetName,
                targetNamePlural,
                sourceName,
                sourceNamePlural, targetTypeName);
    }
}
