package com.kgmyshin.ideaplugin.eventbus;

import com.intellij.usages.Usage;

/**
 * Created by kgmyshin on 2015/06/07.
 */
public interface Filter {
    boolean shouldShow(Usage usage);
}
