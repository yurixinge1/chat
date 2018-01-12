LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := ChatLibrary
LOCAL_SRC_FILES =: ChatLibrary.cpp
include $(BUILD_SHARED_LIBRARY)
LOCAL_PROGUARD_ENABLED:= disabled
LOCAL_LDLIBS+= -L$(SYSROOT)/usr/lib -llog
