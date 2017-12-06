local client = ...

local Request = luajava.bindClass('okhttp3.Request')


function get(url)
    local request = Request.Builder.new():url(url):build()
    local response = client:newCall(request):execute()
    return response:body():string()
end