package cn.edu.bjtu.group1024.common.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class Request implements Parcelable {

	public Request() {

	}

	public Request(Parcel parcel) {
		num = parcel.readInt();

	}

	private int num;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(num);
	}

	public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() {

		@Override
		public Request createFromParcel(Parcel source) {
			return new Request(source);
		}

		@Override
		public Request[] newArray(int size) {
			return new Request[size];
		}
	};

}
