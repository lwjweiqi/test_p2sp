package com.mlytics.mlysdk.kernel.core.infra

import com.mlytics.mlysdk.kernel.core.KernelSettings
import com.mlytics.mlysdk.kernel.core.api.MeteringAPICreateCDNDownloadMeteringHandler
import com.mlytics.mlysdk.kernel.core.api.MeteringAPICreateP2PDownloadMeteringHandler
import com.mlytics.mlysdk.kernel.core.api.model.MeteringAPICreateCDNDownloadMeteringContent
import com.mlytics.mlysdk.kernel.core.api.model.MeteringAPICreateCDNDownloadMeteringContentItem
import com.mlytics.mlysdk.kernel.core.api.model.MeteringAPICreateP2PDownloadMeteringContent
import com.mlytics.mlysdk.kernel.core.api.model.MeteringAPICreateP2PDownloadMeteringContentItem
import com.mlytics.mlysdk.kernel.core.infra.metrics.state.MetricsStatsHolder
import com.mlytics.mlysdk.kernel.core.infra.model.HTTPDownloadRecord
import com.mlytics.mlysdk.kernel.core.infra.model.P2PDownloadRecord
import com.mlytics.mlysdk.kernel.core.infra.model.ReportConstant
import com.mlytics.mlysdk.kernel.core.model.service.Resource
import com.mlytics.mlysdk.util.AbstractFlow
import com.mlytics.mlysdk.util.Component
import com.mlytics.mlysdk.util.DateTool
import com.mlytics.mlysdk.util.DownloaderState
import com.mlytics.mlysdk.util.HttpDownloader
import com.mlytics.mlysdk.util.HttpDownloaderListener
import com.mlytics.mlysdk.util.TaskManager

object ReportSubmitterTaskName {

    val HANDLE_CDN_DOWNLOAD_REPORT = "report submitter: handle cdn download report"

    val HANDLE_P2P_DOWNLOAD_REPORT = "report submitter: handle p2p download report"
}

class ReportSubmitter {
    private var _taskManager: TaskManager<Any> = TaskManager()
    suspend fun activate() {
        this._taskManager.activate()
        this._buildTasks()
    }

    suspend fun deactivate() {
        this._taskManager.deactivate()
    }

    private suspend fun _buildTasks() {
        if (KernelSettings.report.isEnabled && Math.random() < KernelSettings.report.sampleRate) {
            this._buildHandleCDNDownloadReportTask()
            this._buildHandleP2PDownloadReportTask()
        }

    }

    private suspend fun _buildHandleCDNDownloadReportTask() {
        this._taskManager.createCyclicTask(
            ReportSubmitterTaskName.HANDLE_CDN_DOWNLOAD_REPORT,
            CDNDownloadReportHandler(CDNDownloadReportHandlerContent()),
            sleepFirst = true,
            sleepSeconds = 10.0,
            sleepJitter = 0.0,
            maxErrorRetry = -1,
            maxTotalRetry = -1
        )
    }

    private suspend fun _buildHandleP2PDownloadReportTask() {
        this._taskManager.createCyclicTask(
            ReportSubmitterTaskName.HANDLE_P2P_DOWNLOAD_REPORT,
            P2PDownloadReportHandler(P2PDownloadReportHandlerContent()),
            sleepSeconds = 10.0
        )
    }

}

data class CDNDownloadReportDutyContent(
    var cursor_data: HTTPDownloadRecord? = null,
    var reports: MutableList<HTTPDownloadRecord> = mutableListOf(),
    var reportTime: Double = 0.0,
    var isAborted: Boolean = false

)

data class P2PDownloadReportDutyContent(
    var cursor_data: P2PDownloadRecord? = null,
    var reports: MutableList<P2PDownloadRecord> = mutableListOf(),
    var reportTime: Double = 0.0,
    var isAborted: Boolean = false
)

object CDNDownloadReportListener : Component(), HttpDownloaderListener {

    fun key(): String {
        return this::class.simpleName!!
    }

    override suspend fun activate() {
        HttpDownloader.listeners[this.key()] = this
    }

    override suspend fun deactivate() {
        HttpDownloader.listeners.remove(this.key())
    }

    override fun onStateChanged(downloader: HttpDownloader, state: DownloaderState) {

        if (state != DownloaderState.Completed && state != DownloaderState.Failed) {
            return
        }

        downloader.downloadTime.stop()
        val uri = downloader.uri
        val range = downloader.range

        var record = HTTPDownloadRecord().apply {
            id = Resource.makeID(uri.toString(), range)
            contentSize = downloader.contentLength ?: downloader.readLength
            startTime = downloader.connectTime.startTime / 1000.0
            elapsedTime = downloader.downloadTime.stop() / 1000.0
            isSuccess = state == DownloaderState.Completed
            isComplete = state == DownloaderState.Completed
            swarmURI = null
            swarmID = null
            requestURI = uri.toString()
            responseCode = downloader.response?.code
            errorMessage = downloader.error?.message
            algorithmID = KernelSettings.platforms.algorithm_id
            algorithmVersion = KernelSettings.platforms.algorithm_ver
        }

        MetricsStatsHolder.source?.http_download_records?.add(record)
    }
}

class CDNDownloadReportHandler(content: CDNDownloadReportHandlerContent) :
    AbstractFlow<CDNDownloadReportHandlerContent>(content) {

    var index = 0

    override suspend fun process() {
        this._intakeIndex()
        this._injectReports()
        while (this._shouldSubmit) {
            this._submitReports()
        }
    }

    suspend fun _intakeIndex() {

    }

    suspend fun _injectReports() {
        val records = MetricsStatsHolder.source?.http_download_records ?: return
        this._content.reports.addAll(records.subList(index, records.size))
        index += this._content.reports.size
        this._content.cursor_data = records.last()
    }

    var _shouldSubmit: Boolean = false
        get() {
            return this._content.isAborted && this._content.reports.size > 0 && (this._content.reports.size >= ReportConstant.CDN_DOWNLOAD_REPORT_BATCH_SIZE || DateTool.now() - this._content.reportTime >= ReportConstant.MAX_DURATION_OF_REPORT_SUBMISSION)
        }

    suspend fun _submitReports() {
        val reports =
            this._content.reports.subList(0, ReportConstant.CDN_DOWNLOAD_REPORT_BATCH_SIZE)

        MeteringAPICreateCDNDownloadMeteringHandler(
            MeteringAPICreateCDNDownloadMeteringContent(reports.map { report ->
                MeteringAPICreateCDNDownloadMeteringContentItem(
                    id = report.id,
                    contentSize = report.contentSize,
                    startTime = report.startTime,
                    elapsedTime = report.elapsedTime,
                    isSuccess = report.isSuccess,
                    isComplete = report.isComplete,
                    swarmURI = report.swarmURI,
                    sourceURI = report.sourceURI,
                    requestURI = report.requestURI,
                    responseCode = report.responseCode,
                    errorMessage = report.errorMessage,
                    algorithmID = report.algorithmID,
                    algorithmVersion = report.algorithmVersion
                )
            })
        ).process()
        this._content.reports = this._content.reports.subList(
            ReportConstant.CDN_DOWNLOAD_REPORT_BATCH_SIZE, this._content.reports.size
        )
        this._content.reportTime = DateTool.now()
    }

}

typealias CDNDownloadReportHandlerContent = CDNDownloadReportDutyContent
typealias CDNDownloadReportHandlerOptions = CDNDownloadReportHandlerContent

class P2PDownloadReportHandler(content: P2PDownloadReportHandlerContent) :
    AbstractFlow<P2PDownloadReportHandlerContent>(content) {
    var index = 0

    override suspend fun process() {
        this._intakeIndex()
        this._injectReports()
        while (this._shouldSubmit()) {
            this._submitReports()
        }

    }

    suspend fun _intakeIndex() {

    }

    suspend fun _injectReports() {

        val records = MetricsStatsHolder.source?.p2p_download_records ?: return
        this._content.reports = records.subList(index, records.size).filter { record ->
            record.contentSize ?: -1 > 0 || record.totalSize ?: -1 == 0
        }.toMutableList()
        index += this._content.reports.size
        this._content.cursor_data = this._content.reports.last()
    }

    fun _shouldSubmit(): Boolean {
        return this._content.isAborted && this._content.reports.size > 0 && (this._content.reports.size >= ReportConstant.P2P_DOWNLOAD_REPORT_BATCH_SIZE || DateTool.now() - this._content.reportTime >= ReportConstant.MAX_DURATION_OF_REPORT_SUBMISSION)
    }

    suspend fun _submitReports() {
        val reports =
            this._content.reports.subList(0, ReportConstant.P2P_DOWNLOAD_REPORT_BATCH_SIZE)
        MeteringAPICreateP2PDownloadMeteringHandler(
            MeteringAPICreateP2PDownloadMeteringContent(reports.map({ report ->
                MeteringAPICreateP2PDownloadMeteringContentItem(
                    peerID = report.peerID,
                    contentSize = report.contentSize,
                    startTime = report.startTime,
                    elapsedTime = report.elapsedTime,
                    isComplete = report.isComplete,
                    swarmURI = report.swarmURI,
                    sourceURI = report.sourceURI,
                    requestURI = report.requestURI,
                    algorithmID = report.algorithmID,
                    algorithmVersion = report.algorithmVersion
                )
            }))
        ).process(
        )
        this._content.reports = this._content.reports.subList(
            ReportConstant.CDN_DOWNLOAD_REPORT_BATCH_SIZE, this._content.reports.size
        )
        this._content.reportTime = DateTool.now()
    }

}

typealias P2PDownloadReportHandlerContent = P2PDownloadReportDutyContent
typealias P2PDownloadReportHandlerOptions = P2PDownloadReportHandlerContent

object ReportSubmitterHolder {

    var instance: ReportSubmitter? = null

}
