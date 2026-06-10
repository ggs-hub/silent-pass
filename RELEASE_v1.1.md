# Silent Pass v1.1 - Release

## 🎉 Version 1.1 Release

### 🐛 Critical Bug Fixes

#### 1. Number Matching Overflow (Critical)
- **Problem**: False matches when comparing phone numbers of different lengths
- **Solution**: Added length validation before takeLast() operations
- **Example Fix**: "123" no longer matches "456789012123"
- **Impact**: ✅ Improved call matching accuracy

#### 2. Phone Number Normalization (Medium)
- **Problem**: Incomplete separator handling and country code normalization
- **Solution**: 
  - Added parentheses `()` and brackets `[]` support
  - Country code normalization: +86 → 86, 0086 → 86
  - Minimum length validation (6+ digits)
- **Impact**: ✅ Support for more phone number formats

#### 3. Concurrent Data Access (Medium)
- **Problem**: Race condition between UI save and service read
- **Solution**: Changed SharedPreferences from `apply()` to `commit()`
- **Impact**: ✅ Settings now reliably apply before incoming calls

#### 4. URI Number Extraction (Light)
- **Problem**: Improper handling of different URI formats
- **Solution**: Explicit scheme removal and SIP domain handling
- **Impact**: ✅ Better compatibility with various call sources

### 📊 Version Details
- **versionCode**: 1 → 2
- **versionName**: 1.0 → 1.1
- **Target SDK**: 36
- **Min SDK**: 36
- **Language**: Kotlin 100%

### 📝 Modified Files
- `app/src/main/java/com/islate/silentpass/data/ContactRingStore.kt`
- `app/src/main/java/com/islate/silentpass/service/SilentCallScreeningService.kt`
- `app/build.gradle.kts`

### ✅ Testing
- ✅ Various phone number formats (separators, country codes)
- ✅ Save-to-ring timing
- ✅ Concurrent access scenarios
- ✅ Different URI schemes (tel:, sip:)
- ✅ No regressions on existing features

### 🚀 Installation
```bash
# Debug
adb install -r app-debug.apk

# Release
adb install -r app-release.apk

# Launch
adb shell am start -n com.islate.silentpass/.MainActivity
```

### 📥 Downloads
- Debug APK: Available on Release page
- Release APK: Available on Release page
