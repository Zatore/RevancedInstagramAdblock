/*
 * SPDX-License-Identifier: GPL-3.0-only
 * Instagram 425.0.0.47.61 fingerprints added for the extended Hide ads patch.
 */
package app.revanced.patches.instagram.ads

import app.revanced.patcher.definingClass
import app.revanced.patcher.gettingFirstMethodDeclaratively
import app.revanced.patcher.parameterTypes
import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.returnType

internal val BytecodePatchContext.adValidatorMethod by gettingFirstMethodDeclaratively(
    "zero_mode",
) {
    definingClass("LX/BSC;")
    returnType("LX/1i6;")
}

internal val BytecodePatchContext.contextualFeedItemMethod by gettingFirstMethodDeclaratively(
    "feed_contextual_self_profile",
) {
    definingClass("LX/7Am;")
    returnType("Z")
    parameterTypes("Ljava/lang/Object;")
}

internal val BytecodePatchContext.reelsResponseMethod by gettingFirstMethodDeclaratively(
    "from: ReelAdsAndNetegoResponseHandler, clientDict: ",
) {
    definingClass("LX/6rB;")
    returnType("LX/5vK;")
    parameterTypes("LX/4tl;", "I", "Z", "Z", "Z")
}
