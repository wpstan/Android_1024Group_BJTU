1024Group
=========

###homwwork
#### .NativeProgram
> 1.download aosp，build the environment.
> 2.source build/envsetup.sh
> 3.lunch (choose one type)
> 4.make -j4
> 5.waiting......
> 6.emulator
> 7.write a "c" file in aosp's extenals dir
> 8.cd the dir, and run "mm" command to compile
> 9.if you want to compile again ,and you need delete this module, use this command in aosp root dir. "make > clean-module's name"
> 10.adb root, adb remount, adb push (executors module) /data/
> 11.adb shell and cd to /data/, and then type this command: "./(executors module)" to run it
> 12.done!
