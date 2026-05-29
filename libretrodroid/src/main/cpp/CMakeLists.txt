cmake_minimum_required(VERSION 3.13.2)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++17 -Wall")

add_definitions("-DVFS_FRONTEND -DHAVE_STRL")

# ── Dependencies via FetchContent (no submodules needed) ──────────────────────
include(FetchContent)

# Oboe (audio engine)
FetchContent_Declare(
    oboe
    GIT_REPOSITORY https://github.com/google/oboe.git
    GIT_TAG        1.5-stable
    GIT_SHALLOW    TRUE
)
FetchContent_MakeAvailable(oboe)

# libretro-common (VFS, string utils, etc.)
FetchContent_Declare(
    libretro_common
    GIT_REPOSITORY https://github.com/libretro/libretro-common.git
    GIT_TAG        master
    GIT_SHALLOW    TRUE
)
FetchContent_MakeAvailable(libretro_common)

# ── Include dirs ──────────────────────────────────────────────────────────────
include_directories(${libretro_common_SOURCE_DIR}/include)

# ── libretro-common sources ───────────────────────────────────────────────────
set(LIBRETRO_COMMON
    ${libretro_common_SOURCE_DIR}/vfs/vfs_implementation.c
    ${libretro_common_SOURCE_DIR}/string/stdstring.c
    ${libretro_common_SOURCE_DIR}/encodings/encoding_utf.c
    ${libretro_common_SOURCE_DIR}/file/file_path.c
    ${libretro_common_SOURCE_DIR}/time/rtime.c
)

# ── libretrodroid shared library ──────────────────────────────────────────────
add_library(libretrodroid SHARED
    libretrodroidjni.h
    libretrodroidjni.cpp
    libretrodroid.h
    libretrodroid.cpp
    log.h
    core.h
    core.cpp
    video.h
    video.cpp
    immersivemode.h
    immersivemode.cpp
    videolayout.h
    videolayout.cpp
    renderers/renderer.h
    renderers/renderer.cpp
    renderers/es3/es3utils.h
    renderers/es3/es3utils.cpp
    renderers/es3/framebufferrenderer.h
    renderers/es3/framebufferrenderer.cpp
    renderers/es2/imagerendereres2.h
    renderers/es2/imagerendereres2.cpp
    renderers/es3/imagerendereres3.h
    renderers/es3/imagerendereres3.cpp
    audio.h
    audio.cpp
    resamplers/resampler.h
    resamplers/linearresampler.h
    resamplers/linearresampler.cpp
    resamplers/sincresampler.h
    resamplers/sincresampler.cpp
    fpssync.h
    fpssync.cpp
    environment.h
    environment.cpp
    input.h
    input.cpp
    shadermanager.h
    shadermanager.cpp
    rumble.h
    rumble.cpp
    rumblestate.h
    rumblestate.cpp
    utils/javautils.h
    utils/javautils.cpp
    utils/utils.cpp
    utils/utils.h
    utils/jnistring.h
    utils/jnistring.cpp
    utils/libretrodroidexception.h
    utils/libretrodroidexception.cpp
    utils/rect.h
    utils/rect.cpp
    errorcodes.h
    errorcodes.cpp
    vfs/vfs.h
    vfs/vfs.cpp
    vfs/vfsfile.h
    vfs/vfsfile.cpp
    vfs/fdwrapper.h
    vfs/fdwrapper.cpp
    microphone/microphone.h
    microphone/microphone.cpp
    microphone/microphoneinterface.h
    microphone/microphoneinterface.cpp
    ${LIBRETRO_COMMON}
)

# Support 16k pages for Android 15
target_link_options(libretrodroid PRIVATE "-Wl,-z,max-page-size=16384")

target_link_libraries(libretrodroid
    android
    log
    EGL
    oboe
    GLESv3
)
