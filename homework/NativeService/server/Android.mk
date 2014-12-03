LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

# our own branch needs these headers
LOCAL_C_INCLUDES +=

LOCAL_PRELINK_MODULE := false

LOCAL_LDLIBS := -llog
LOCAL_SHARED_LIBRARIES := \
    liblog \
    libcutils \
    libutils \
    libbinder \
    libhelloworldservice
    
LOCAL_SRC_FILES := helloworldserver.cpp
LOCAL_MODULE := helloworldserver
include $(BUILD_EXECUTABLE)
