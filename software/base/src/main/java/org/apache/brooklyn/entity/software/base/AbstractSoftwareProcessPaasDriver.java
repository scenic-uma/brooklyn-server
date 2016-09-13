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
package org.apache.brooklyn.entity.software.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.brooklyn.api.entity.EntityLocal;
import org.apache.brooklyn.api.location.Location;
import org.apache.brooklyn.location.paas.PaasLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.Beta;

@Beta
public abstract class AbstractSoftwareProcessPaasDriver implements SoftwareProcessDriver {

    public static final Logger log = LoggerFactory
            .getLogger(AbstractSoftwareProcessPaasDriver.class);

    private final PaasLocation location;
    protected final EntityLocal entity;

    public AbstractSoftwareProcessPaasDriver(EntityLocal entity,
                                                     PaasLocation location) {
        this.entity = checkNotNull(entity, "entity");
        this.location = checkNotNull(location, "location");
        init();
    }

    protected void init() {
    }

    @Override
    public EntityLocal getEntity() {
        return entity;
    }

    @Override
    public Location getLocation() {
        return location;
    }

}
