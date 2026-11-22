# ReVanced Instagram Adblock

An unofficial ReVanced patch for Instagram **425.0.0.47.61** (`com.instagram.android`). The current source and ready-to-import bundle are version **1.0.1**.

Download the patch [Here](https://github.com/Zatore/RevancedInstagramAdblock/blob/main/Instagram-425-Hide-Ads.rvp)

The patch routes Instagram's shared sponsored-content validator through its existing empty-result path and retains focused feed and Reels hooks as fallbacks. The repository owner reports that ads remained blocked after closing and reopening Instagram and switching accounts. Ads using a different code path may still appear.

## Repository contents

- `patches/src/main/kotlin/.../ads/`: patch implementation and method fingerprints.
- `dist/Instagram-425-Hide-Ads-1.0.1.rvp`: ready-to-import ReVanced patch bundle.
- `gradle/`, `gradlew`, and `gradlew.bat`: Gradle wrapper and build setup.
- `CHANGELOG.md`: patch release history.
- `LICENSE`: GPL-3.0 license text.

The Instagram APK is not included. The patch targets the supplied 425.0.0.47.61 build and is not expected to match newer or repackaged versions without updates.

## Build the patch bundle

The project uses the ReVanced Patches Gradle plugin. Run the `:patches:buildAndroid` task from the repository root:

```powershell
.\gradlew.bat :patches:buildAndroid
```

The generated `.rvp` is written to `patches/build/libs/`. Gradle may need GitHub Packages credentials for the ReVanced plugin. Supply them through `githubPackagesUsername` and `githubPackagesPassword` Gradle properties or the `GITHUB_ACTOR` and `GITHUB_TOKEN` environment variables. Keep tokens out of the repository.

## Install in ReVanced Manager

1. Copy the `.rvp` bundle to the phone.
2. In ReVanced Manager, open **Patches**, tap the edit pencil, tap **+**, then choose **Select from storage** and select the bundle.
3. Patch Instagram **425.0.0.47.61** and enable **Instagram 425 Hide Ads**.

The `.rvp` is a patch bundle, not an APK. ReVanced Manager applies it to the matching Instagram APK.

## Attribution

This GPL-3.0 patch includes fallback hooks derived from ReVanced's Instagram `HideAdsPatch.kt` and `Fingerprints.kt`. The shared-validator approach is adapted for Instagram 425 from RishiTheSavior's GPL-3.0 Instagram patch.

- [ReVanced HideAdsPatch.kt](https://gitlab.com/ReVanced/revanced-patches/-/raw/main/patches/src/main/kotlin/app/revanced/patches/instagram/ads/HideAdsPatch.kt)
- [ReVanced Fingerprints.kt](https://gitlab.com/ReVanced/revanced-patches/-/raw/main/patches/src/main/kotlin/app/revanced/patches/instagram/ads/Fingerprints.kt)
- [RishiTheSavior BlockAllAdsPatch.kt](https://github.com/RishiTheSavior/revanced-instagram-patches/blob/main/patches/src/main/kotlin/app/revanced/patches/instagram/ads/BlockAllAdsPatch.kt)
