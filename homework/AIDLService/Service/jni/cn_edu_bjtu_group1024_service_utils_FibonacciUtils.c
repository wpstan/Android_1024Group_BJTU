#include<jni.h>

int recursion(int n) {
	if (n == 1 || n == 2) {
		return 1;
	} else {
		return (recursion(n - 1) + recursion(n - 2));
	}
}

jint Java_cn_edu_bjtu_group1024_service_utils_FibonacciUtils_nativeRecursion(
		JNIEnv* env, jclass s, jint n) {
	return recursion(n);
}

jint Java_cn_edu_bjtu_group1024_service_utils_FibonacciUtils_nativeInterative(
		JNIEnv* env, jclass s, jint n) {

	int n1 = 1;
	int n2 = 1;
	int result = 0;
	if (n == 1 || n == 2) {
		return n1;
	}
	int i;
	for (i = 3; i <= n; i++) {
		result = n1 + n2;
		n1 = n2;
		n2 = result;
	}
	return result;
}
