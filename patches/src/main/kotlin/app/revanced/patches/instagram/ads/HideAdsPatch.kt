/*
 * SPDX-License-Identifier: GPL-3.0-only
 * Derived from ReVanced's Instagram HideAdsPatch.kt and the shared-validator
 * approach in RishiTheSavior's Instagram patches; adapted for Instagram
 * 425.0.0.47.61.
 */
package app.revanced.patches.instagram.ads

import app.revanced.patcher.extensions.ExternalLabel
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.fieldReference
import app.revanced.patcher.extensions.getInstruction
import app.revanced.patcher.extensions.methodReference
import app.revanced.patcher.extensions.string
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val IG_ZERO_MAIN = "Lcom/instagram/zero/main/IgZeroMain;"
private const val IG_ZERO_MODULE_STATIC = "Lcom/instagram/zero/common/IgZeroModuleStatic;"

@Suppress("unused")
val hideAdsPatch = bytecodePatch(
    name = "Instagram 425 Hide Ads",
    description = "Rejects sponsored content in Instagram 425.0.0.47.61's shared ad validator, with feed and Reels fallbacks.",
) {
    compatibleWith("com.instagram.android")

    apply {
        // The shared validator already has a branch that returns an empty ad result for zero mode.
        // Enter that branch directly so ad eligibility does not depend on the zero-rating flags.
        val validator = adValidatorMethod
        val instructions = validator.implementation?.instructions?.toList()
            ?: throw PatchException("The sponsored-content validator has no implementation.")

        val zeroModeIndex = instructions.indexOfFirst { it.string == "zero_mode" }
        if (zeroModeIndex < 0) throw PatchException("Could not find the zero_mode rejection reason.")

        // In this Instagram build the branch starts by creating the empty reason list.
        val blockBranchIndex = (zeroModeIndex - 1 downTo 0).firstOrNull { index ->
            instructions[index].opcode == Opcode.INVOKE_STATIC &&
                instructions[index].methodReference?.returnType == "Ljava/util/ArrayList;" &&
                instructions.subList(index + 1, zeroModeIndex)
                    .all { it.opcode == Opcode.MOVE_RESULT_OBJECT }
        } ?: throw PatchException("Could not find the start of the zero_mode rejection branch.")

        val zeroMainIndex = (0 until blockBranchIndex).firstOrNull {
            instructions[it].refersTo(IG_ZERO_MAIN)
        } ?: throw PatchException("Could not find the zero-rating check.")
        val checkIndex = (zeroMainIndex downTo 0).firstOrNull { index ->
            instructions[index].opcode == Opcode.SGET_OBJECT &&
                instructions[index].fieldReference?.definingClass == IG_ZERO_MODULE_STATIC
        } ?: throw PatchException("Could not find the start of the zero-rating check.")

        if (instructions.subList(checkIndex, blockBranchIndex)
                .any { it.opcode == Opcode.NEW_INSTANCE }
        ) {
            throw PatchException("Unexpected layout of the sponsored-content validator.")
        }

        validator.addInstructionsWithLabels(
            checkIndex,
            "goto :block_ad",
            ExternalLabel("block_ad", instructions[blockBranchIndex]),
        )

        // Keep ReVanced's existing ad-injector hook.
        val adInjectorMethod = isAdPodAdInjectorMethodOrNull
            ?: insertItemAdInjectorMethodOrNull
            ?: throw PatchException("Could not find the ad injector method.")
        adInjectorMethod.addInstructions(
            0,
            """
                const/4 v0, 0x0
                return v0
            """,
        )

        // Drop contextual-feed media when Instagram's Media.Dtc() ad flag is true.
        val feedMethod = contextualFeedItemMethod
        feedMethod.addInstructionsWithLabels(
            1,
            """
                invoke-virtual {v9}, LX/6lu;->A05()Lcom/instagram/feed/media/Media;
                move-result-object v0
                if-eqz v0, :feed_original
                invoke-virtual {v0}, Lcom/instagram/feed/media/Media;->Dtc()Z
                move-result v0
                if-eqz v0, :feed_original
                const/4 v0, 0x0
                return v0
            """,
            ExternalLabel("feed_original", feedMethod.getInstruction(1)),
        )

        // Skip ad media before it is added to the Reels response model.
        val reelsMethod = reelsResponseMethod
        reelsMethod.addInstructionsWithLabels(
            57,
            """
                invoke-virtual {v8}, Lcom/instagram/feed/media/Media;->Dtc()Z
                move-result v0
                if-eqz v0, :reels_original
                goto :reels_next_item
            """,
            ExternalLabel("reels_original", reelsMethod.getInstruction(57)),
            ExternalLabel("reels_next_item", reelsMethod.getInstruction(51)),
        )
    }
}

private fun Instruction.refersTo(type: String): Boolean {
    val reference = (this as? ReferenceInstruction)?.reference ?: return false
    return (reference as? FieldReference)?.definingClass == type ||
        (reference as? MethodReference)?.returnType == type
}
