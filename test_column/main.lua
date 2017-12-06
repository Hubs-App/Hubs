assert(loadfile("okhttp.lua"))(...)

local Article = luajava.bindClass('cn.nekocode.hubs.data.model.Article')
local ArrayList = luajava.bindClass('java.util.ArrayList')
local JSONObject = luajava.bindClass('org.json.JSONObject')


function getItems(page)
    local articleList = ArrayList.new()
    local url = "http://i.jandan.net/?oxwlxojflwblxbsapi=get_recent_post" ..
            "&include=url,date,tags,author,title,excerpt,comment_count,comment_status,custom_fields" ..
            "&custom_fields=thumb_c,views&page=" .. page + 1

    local json = JSONObject.new(get(url))
    local count = json:getInt("count")
    local posts = json:getJSONArray("posts")

    for i = 0, count-1 do
        local post = posts:getJSONObject(i)
        local coverUrl = post:getJSONObject("custom_fields"):getJSONArray("thumb_c"):getString(0)

        local article = Article.new()
        article:setUrl(post:getString("url"))
        article:setCoverUrl(coverUrl)
        article:setTitleHtml(post:getString("title"))
        article:setDescriptionHtml(post:getString("excerpt"))
        articleList:add(article)
    end

    return articleList
end