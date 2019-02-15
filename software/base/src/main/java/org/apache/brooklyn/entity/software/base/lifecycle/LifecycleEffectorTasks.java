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
package org.apache.brooklyn.entity.software.base.lifecycle;


import com.google.common.annotations.Beta;
import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.api.location.Location;
import org.apache.brooklyn.util.core.config.ConfigBag;

import javax.annotation.Nullable;
import java.util.Collection;

@Beta
public interface LifecycleEffectorTasks {

    void attachLifecycleEffectors(Entity entity);

    void start(Collection<? extends Location> locations);

    void restart(ConfigBag parameters);

    void stop(ConfigBag paramters);

    void suspend(ConfigBag paramters);

    @Deprecated
    void stop();

    Location getLocation(@Nullable Collection<? extends Location> locations);
}
