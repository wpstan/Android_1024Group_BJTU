BJTU-1024Group
=========

###homework
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

#### .NativeService
> 1.mkdir service server client <br>
> 2.write cpp and Android.mk in these dir <br>
> 3.compile service use "mm" command and get the result .so <br>
> 4.compile server  use "mm" command and get the result executor file <br>
> 5.compile client  use "mm" command and get the result executor file <br>
> 6.adb root, adb remount ,adb push .so to /system/lib/ (warn: this .so must push in /system/lib/ dir, so the server can find it, or the server cannot run normally). And the server, client executor files are pushed to /data/ dir. <br>
> 7.run the server first <br>
> 8.run client <br>
> 9.we will get a print "Hello World by 1024 Group" <br>
> 10.done! <br>
