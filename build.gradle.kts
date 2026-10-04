
plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

listOf("runClient", "runClient17", "runClient21", "runClient25").forEach { taskName ->
    tasks.named<JavaExec>(taskName) {
        val filteredMessages = listOf(
            "WARNING: package sun.lwawt.macosx not in java.desktop",
            "WARNING: A terminally deprecated method in sun.misc.Unsafe has been called",
            "WARNING: sun.misc.Unsafe::arrayBaseOffset has been called by makamys.coretweaks.repackage.com.esotericsoftware.kryo.kryo5.unsafe.UnsafeUtil (file:/home/evgenwargold/.gradle/caches/modules-2/files-2.1/com.github.GTNewHorizons/CoreTweaks/0.3.3.8-GTNH/24ac6cf2e568be337bdd4d2ddaa6013fdbbef272/CoreTweaks-0.3.3.8-GTNH-dev.jar)",
            "Please consider reporting this to the maintainers of class makamys.coretweaks.repackage.com.esotericsoftware.kryo.kryo5.unsafe.UnsafeUtil",
            "WARNING: sun.misc.Unsafe::arrayBaseOffset will be removed in a future release"
        )

        standardOutput = object : java.io.OutputStream() {
            private val buffer = StringBuilder()

            override fun write(b: Int) {
                buffer.append(b.toChar())
                if (b.toChar() == '\n') {
                    val line = buffer.toString()

                    val shouldFilter = filteredMessages.any { line.contains(it) }

                    if (!shouldFilter) {
                        System.out.print(line)
                    }

                    buffer.setLength(0)
                }
            }
        }

        errorOutput = standardOutput
    }
}
