1024Group
=========

###homwwork
#### .NativeProgram
> 1.download aosp，build the environment. <br>
> 2.source build/envsetup.sh <br>
> 3.lunch (choose one type) <br>
> 4.make -j4 <br>
> 5.waiting...... <br>
> 6.emulator <br>
> 7.write a "c" file in aosp's extenals dir <br>
> 8.cd the dir, and run "mm" command to compile <br>
> 9.if you want to compile again ,and you need delete this module, use this command in aosp root dir. "make > clean-module's name" <br>
> 10.adb root, adb remount, adb push (executors module) /data/ <br>
> 11.adb shell and cd to /data/, and then type this command: "./(executors module)" to run it <br>
> 12.done! <br>
