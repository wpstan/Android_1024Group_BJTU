#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include "helloworldservice.h"

namespace android
{
	static pthread_key_t sigbuskey;

	int helloworldservice::Instance()
	{
		LOGE("helloworldservice Instantiate\n");
		int ret = defaultServiceManager()->addService(String16("Bjtu1024Group"), new helloworldservice());
		LOGE("helloworldservice ret = %d\n", ret);
		return ret;
	}
	helloworldservice::helloworldservice()
	{
		LOGV("helloworldservice create \n");
		pthread_key_create(&sigbuskey, NULL);
	}
	helloworldservice::~helloworldservice()
	{
		pthread_key_delete(sigbuskey);
		LOGV("helloworldservice destory\n");
	}

	status_t helloworldservice::onTransact(uint32_t code, const Parcel& data, Parcel* reply, uint32_t flags)
	{
		reply->writeString("Hello World! by 1024 group");
		return NO_ERROR;
	}
}
