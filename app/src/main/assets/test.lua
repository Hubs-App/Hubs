local activity = ...

activity:t("Test")

--local Thread = luajava.bindClass('java.lang.Thread')
--pcall(Thread.sleep, Thread, 2000)
--activity:finish()

function test()
    activity:t("BBBBB")
end