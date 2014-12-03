#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include "helloworldclient.h"


namespace android
{
	sp<IBinder> binder;
        
        void helloworldclient::show()
        {
            getHelloWorldService();
            Parcel data, reply;
            data.writeInt32(getpid());
            
            binder->transact(0, data, &reply);
            printf("%s",reply.readCString());
        }

        void helloworldclient::getHelloWorldService()
        {
            sp<IServiceManager> sm = defaultServiceManager();
            binder = sm->getService(String16("Bjtu1024Group"));
            if(binder == 0)
            {
                return;
            }
        }

};

using namespace android;  
  
int main(int argc, char** argv)  
{  
    helloworldclient* p = new helloworldclient();  
    p->show();
    return 0;  
}  
