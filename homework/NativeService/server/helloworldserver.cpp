#include <stdio.h>
#include <sys/types.h>  
#include <unistd.h>  
#include <grp.h>  
#include <binder/IPCThreadState.h>
#include <binder/ProcessState.h>
#include <binder/IServiceManager.h>
#include "../service/helloworldservice.h"

using namespace android;

int main(int arg, char** argv)
{
    	sp<ProcessState> proc(ProcessState::self());  
    	sp<IServiceManager> sm = defaultServiceManager();  
   	helloworldservice::Instance();   
   	ProcessState::self()->startThreadPool(); 
	IPCThreadState::self()->joinThreadPool();
}
