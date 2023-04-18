package com.mlytics.mlysdk.driver

import com.mlytics.mlysdk.kernel.core.KernelConfigurator
import com.mlytics.mlysdk.kernel.core.KernelValidator

//import Foundation
class DriverConfigurator(var options: MLYDriverOptions?) {
    fun config() {
        KernelValidator(this.options).verify()
        KernelConfigurator(this.options).config()
    }
}
 class MLYDriverOptions(
    var client: MLYClientOptions = MLYClientOptions(),
    var debug: Boolean = false,
    var server: MLYServerOptioins = MLYServerOptioins()
)
 class MLYClientOptions(
    var id: String? = null
)
class MLYServerOptioins(
    var fqdn: String? = null
)