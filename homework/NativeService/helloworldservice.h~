#ifndef ANDROID_HELLOWORLDSERVICE_H
#define ANDROID_HELLOWORLDSERVICE_H

#include <binder/IInterface.h>
#include <binder/Parcel.h>

namespace android
{
	class helloworldservice : public BBinder
	{
	public:
		static int Instance();
		helloworldservice();
		virtual ~helloworldservice();
		virtual status_t onTransact(uint32_t, const Parcel&, Parcel*, uint32_t);
	};
}
