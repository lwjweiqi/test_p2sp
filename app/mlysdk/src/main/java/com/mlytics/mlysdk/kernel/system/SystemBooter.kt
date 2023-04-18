package com.mlytics.mlysdk.kernel.system

import com.mlytics.mlysdk.kernel.core.servant.MCDNInitializer
import com.mlytics.mlysdk.util.Component
import kotlin.reflect.KClass

class SystemBooter {

    fun register(component: Component, vararg dependence: KClass<out Component>) {
        val cls = component::class
        this.activates[cls] = component
        this.dependencies[cls] = dependence.asList()
    }

    var dependencies = mutableMapOf<KClass<out Component>, List<KClass<out Component>>>()
    var activates = mutableMapOf<KClass<out Component>, Component>()

    var isActivated = false

    suspend fun activate() {
        this.isActivated = true
        activateAll()
    }

    private suspend fun activateAll() {
        val keys = activates.values.toSet()
        keys.forEach {
            activate(it)
        }
    }

    private suspend fun activate(component: Component) {
        if (!this.isActivated) {
            return
        }
        if (component.isActivated) {
            return
        }
        val cls = component::class
        dependencies[cls]?.forEach {
            activates[it]?.apply {
                activate(this)
            }
        }
        component.activate()
    }

    suspend fun deactivate() {
        if (!this.isActivated) {
            return
        }
        this.isActivated = false
        MCDNInitializer().process()
        activates.forEach {
            it.value.deactivate()
            activates.remove(it.key)
        }
    }

}
