/*
 * Copyright 2009-2011 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>

#include "com_groctaurant_groctaurant_Serial_SerialPort.h"
#include "android/log.h"

static const char *TAG="serial_port";	//????????????Android???APP???


#define GET_DSR3  		0x123 //_IOC(_IOC_READ,0x1f,0x1f,0xc49)
#define SET_DTR  			0x124 //_IOC(_IOC_WRITE,0x1f,0x1f,0xc49)
#define SET_DTR_HIGH  0x125 //_IOC(_IOC_WRITE,0x1f,0x1f,0xc50)
#define SET_DTR_LOW		0x126 //_IOC(_IOC_WRITE,0x1f,0x1f,0xc51)

#define	GET_UART_1_CTS    		 0x101		//??????1??CTS
#define	SET_UART_1_RTS_HIGH    0x102		//???�???1??RTS???
#define	SET_UART_1_RTS_LOW 		 0x103		//???�???1??RTS???
#define	GET_UART_2_CTS    		 0x201
#define	SET_UART_2_RTS_HIGH    0x202
#define	SET_UART_2_RTS_LOW 		 0x203
#define	GET_UART_3_CTS    		 0x301
#define	SET_UART_3_RTS_HIGH    0x302
#define	SET_UART_3_RTS_LOW 		 0x303
#define	GET_UART_4_CTS    		 0x401
#define	SET_UART_4_RTS_HIGH    0x402
#define	SET_UART_4_RTS_LOW 		 0x403
#define	GET_UART_5_CTS    		 0x501
#define	SET_UART_5_RTS_HIGH    0x502
#define	SET_UART_5_RTS_LOW 		 0x503



#define APP_GET_DSR3  		1
#define APP_SET_DTR  			2
#define APP_SET_DTR_HIGH  3
#define APP_SET_DTR_LOW		4

#define APP_GET_1_CTS  					11
#define APP_SET_1_RTS_HIGH  		12
#define APP_SET_1_RTS_LOW	  		13
#define APP_GET_2_CTS  					21
#define APP_SET_2_RTS_HIGH  		22
#define APP_SET_2_RTS_LOW	  		23
#define APP_GET_3_CTS  					31
#define APP_SET_3_RTS_HIGH  		32
#define APP_SET_3_RTS_LOW	  		33
#define APP_GET_4_CTS  					41
#define APP_SET_4_RTS_HIGH  		42
#define APP_SET_4_RTS_LOW	  		43
#define APP_GET_5_CTS  					51
#define APP_SET_5_RTS_HIGH  		52
#define APP_SET_5_RTS_LOW	  		53


int fd = 0;
static speed_t getBaudrate(jint baudrate)
{
    switch(baudrate) {
        case 0: return B0;
        case 50: return B50;
        case 75: return B75;
        case 110: return B110;
        case 134: return B134;
        case 150: return B150;
        case 200: return B200;
        case 300: return B300;
        case 600: return B600;
        case 1200: return B1200;
        case 1800: return B1800;
        case 2400: return B2400;
        case 4800: return B4800;
        case 9600: return B9600;
        case 19200: return B19200;
        case 38400: return B38400;
        case 57600: return B57600;
        case 115200: return B115200;
        case 230400: return B230400;
        case 460800: return B460800;
        case 500000: return B500000;
        case 576000: return B576000;
        case 921600: return B921600;
        case 1000000: return B1000000;
        case 1152000: return B1152000;
        case 1500000: return B1500000;
        case 2000000: return B2000000;
        case 2500000: return B2500000;
        case 3000000: return B3000000;
        case 3500000: return B3500000;
        case 4000000: return B4000000;
        default: return -1;
    }
}
/*
 * Class:     android_serialport_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_com_groctaurant_groctaurant_Serial_SerialPort_open
        (JNIEnv *env, jclass thiz, jstring path, jint baudrate, jint flags)
{
//	int fd;
        speed_t speed;
        jobject mFileDescriptor;

        ;

        /* Check arguments */
        {
            speed = getBaudrate(baudrate);
            if (speed == -1) {
                /* TODO: throw an exception */

                return NULL;
            }
        }

        /* Opening device */
        {
            jboolean iscopy;
            const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);

            fd = open(path_utf, O_RDWR | flags);


            (*env)->ReleaseStringUTFChars(env, path, path_utf);
            if (fd == -1)
            {
                /* Throw an exception */

                /* TODO: throw an exception */
                return NULL;
            }
        }

        /* Configure device */
        {
            struct termios cfg;

            if (tcgetattr(fd, &cfg))
            {

                close(fd);
                /* TODO: throw an exception */
                return NULL;
            }

            cfmakeraw(&cfg);
            cfsetispeed(&cfg, speed);
            cfsetospeed(&cfg, speed);

            if (tcsetattr(fd, TCSANOW, &cfg))
            {

                close(fd);
                /* TODO: throw an exception */
                return NULL;
            }


        }

        /* Create a corresponding file descriptor */
        {
            jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
            jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor, "<init>", "()V");
            jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
            mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
            (*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint)fd);
        }

        return mFileDescriptor;
    }

JNIEXPORT void JNICALL Java_com_groctaurant_groctaurant_Serial_SerialPort_close
        (JNIEnv *env, jobject thiz)
{
    jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
    jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

    jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
    jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");

    jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
    jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);


    close(descriptor);
}

JNIEXPORT jint JNICALL Java_com_groctaurant_groctaurant_Serial_SerialPort_IOCTLVIB
        (JNIEnv *env, jobject  thiz, jint controlcode)
{
    //??Android???????controlcode ??????????CTLCODE????
    int CTLCODE = controlcode;
    int value = 0;

    switch(CTLCODE)
    {
        case APP_GET_DSR3:
        {

            value = ioctl(fd,GET_DSR3,GET_DSR3);//???????????????ioctrl??????????VIB_ON?????????????????
//           __android_log_print(ANDROID_LOG_INFO, "tty", "fd  -----   %d",fd);
//           __android_log_print(ANDROID_LOG_INFO, "tty", "size int -----   GET_DSR3");
            break;
        }
        case APP_SET_DTR:
        {
            value = ioctl(fd,SET_DTR,SET_DTR);//???????????????ioctrl??????????VIB_OFF?????????????????
//           __android_log_print(ANDROID_LOG_INFO, "tty", "fd  -----   %d",fd);
//           __android_log_print(ANDROID_LOG_INFO, "tty", "size int -----   SET_DTR");
            break;
        }
        case APP_SET_DTR_HIGH:
        {
            ioctl(fd,SET_DTR_HIGH,SET_DTR_HIGH);
//            __android_log_print(ANDROID_LOG_INFO, "tty", "fd  -----   %d",fd);
//            __android_log_print(ANDROID_LOG_INFO, "tty", "size int -----   SET_DTR_HIGH");
            break;
        }
        case APP_SET_DTR_LOW:
        {
            ioctl(fd,SET_DTR_LOW,SET_DTR_LOW);
//           __android_log_print(ANDROID_LOG_INFO, "tty", "fd  -----   %d",fd);
//           __android_log_print(ANDROID_LOG_INFO, "tty", "size int -----   SET_DTR_LOW");
            break;
        }



        case APP_GET_1_CTS:
        {

            value = ioctl(fd,GET_UART_1_CTS,GET_UART_1_CTS);

            break;
        }
        case APP_SET_1_RTS_HIGH:
        {
            ioctl(fd,SET_UART_1_RTS_HIGH,SET_UART_1_RTS_HIGH);

            break;
        }
        case APP_SET_1_RTS_LOW:
        {
            ioctl(fd,SET_UART_1_RTS_LOW,SET_UART_1_RTS_LOW);

            break;
        }

        case APP_GET_2_CTS:
        {


            value = ioctl(fd,GET_UART_2_CTS,GET_UART_2_CTS);
            break;
        }
        case APP_SET_2_RTS_HIGH:
        {
            ioctl(fd,SET_UART_2_RTS_HIGH,SET_UART_2_RTS_HIGH);


            break;
        }
        case APP_SET_2_RTS_LOW:
        {
            ioctl(fd,SET_UART_2_RTS_LOW,SET_UART_2_RTS_LOW);

            break;
        }

        case APP_GET_3_CTS:
        {

            value = ioctl(fd,GET_UART_3_CTS,GET_UART_3_CTS);
            break;
        }
        case APP_SET_3_RTS_HIGH:
        {
            ioctl(fd,SET_UART_3_RTS_HIGH,SET_UART_3_RTS_HIGH);

            break;
        }
        case APP_SET_3_RTS_LOW:
        {
            ioctl(fd,SET_UART_3_RTS_LOW,SET_UART_3_RTS_LOW);

            break;
        }

        case APP_GET_4_CTS:
        {

            value = ioctl(fd,GET_UART_4_CTS,GET_UART_4_CTS);
            break;
        }
        case APP_SET_4_RTS_HIGH:
        {
            ioctl(fd,SET_UART_4_RTS_HIGH,SET_UART_4_RTS_HIGH);

            break;
        }
        case APP_SET_4_RTS_LOW:
        {
            ioctl(fd,SET_UART_4_RTS_LOW,SET_UART_4_RTS_LOW);

            break;
        }

        case APP_GET_5_CTS:
        {

            value = ioctl(fd,GET_UART_5_CTS,GET_UART_5_CTS);
            break;
        }
        case APP_SET_5_RTS_HIGH:
        {
            ioctl(fd,SET_UART_5_RTS_HIGH,SET_UART_5_RTS_HIGH);

            break;
        }
        case APP_SET_5_RTS_LOW:
        {
            ioctl(fd,SET_UART_5_RTS_LOW,SET_UART_5_RTS_LOW);

            break;
        }


        default:
            break;
    }
    return value;
}
