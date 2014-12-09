package cn.edu.bjtu.group1024.common.aidl;
import cn.edu.bjtu.group1024.common.aidl.Response;
import cn.edu.bjtu.group1024.common.aidl.Request;
interface IFibonacci{
	Response javaRecursion(in Request request);                   // java²ãµÄµÝ¹é
	Response javaInterative(in Request request);                   // java²ãµÄµü´ú
	Response nativeRecursion(in Request request);              // native²ãµÄµÝ¹é
	Response nativeInterative(in Request request);               // native²ãµÄµü´ú
}