package cn.edu.bjtu.group1024.service;

import cn.edu.bjtu.group1024.common.aidl.IFibonacci;
import cn.edu.bjtu.group1024.common.aidl.Request;
import cn.edu.bjtu.group1024.common.aidl.Response;
import cn.edu.bjtu.group1024.service.utils.FibonacciUtils;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class FibonacciService extends Service {

	/**
	 * º”‘ÿnative library
	 */
	static {
		System.loadLibrary("nativeFibonacci");
	}

	private final IFibonacci.Stub mBinder = new IFibonacci.Stub() {

		@Override
		public Response javaRecursion(Request request) throws RemoteException {
			long preMillSecond = System.currentTimeMillis();
			int result = FibonacciUtils.javaRecursion(request.getNum());
			long afterMillSecod = System.currentTimeMillis();
			int consumeMill = (int) (afterMillSecod - preMillSecond);

			Response response = new Response();
			response.setmResult(result);
			response.setMillSecond(consumeMill);

			return response;
		}

		@Override
		public Response javaInterative(Request request) throws RemoteException {
			long preMillSecond = System.currentTimeMillis();
			int result = FibonacciUtils.javaInterative(request.getNum());
			long afterMillSecod = System.currentTimeMillis();
			int consumeMill = (int) (afterMillSecod - preMillSecond);

			Response response = new Response();
			response.setmResult(result);
			response.setMillSecond(consumeMill);

			return response;
		}

		@Override
		public Response nativeRecursion(Request request) throws RemoteException {
			long preMillSecond = System.currentTimeMillis();
			int result = FibonacciUtils.nativeRecursion(request.getNum());
			long afterMillSecod = System.currentTimeMillis();
			int consumeMill = (int) (afterMillSecod - preMillSecond);

			Response response = new Response();
			response.setmResult(result);
			response.setMillSecond(consumeMill);

			return response;
		}

		@Override
		public Response nativeInterative(Request request)
				throws RemoteException {
			long preMillSecond = System.currentTimeMillis();
			int result = FibonacciUtils.nativeInterative(request.getNum());
			long afterMillSecod = System.currentTimeMillis();
			int consumeMill = (int) (afterMillSecod - preMillSecond);

			Response response = new Response();
			response.setmResult(result);
			response.setMillSecond(consumeMill);

			return response;
		}

	};

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

}
