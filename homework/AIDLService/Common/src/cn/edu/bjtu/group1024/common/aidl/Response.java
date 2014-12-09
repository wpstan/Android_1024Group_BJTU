package cn.edu.bjtu.group1024.common.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class Response implements Parcelable {

	public Response() {

	}

	public Response(Parcel parcel) {
		millSecond = parcel.readInt();
		mResult = parcel.readInt();
	}

	private int millSecond;
	private int mResult;

	public int getMillSecond() {
		return millSecond;
	}

	public void setMillSecond(int millSecond) {
		this.millSecond = millSecond;
	}

	public int getmResult() {
		return mResult;
	}

	public void setmResult(int mResult) {
		this.mResult = mResult;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(millSecond);
		dest.writeInt(mResult);
	}

	public static final Parcelable.Creator<Response> CREATOR = new Creator<Response>() {

		@Override
		public Response createFromParcel(Parcel source) {
			return new Response(source);
		}

		@Override
		public Response[] newArray(int size) {
			return new Response[size];
		}
	};

}
