package org.xwalk.core.internal;

import org.chromium.components.navigation_interception.NavigationParams;

interface XWalkNavigationHandler {
    boolean handleNavigation(NavigationParams navigationParams);
}
