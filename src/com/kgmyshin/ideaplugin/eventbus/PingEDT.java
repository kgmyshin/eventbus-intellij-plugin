/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kgmyshin.ideaplugin.eventbus;

import com.intellij.openapi.util.Condition;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by kgmyshin on 2015/06/07.
 */
public class PingEDT {
    private final String myName;
    private final Runnable pingAction;
    private volatile boolean stopped;
    private volatile boolean pinged;
    private final Condition<?> myShutUpCondition;
    private final int myMaxUnitOfWorkThresholdMs; //-1 means indefinite

    private final AtomicBoolean invokeLaterScheduled = new AtomicBoolean();
    private final Runnable myUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            boolean b = invokeLaterScheduled.compareAndSet(true, false);
            assert b;
            if (stopped || myShutUpCondition.value(null)) {
                stop();
                return;
            }
            long start = System.currentTimeMillis();
            int processed = 0;
            while (true) {
                if (processNext()) {
                    processed++;
                }
                else {
                    break;
                }
                long finish = System.currentTimeMillis();
                if (myMaxUnitOfWorkThresholdMs != -1 && finish - start > myMaxUnitOfWorkThresholdMs) break;
            }
            if (!isEmpty()) {
                scheduleUpdate();
            }
        }
    };

    public PingEDT(@NotNull @NonNls String name, @NotNull Condition<?> shutUpCondition,
                   int maxUnitOfWorkThresholdMs, @NotNull Runnable pingAction) {
        myName = name;
        myShutUpCondition = shutUpCondition;
        myMaxUnitOfWorkThresholdMs = maxUnitOfWorkThresholdMs;
        this.pingAction = pingAction;
    }

    private boolean isEmpty() {
        return !pinged;
    }

    private boolean processNext() {
        pinged = false;
        pingAction.run();
        return pinged;
    }

    // returns true if invokeLater was called
    public boolean ping() {
        pinged = true;
        return scheduleUpdate();
    }

    // returns true if invokeLater was called
    private boolean scheduleUpdate() {
        if (!stopped && invokeLaterScheduled.compareAndSet(false, true)) {
            SwingUtilities.invokeLater(myUpdateRunnable);
            return true;
        }
        return false;
    }

    public void stop() {
        stopped = true;
    }
}
