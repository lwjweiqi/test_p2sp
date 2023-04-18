package com.mlytics.mlysdk.kernel.core.base

import com.mlytics.mlysdk.util.Component

object BaseComponentName {
    const val SYSTEM = "system"
    const val METRICS = "metrics"
    const val REPORT = "report"
    const val FILER = "filer"
}

object BaseComponentBundle {
    val REQUIRED = listOf<String>(
        BaseComponentName.SYSTEM,
        BaseComponentName.METRICS,
        BaseComponentName.REPORT,
        BaseComponentName.FILER
    )
}

object ComponentDependencies {

    val components = mutableMapOf<Component, List<Component>>(
//        SystemComponent to listOf<Component>(),
//        MCDNComponent to listOf<Component>(
//            SystemComponent
//        )
    )
}

object ComponentActivate {
    val components = mutableListOf<Component>(
//        SystemComponent
//        , MCDNComponent
//        ,CacheComponent
//        ,OriginComponent
//        ,TrackerComponent
//        ,SwarmComponent
    )
}