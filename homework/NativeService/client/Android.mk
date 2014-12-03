LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_C_INCLUDES +=

LOCAL_PRELINK_MODULE := false

LOCAL_LDLIBS := -llog
LOCAL_SHARED_LIBRARIES := \
    liblog \
    libcutils \
    libutils \
    libbinder \
    libhelloworldservice
    
LOCAL_SRC_FILES := helloworldclient.cpp
LOCAL_MODULE := helloworldclient
include $(BUILD_EXECUTABLE)
