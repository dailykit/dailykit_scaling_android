LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := serial_port
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	C:\Users\ADMIN\StudioProjects\groctaurant\app\src\main\jni\Android.mk \
	C:\Users\ADMIN\StudioProjects\groctaurant\app\src\main\jni\SerialPort.c \
	C:\Users\ADMIN\StudioProjects\groctaurant\app\src\main\jni\com_groctaurant_groctaurant_Serial_SerialPort.h \
	C:\Users\ADMIN\StudioProjects\groctaurant\app\src\main\jni\SerialPort.c.bak \

LOCAL_C_INCLUDES += C:\Users\ADMIN\StudioProjects\groctaurant\app\src\debug\jni
LOCAL_C_INCLUDES += C:\Users\ADMIN\StudioProjects\groctaurant\app\src\main\jni

include $(BUILD_SHARED_LIBRARY)
