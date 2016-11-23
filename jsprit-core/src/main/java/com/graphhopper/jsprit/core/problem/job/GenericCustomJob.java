/*
 * Licensed to GraphHopper GmbH under one or more contributor
 * license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * GraphHopper GmbH licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.graphhopper.jsprit.core.problem.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.solution.route.activity.DeliveryActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.ExchangeActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.JobActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.PickupActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.ServiceActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindows;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindowsImpl;

/**
 * Created by schroeder on 16/11/16.
 *
 * @author schroeder
 * @author balage
 */
public class GenericCustomJob extends AbstractJob {


    public static abstract class BuilderBase<T extends GenericCustomJob, B extends GenericCustomJob.BuilderBase<T, B>>
    extends JobBuilder<T, B> {

        private enum ActivityType {
            SERVICE {

                @Override
                public JobActivity create(GenericCustomJob job, BuilderBase<?, ?>.BuilderActivityInfo info) {
                    return new ServiceActivity(job, info.getName() == null ? name().toLowerCase() : info.getName(),
                                    info.getLocation(), info.getOperationTime(), info.getSize(), prepareTimeWindows(info));
                }
            },
            PICKUP {

                @Override
                public JobActivity create(GenericCustomJob job, BuilderBase<?, ?>.BuilderActivityInfo info) {
                    return new PickupActivity(job, info.getName() == null ? name().toLowerCase() : info.getName(),
                                    info.getLocation(), info.getOperationTime(), info.getSize(), prepareTimeWindows(info));
                }
            },
            DELIVERY {

                @Override
                public JobActivity create(GenericCustomJob job, BuilderBase<?, ?>.BuilderActivityInfo info) {
                    return new DeliveryActivity(job, info.getName() == null ? name().toLowerCase() : info.getName(),
                                    info.getLocation(), info.getOperationTime(), info.getSize(), prepareTimeWindows(info));
                }
            },
            EXCHANGE {

                @Override
                public JobActivity create(GenericCustomJob job, BuilderBase<?, ?>.BuilderActivityInfo info) {
                    return new ExchangeActivity(job, info.getName() == null ? name().toLowerCase() : info.getName(),
                                    info.getLocation(), info.getOperationTime(), info.getSize(), prepareTimeWindows(info));
                }
            };

            public abstract JobActivity create(GenericCustomJob job, BuilderBase<?, ?>.BuilderActivityInfo builderActivityInfo);

            private static Collection<TimeWindow> prepareTimeWindows(BuilderBase<?, ?>.BuilderActivityInfo info) {
                TimeWindows tws = info.getTimeWindows();
                if (tws.getTimeWindows().isEmpty()) {
                    tws = TimeWindows.ANY_TIME;
                }
                return tws.getTimeWindows();
            }
        }

        public class BuilderActivityInfo {
            private ActivityType type;
            private Location locs;
            private SizeDimension size = SizeDimension.EMPTY;
            private String name = null;
            private double operationTime = 0;
            private TimeWindowsImpl timeWindows = new TimeWindowsImpl();

            private BuilderActivityInfo(ActivityType type, Location locs) {
                super();
                this.type = type;
                this.locs = locs;
            }

            public ActivityType getType() {
                return type;
            }

            public Location getLocation() {
                return locs;
            }

            public SizeDimension getSize() {
                return size;
            }

            public BuilderActivityInfo withSize(SizeDimension size) {
                this.size = size;
                return this;
            }

            public String getName() {
                return name;
            }

            public BuilderActivityInfo withName(String name) {
                this.name = name;
                return this;
            }

            public TimeWindows getTimeWindows() {
                return timeWindows;
            }

            public BuilderActivityInfo withTimeWindow(TimeWindow timeWindow) {
                timeWindows.add(timeWindow);
                return this;
            }

            public BuilderActivityInfo withTimeWindows(TimeWindow... tws) {
                timeWindows.addAll(tws);
                return this;
            }

            public BuilderBase<T, B> finish() {
                return BuilderBase.this;
            }

            public double getOperationTime() {
                return operationTime;
            }

            public BuilderActivityInfo withOperationTime(double operationTime) {
                this.operationTime = operationTime;
                return this;
            }

        }

        List<BuilderActivityInfo> acts = new ArrayList<>();

        public BuilderBase(String id) {
            super(id);
        }

        public BuilderActivityInfo addService(Location location) {
            BuilderActivityInfo act = new BuilderActivityInfo(ActivityType.SERVICE, location);
            acts.add(act);
            return act;
        }

        public BuilderActivityInfo addPickup(Location location) {
            BuilderActivityInfo act = new BuilderActivityInfo(ActivityType.PICKUP, location);
            acts.add(act);
            return act;
        }

        public BuilderActivityInfo addDelivery(Location location) {
            BuilderActivityInfo act = new BuilderActivityInfo(ActivityType.DELIVERY, location);
            acts.add(act);
            return act;
        }

        public BuilderActivityInfo addExchange(Location location) {
            BuilderActivityInfo act = new BuilderActivityInfo(ActivityType.EXCHANGE, location);
            acts.add(act);
            return act;
        }

        //        private void add(ActivityType type, Location location, SizeDimension size, String name, Collection<TimeWindow> tws) {
        //            BuilderActivityInfo builderActivityInfo = new BuilderActivityInfo(type, location);
        //            if (name != null) {
        //                builderActivityInfo.setName(name);
        //            }
        //            if (size != null) {
        //                builderActivityInfo.setSize(size);
        //            }
        //            if (tws != null) {
        //                builderActivityInfo.addTimeWindows(tws);
        //            }
        //
        //            acts.add(builderActivityInfo);
        //        }
        //
        //        // Service
        //
        //        public GenericCustomJob.BuilderBase<T, B> addService(Location location, SizeDimension size, String name,
        //                        Collection<TimeWindow> tws) {
        //            add(ActivityType.SERVICE, location, size, name, tws);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addService(Location location, SizeDimension size, String name, TimeWindow tw) {
        //            add(ActivityType.SERVICE, location, size, name, Collections.singleton(tw));
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addService(Location location, SizeDimension size, Collection<TimeWindow> tws) {
        //            add(ActivityType.SERVICE, location, size, null, tws);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addService(Location location, SizeDimension size, TimeWindow tw) {
        //            add(ActivityType.SERVICE, location, size, null, Collections.singleton(tw));
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addService(Location location, SizeDimension size, String name) {
        //            add(ActivityType.SERVICE, location, size, name, null);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addService(Location location, SizeDimension size) {
        //            add(ActivityType.SERVICE, location, size, null, null);
        //            return this;
        //        }
        //
        //        // Pickup
        //
        //        public GenericCustomJob.BuilderBase<T, B> addPickup(Location location, SizeDimension size, String name,
        //                        Collection<TimeWindow> tws) {
        //            add(ActivityType.PICKUP, location, size, name, tws);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addPickup(Location location, SizeDimension size, String name, TimeWindow tw) {
        //            add(ActivityType.PICKUP, location, size, name, Collections.singleton(tw));
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addPickup(Location location, SizeDimension size, Collection<TimeWindow> tws) {
        //            add(ActivityType.PICKUP, location, size, null, tws);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addPickup(Location location, SizeDimension size, TimeWindow tw) {
        //            add(ActivityType.PICKUP, location, size, null, Collections.singleton(tw));
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addPickup(Location location, SizeDimension size, String name) {
        //            add(ActivityType.PICKUP, location, size, name, null);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addPickup(Location location, SizeDimension size) {
        //            add(ActivityType.PICKUP, location, size, null, null);
        //            return this;
        //        }
        //
        //        // Delivery
        //
        //        public GenericCustomJob.BuilderBase<T, B> addDelivery(Location location, SizeDimension size, String name,
        //                        Collection<TimeWindow> tws) {
        //            add(ActivityType.DELIVERY, location, size, name, tws);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addDelivery(Location location, SizeDimension size, String name, TimeWindow tw) {
        //            add(ActivityType.DELIVERY, location, size, name, Collections.singleton(tw));
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addDelivery(Location location, SizeDimension size, Collection<TimeWindow> tws) {
        //            add(ActivityType.DELIVERY, location, size, null, tws);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addDelivery(Location location, SizeDimension size, TimeWindow tw) {
        //            add(ActivityType.DELIVERY, location, size, null, Collections.singleton(tw));
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addDelivery(Location location, SizeDimension size, String name) {
        //            add(ActivityType.DELIVERY, location, size, name, null);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addDelivery(Location location, SizeDimension size) {
        //            add(ActivityType.DELIVERY, location, size, null, null);
        //            return this;
        //        }
        //
        //        // Exchange
        //
        //        public GenericCustomJob.BuilderBase<T, B> addExchange(Location location, SizeDimension size, String name,
        //                        Collection<TimeWindow> tws) {
        //            add(ActivityType.EXCHANGE, location, size, name, tws);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addExchange(Location location, SizeDimension size, String name, TimeWindow tw) {
        //            add(ActivityType.EXCHANGE, location, size, name, Collections.singleton(tw));
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addExchange(Location location, SizeDimension size, Collection<TimeWindow> tws) {
        //            add(ActivityType.EXCHANGE, location, size, null, tws);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addExchange(Location location, SizeDimension size, TimeWindow tw) {
        //            add(ActivityType.EXCHANGE, location, size, null, Collections.singleton(tw));
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addExchange(Location location, SizeDimension size, String name) {
        //            add(ActivityType.EXCHANGE, location, size, name, null);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addExchange(Location location, SizeDimension size) {
        //            add(ActivityType.EXCHANGE, location, size, null, null);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addExchange(Location location, String name, Collection<TimeWindow> tws) {
        //            add(ActivityType.EXCHANGE, location, null, name, tws);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addExchange(Location location, String name, TimeWindow tw) {
        //            add(ActivityType.EXCHANGE, location, null, name, Collections.singleton(tw));
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addExchange(Location location, Collection<TimeWindow> tws) {
        //            add(ActivityType.EXCHANGE, location, null, null, tws);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addExchange(Location location, TimeWindow tw) {
        //            add(ActivityType.EXCHANGE, location, null, null, Collections.singleton(tw));
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addExchange(Location location, String name) {
        //            add(ActivityType.EXCHANGE, location, null, name, null);
        //            return this;
        //        }
        //
        //        public GenericCustomJob.BuilderBase<T, B> addExchange(Location location) {
        //            add(ActivityType.EXCHANGE, location, null, null, null);
        //            return this;
        //        }

        @Override
        protected void validate() {
            if (acts.isEmpty()) {
                throw new IllegalStateException("There is no activities defined for the job.");
            }
        }

        public List<BuilderActivityInfo> getActs() {
            return Collections.unmodifiableList(acts);
        }

    }

    public static final class Builder extends GenericCustomJob.BuilderBase<GenericCustomJob, GenericCustomJob.Builder> {

        public static GenericCustomJob.Builder newInstance(String id) {
            return new GenericCustomJob.Builder(id);
        }

        public Builder(String id) {
            super(id);
        }

        @Override
        protected GenericCustomJob createInstance() {
            return new GenericCustomJob(this);
        }

    }

    /**
     * Builder based constructor.
     *
     * @param builder The builder instance.
     * @see JobBuilder
     */
    protected GenericCustomJob(JobBuilder<?, ?> builder) {
        super(builder);

    }

    @Override
    public SizeDimension getSize() {
        return SizeDimension.EMPTY;
    }

    @Override
    protected void createActivities(JobBuilder<? extends AbstractJob, ?> jobBuilder) {
        GenericCustomJob.Builder builder = (GenericCustomJob.Builder) jobBuilder;
        JobActivityList list = new SequentialJobActivityList(this);
        for (GenericCustomJob.Builder.BuilderActivityInfo info : builder.getActs()) {
            JobActivity act = info.getType().create(this, info);
            list.addActivity(act);
        }
        setActivities(list);
    }
}

