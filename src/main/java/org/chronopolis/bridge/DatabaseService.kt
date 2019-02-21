package org.chronopolis.bridge

import org.chronopolis.bridge.config.DbConfig
import org.chronopolis.bridge.db.Tables
import org.chronopolis.bridge.db.tables.records.RestorationRecord
import org.chronopolis.bridge.db.tables.records.SnapshotRecord
import org.chronopolis.bridge.models.BridgeStatus
import org.jooq.impl.DSL

/**
 * Query for a Restoration and a Snapshot; pass along the result(s)
 *
 * @param config The [DbConfig] for connecting to the database
 * @since 1.0
 * @author shake
 */
fun fetchRestorations(config: DbConfig): List<RestoreTuple> {
    val snapshotTable = Tables.SNAPSHOT
    val restoreTable = Tables.RESTORATION

    // what happens if there's an exception?
    return DSL.using(config.url(), config.username(), config.password()).use { ctx ->
        ctx.select().from(restoreTable)
                .join(snapshotTable)
                .on(restoreTable.SNAPSHOT_ID.eq(snapshotTable.ID)
                        .and(restoreTable.STATUS.eq(BridgeStatus.RETRIEVING_FROM_STORAGE.name)))
                .orderBy(restoreTable.ID.asc())
                .fetch()
    }.map { record ->
        val restore = record.into(restoreTable)
        val snapshot = record.into(snapshotTable)
        RestoreTuple(restore, snapshot)
    }
}

/**
 * Encapsulate a [RestorationRecord] and [SnapshotRecord] together... forever... they're meant to be
 * together. Might be something better than what's essentially a Tuple but hey you know.
 *
 * @since 1.0
 * @author shake
 */
data class RestoreTuple(val restore: RestorationRecord, val snapshot: SnapshotRecord)