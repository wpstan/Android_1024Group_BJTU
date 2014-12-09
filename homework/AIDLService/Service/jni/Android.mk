LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE	:= nativeFibonacci
LOCAL_SRC_FILES	:= cn_edu_bjtu_group1024_service_utils_FibonacciUtils.c
include	$(BUILD_SHARED_LIBRARY)
